package com.zo.platform.starter.redis.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author zhoump
 * @date 2026/11/4
 * @purpose
 */
@Data
@ConfigurationProperties(prefix = "platform.redis.cache")
public class RedisCacheProperties {
    /**
     * 全局默认过期时间，默认1天
     */
    private Duration defaultTtl = Duration.ofDays(1);
    /**
     * 是否禁止缓存空值（防缓存穿透可关闭，空值短TTL）
     */
    private boolean disableCachingNullValues = true;
    /**
     * 缓存Key统一前缀
     */
    private String keyPrefix = "cache:";
    /**
     * 是否开启随机TTL（打散过期时间，防缓存雪崩）
     */
    private boolean randomTtlEnabled = false;

    /**
     * 随机TTL偏移比例，默认±10%
     */
    private double randomTtlFactor = 0.1;
}
