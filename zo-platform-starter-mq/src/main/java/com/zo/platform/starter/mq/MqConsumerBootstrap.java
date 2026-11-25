package com.zo.platform.starter.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zo.platform.starter.mq.config.MqProperties;
import com.zo.platform.starter.redis.RedisUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.*;
import org.springframework.core.GenericTypeResolver;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zhoump
 * @date 2026/11/25
 * @purpose
 */
public class MqConsumerBootstrap implements ApplicationContextAware, SmartInitializingSingleton, DisposableBean {

    private static final String IDEMPOTENT_KEY_PREFIX = "mq:idempotent:";
    private static final Logger log = LoggerFactory.getLogger(MqConsumerBootstrap.class);
    private ApplicationContext applicationContext;
    private final MqProperties properties;
    private final ObjectMapper objectMapper;
    private final RedisUtil redisUtil;
    /** 维护所有已启动的消费者实例，用于停机 */
    private final List<DefaultMQPushConsumer> consumers = new ArrayList<>();
    // 新增：注入所有死信处理器
    private final List<MqDeadLetterHandler> deadLetterHandlers;

    public MqConsumerBootstrap(MqProperties properties, ObjectMapper objectMapper,RedisUtil redisUtil,List<MqDeadLetterHandler> deadLetterHandlers) {
        this.properties = properties;
        this.objectMapper = objectMapper;
        this.redisUtil = redisUtil;
        this.deadLetterHandlers = deadLetterHandlers != null ? deadLetterHandlers : Collections.emptyList();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(MqConsumer.class);
        log.info("扫描到 MqConsumer 消费者数量：{}", beans.size());

        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object bean = entry.getValue();
            MqConsumer annotation = bean.getClass().getAnnotation(MqConsumer.class);
            if (!(bean instanceof MqMessageHandler)) {
                throw new IllegalStateException("@RocketMQConsumer 必须实现 RocketMQMessageHandler 接口");
            }
            createConsumer(annotation, (MqMessageHandler<?>) bean);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void createConsumer(MqConsumer annotation, MqMessageHandler handler) {
        try {
            String consumerGroup = annotation.consumerGroup().isEmpty()
                    ? annotation.topic() + "-consumer-group"
                    : annotation.consumerGroup();

            DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(consumerGroup);
            consumer.setNamesrvAddr(properties.getNameServer());
            consumer.subscribe(annotation.topic(), annotation.tag());
            consumer.setMessageModel(annotation.messageModel());
            consumer.setConsumeThreadMin(annotation.consumeThreadNumber());
            consumer.setConsumeThreadMax(annotation.consumeThreadNumber());

            int retryTimes = annotation.maxReconsumeTimes() == -1
                    ? properties.getMaxReconsumeTimes()
                    : annotation.maxReconsumeTimes();
            consumer.setMaxReconsumeTimes(retryTimes);

            // 解析泛型，获取消息体实际类型
            Class<?> bodyClass = GenericTypeResolver.resolveTypeArgument(
                    handler.getClass(), MqMessageHandler.class);

            // 注册消息监听器
            consumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                for (MessageExt msg : msgs) {
                    // 幂等校验
                    if (properties.isIdempotent() && !checkIdempotent(msg.getMsgId())) {
                        continue;
                    }

                    try {
                        String bodyStr = new String(msg.getBody(), StandardCharsets.UTF_8);
                        Object body = objectMapper.readValue(bodyStr, bodyClass);
                        //boolean success = handler.handle(body, msg, context);
                        boolean success = handler.handle(body);
                        if (!success) {
                            return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                        }
                    } catch (Exception e) {
                        log.error("消息消费异常，msgId={}", msg.getMsgId(), e);
                        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
            // 开启死信监听则创建对应死信消费者
            if (annotation.enableDeadLetter()) {
                createDeadLetterConsumer(consumerGroup, annotation.topic());
            }
            consumer.start();
            // 加入实例列表，用于后续关闭
            consumers.add(consumer);
            log.info("RocketMQ消费者启动成功：group={}, topic={}, tag={}",
                    consumerGroup, annotation.topic(), annotation.tag());
        } catch (Exception e) {
            throw new RuntimeException("RocketMQ 消费者启动失败", e);
        }
    }

    /**
     * 创建死信队列消费者
     */
    private void createDeadLetterConsumer(String originalConsumerGroup, String originalTopic) {
        try {
            String dlqTopic = "%DLQ%" + originalConsumerGroup;
            String dlqConsumerGroup = originalConsumerGroup + "-dlq-group";

            DefaultMQPushConsumer dlqConsumer = new DefaultMQPushConsumer(dlqConsumerGroup);
            dlqConsumer.setNamesrvAddr(properties.getNameServer());
            dlqConsumer.subscribe(dlqTopic, "*");
            dlqConsumer.setConsumeThreadMin(1);
            dlqConsumer.setConsumeThreadMax(2);

            dlqConsumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                for (MessageExt msg : msgs) {
                    log.error("收到死信消息，原消费者组={}，原主题={}，msgId={}",
                            originalConsumerGroup, originalTopic, msg.getMsgId());
                    // 调用所有自定义死信处理器
                    for (MqDeadLetterHandler handler : deadLetterHandlers) {
                        try {
                            handler.handle(originalConsumerGroup, originalTopic, msg);
                        } catch (Exception e) {
                            log.error("死信处理器执行异常", e);
                        }
                    }
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });

            dlqConsumer.start();
            consumers.add(dlqConsumer); // 加入统一优雅停机管理
            log.info("死信队列消费者启动成功：group={}，topic={}", dlqConsumerGroup, dlqTopic);
        } catch (Exception e) {
            log.error("死信队列消费者创建失败", e);
        }
    }

    /**
     * Redis 幂等校验：true=首次消费，false=重复消费
     */
    private boolean checkIdempotent(String msgId) {
        String key = IDEMPOTENT_KEY_PREFIX + msgId;
        Boolean absent = redisUtil.setIfAbsent(key, "1", properties.getIdempotentExpireSeconds(), TimeUnit.SECONDS);
        return Boolean.TRUE.equals(absent);
    }

    /**
     * Spring 容器销毁时，自动触发优雅关闭消费者
     */
    @Override
    public void destroy() {
        log.info("开始关闭RocketMQ消费者，总数：{}", consumers.size());
        // 逆序关闭，后启动的先关闭
        Collections.reverse(consumers);

        for (DefaultMQPushConsumer consumer : consumers) {
            String group = consumer.getConsumerGroup();
            try {
                log.info("正在关闭消费者：{}", group);
                // shutdown 内部会停止拉取新消息、等待消费线程完成、提交消费进度、释放连接
                consumer.shutdown();
                log.info("消费者[{}]关闭成功", group);
            } catch (Exception e) {
                log.error("消费者[{}]关闭异常", group, e);
            }
        }
        log.info("RocketMQ 所有消费者关闭完成");
    }
}
