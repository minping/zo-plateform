package com.zo.platform.starter.mq;

import com.zo.platform.starter.log.ZoLog;
import org.apache.rocketmq.common.message.MessageExt;

/**
 * @author zhoump
 * @date 2026/11/25
 * @purpose
 */
public class DeadLetterNotifier implements MqDeadLetterHandler{

    /**
     * 处理死信消息
     *
     * @param consumerGroup 原消费者组
     * @param topic         原主题
     * @param messageExt    死信消息详情
     */
    @Override
    public void handle(String consumerGroup, String topic, MessageExt messageExt) {
        // 自定义告警逻辑：钉钉机器人推送、邮件、落库等
        String content = String.format(
                "【RocketMQ死信告警】\n消费者组：%s\n主题：%s\n消息ID：%s\n重试次数：%d",
                consumerGroup, topic, messageExt.getMsgId(), messageExt.getReconsumeTimes()
        );
        ZoLog.debug("监听到死信队列:" + content);
    }
}
