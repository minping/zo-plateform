package com.zo.platform.starter.mq;

import org.apache.rocketmq.remoting.protocol.heartbeat.MessageModel;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface MqConsumer {

    /** 订阅主题 */
    String topic();

    /** 订阅标签，默认全部 */
    String tag() default "*";

    /** 是否开启死信队列自动监听，默认开启 */
    boolean enableDeadLetter() default true;

    /** 消费者组，不填默认 topic + "-consumer-group" */
    String consumerGroup() default "";

    /** 消费模式：集群/广播 */
    MessageModel messageModel() default MessageModel.CLUSTERING;

    /** 并发消费线程数 */
    int consumeThreadNumber() default 4;

    /** 单实例最大重试次数，-1 走全局配置 */
    int maxReconsumeTimes() default -1;
}