package com.zo.platform.starter.mq;

import org.apache.rocketmq.common.message.MessageExt;

/**
 * @author zhoump
 * @date 2026/11/25
 * @purpose 死信消息处理器，业务侧实现此接口可自定义死信告警逻辑
 */
public interface MqDeadLetterHandler {
    /**
     * 处理死信消息
     * @param consumerGroup 原消费者组
     * @param topic 原主题
     * @param messageExt 死信消息详情
     */
    void handle(String consumerGroup, String topic, MessageExt messageExt);
}
