package com.zo.platform.starter.redis.lock;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * @author zhoump
 * @date 2026/11/5
 * @purpose
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedisLock {

    /**
     * 锁的 Key，支持 SpEL 表达式
     * 示例：#orderId、#order.orderId
     */
    String key();

    /**
     * 锁 Key 统一前缀
     */
    String prefix() default "lock:";

    /**
     * 获取锁的最大等待时间
     */
    long waitTime() default 3;

    /**
     * 锁持有过期时间（看门狗开启时为初始过期时间）
     */
    long leaseTime() default 30;

    /**
     * 时间单位，默认秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 是否支持可重入，默认支持
     */
    boolean reentrant() default true;

    /**
     * 是否开启自动续期（看门狗），默认开启
     * 长耗时业务建议开启，防止锁提前过期导致并发问题
     */
    boolean autoRenew() default true;

    /**
     * 获取锁失败是否抛出异常
     * true：抛出 LockException
     * false：直接返回 null（仅对非 void 返回值方法生效）
     */
    boolean throwIfFail() default true;
}