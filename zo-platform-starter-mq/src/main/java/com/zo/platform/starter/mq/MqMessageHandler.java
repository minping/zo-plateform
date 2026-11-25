package com.zo.platform.starter.mq;

/**
 * 消息消费处理器，业务类实现此接口即可
 * @param <T> 消息体反序列化类型
 */
public interface MqMessageHandler<T> {

    /**
     * 消费消息业务逻辑
     * @param body 反序列化后的消息体
     * @param messageExt 原始消息对象
     * @param context 消费上下文
     * @return true=消费成功；false=消费失败，进入重试
     */
    boolean handle(T body);
}
