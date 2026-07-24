package com.jfinal.plugin.activerecord.cache;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public interface ICache {
    <T> T get(String cacheName, Object key);

    void put(String cacheName, Object key, Object value);

    void remove(String cacheName, Object key);

    void removeAll(String cacheName);
}
