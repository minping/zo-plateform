package com.zo.platform.starter.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;

import java.nio.charset.StandardCharsets;

/**
 * @author zhoump
 * @date 2026/11/25
 * @purpose
 */
public class MqSender {

    private final DefaultMQProducer producer;
    private final ObjectMapper objectMapper;

    private static final String SCHEDULED_DELIVER_TIME = "__STARTDELIVERTIME";

    public MqSender(DefaultMQProducer producer, ObjectMapper objectMapper) {
        this.producer = producer;
        this.objectMapper = objectMapper;
    }

    /**
     * 同步发送普通消息
     */
    public SendResult send(String topic, String tag, Object body) {
        try {
            Message msg = buildMessage(topic, tag, body);
            return producer.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("RocketMQ 同步发送失败", e);
        }
    }

    /**
     * 异步发送消息
     */
    public void sendAsync(String topic, String tag, Object body, SendCallback callback) {
        try {
            Message msg = buildMessage(topic, tag, body);
            producer.send(msg, callback);
        } catch (Exception e) {
            throw new RuntimeException("RocketMQ 异步发送失败", e);
        }
    }

    /**
     * 单向发送（日志/埋点场景，不关心结果）
     */
    public void sendOneWay(String topic, String tag, Object body) {
        try {
            Message msg = buildMessage(topic, tag, body);
            producer.sendOneway(msg);
        } catch (Exception e) {
            throw new RuntimeException("RocketMQ 单向发送失败", e);
        }
    }

    /**
     * 延迟消息（4.x 档位模式）
     * @param delayLevel 延迟等级 1~18
     */
    public SendResult sendDelay(String topic, String tag, Object body, DelayLevel delayLevel) {
        try {
            Message msg = buildMessage(topic, tag, body);
            msg.setDelayTimeLevel(delayLevel.level);
            return producer.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("RocketMQ 延迟消息发送失败", e);
        }
    }

    /**
     * 定时消息（5.x 任意时间点，毫秒时间戳）
     * @param deliverTimestamp 投递时间戳（毫秒）
     */
    public SendResult sendScheduled(String topic, String tag, Object body, long deliverTimestamp) {
        try {
            Message msg = buildMessage(topic, tag, body);
            msg.putUserProperty(SCHEDULED_DELIVER_TIME, String.valueOf(deliverTimestamp));
            return producer.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("RocketMQ 定时消息发送失败", e);
        }
    }

    /**
     * 顺序消息（按 hashKey 投递到同一队列）
     */
    public SendResult sendOrderly(String topic, String tag, Object body, String hashKey) {
        try {
            Message msg = buildMessage(topic, tag, body);
            return producer.send(msg, (mqs, msg1, arg) -> {
                int index = Math.abs(arg.hashCode()) % mqs.size();
                return mqs.get(index);
            }, hashKey);
        } catch (Exception e) {
            throw new RuntimeException("RocketMQ 顺序消息发送失败", e);
        }
    }

    /**
     * 构建通用消息对象
     */
    private Message buildMessage(String topic, String tag, Object body) throws Exception {
        String json = objectMapper.writeValueAsString(body);
        return new Message(topic, tag, json.getBytes(StandardCharsets.UTF_8));
    }
}
