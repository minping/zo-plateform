package com.jfinal.plugin.activerecord.cache;

/**
 * @author zhoump
 * @date 2026/7/14
 * @purpose
 */
public class EhCache implements ICache {

    @SuppressWarnings("unchecked")
    public <T> T get(String cacheName, Object key) {
        return null;
    }

    public void put(String cacheName, Object key, Object value) {


    }

    public void remove(String cacheName, Object key) {


    }

    public void removeAll(String cacheName) {
    }
}