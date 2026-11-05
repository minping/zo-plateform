package com.zo.platform.starter.redis.config;

import com.zo.platform.starter.redis.cache.PlatFormKeyGenerator;
import com.zo.platform.starter.redis.cache.PlatformCacheErrorHandler;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhoump
 * @date 2026/11/5
 * @purpose
 */
public class PlatformCacheConfig extends CachingConfigurerSupport {

    @Override
    public KeyGenerator keyGenerator() {
        return new PlatFormKeyGenerator();
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new PlatformCacheErrorHandler();
    }
}
