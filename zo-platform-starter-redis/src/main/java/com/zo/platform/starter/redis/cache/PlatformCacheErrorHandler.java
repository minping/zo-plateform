package com.zo.platform.starter.redis.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

/**
 * @author zhoump
 * @date 2026/11/5
 * @purpose
 */
@Slf4j
public class PlatformCacheErrorHandler implements CacheErrorHandler {
    @Override
    public void handleCacheGetError(RuntimeException e, Cache cache, Object key) {
        log.warn("缓存读取失败，自动回源 | 缓存名:{} | key:{} | 异常:{}",
                cache.getName(), key, e.getMessage());
    }

    @Override
    public void handleCachePutError(RuntimeException e, Cache cache, Object key, Object value) {
        log.warn("缓存写入失败 | 缓存名:{} | key:{} | 异常:{}",
                cache.getName(), key, e.getMessage());
    }

    @Override
    public void handleCacheEvictError(RuntimeException e, Cache cache, Object key) {
        log.warn("缓存删除失败 | 缓存名:{} | key:{} | 异常:{}",
                cache.getName(), key, e.getMessage());
    }

    @Override
    public void handleCacheClearError(RuntimeException e, Cache cache) {
        log.warn("缓存清空失败 | 缓存名:{} | 异常:{}",
                cache.getName(), e.getMessage());
    }
}
