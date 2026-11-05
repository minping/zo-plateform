package com.zo.platform.starter.redis.lock;

import com.zo.platform.starter.exception.ZoException;

/**
 * @author zhoump
 * @date 2026/11/5
 * @purpose
 */
public class RedisLockException extends ZoException {

    public RedisLockException() {
        super("获取分布式锁失败");
        setState(10001);
    }

    public RedisLockException(String message) {
        super(message);
        setState(10001);
    }

    public RedisLockException(String message, Throwable cause) {
        super(message);
        cause.printStackTrace();
        setState(10001);
    }
}
