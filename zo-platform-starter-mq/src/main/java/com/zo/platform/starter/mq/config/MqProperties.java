package com.zo.platform.starter.mq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhoump
 * @date 2026/11/25
 * @purpose
 */
@ConfigurationProperties(prefix = "zo.rocketmq")
public class MqProperties {

    /** NameServer 地址 */
    private String nameServer;

    /** 生产者组名，默认取应用名 */
    private String producerGroup;

    /** 消息发送超时时间，毫秒 */
    private int sendTimeout = 3000;

    /** 消费失败最大重试次数 */
    private int maxReconsumeTimes = 3;

    /** 是否开启 Redis 幂等校验 */
    private boolean idempotent = true;

    /** 幂等 key 过期时间，秒（默认24小时） */
    private long idempotentExpireSeconds = 86400;

    // getter / setter
    public String getNameServer() { return nameServer; }
    public void setNameServer(String nameServer) { this.nameServer = nameServer; }
    public String getProducerGroup() { return producerGroup; }
    public void setProducerGroup(String producerGroup) { this.producerGroup = producerGroup; }
    public int getSendTimeout() { return sendTimeout; }
    public void setSendTimeout(int sendTimeout) { this.sendTimeout = sendTimeout; }
    public int getMaxReconsumeTimes() { return maxReconsumeTimes; }
    public void setMaxReconsumeTimes(int maxReconsumeTimes) { this.maxReconsumeTimes = maxReconsumeTimes; }
    public boolean isIdempotent() { return idempotent; }
    public void setIdempotent(boolean idempotent) { this.idempotent = idempotent; }
    public long getIdempotentExpireSeconds() { return idempotentExpireSeconds; }
    public void setIdempotentExpireSeconds(long idempotentExpireSeconds) { this.idempotentExpireSeconds = idempotentExpireSeconds;}
}
