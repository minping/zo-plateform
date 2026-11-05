package com.zo.platform.starter.redis.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Redis 分布式锁核心服务
 * 基于 Lua 脚本实现原子性、可重入、自动续期
 */
@Slf4j
public class RedisLockService {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 线程级锁持有者ID：同线程共享，用于可重入识别
     */
    private final ThreadLocal<String> holderIdThreadLocal = ThreadLocal.withInitial(
            () -> UUID.randomUUID().toString().replace("-", "") + ":" + Thread.currentThread().getId()
    );

    /**
     * 看门狗定时任务容器：记录每个锁的续期任务
     * key = lockKey + holderId
     */
    private final Map<String, ScheduledFuture<?>> renewTaskMap = new ConcurrentHashMap<>();

    /**
     * 看门狗线程池
     */
    private ScheduledExecutorService watchdogExecutor;

    /**
     * 加锁 Lua 脚本（可重入 Hash 版）
     * 返回 1=加锁成功，其他=剩余过期时间(毫秒)
     */
    private DefaultRedisScript<Long> lockScript;

    /**
     * 解锁 Lua 脚本
     * 返回 1=完全释放，0=重入次数递减/无权释放
     */
    private DefaultRedisScript<Long> unlockScript;

    /**
     * 续期 Lua 脚本
     */
    private DefaultRedisScript<Long> renewScript;

    public RedisLockService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        // 初始化看门狗线程池
        watchdogExecutor = new ScheduledThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                r -> {
                    Thread thread = new Thread(r, "redis-lock-watchdog-");
                    thread.setDaemon(true);
                    return thread;
                }
        );

        // 初始化加锁脚本
        lockScript = new DefaultRedisScript<>();
        lockScript.setResultType(Long.class);
        lockScript.setScriptText("local key = KEYS[1]\n" +
                "                local holderId = ARGV[1]\n" +
                "                local leaseSeconds = tonumber(ARGV[2])\n" +
                "                local reentrant = tonumber(ARGV[3])\n" +
                "                \n" +
                "                -- 锁不存在，直接加锁\n" +
                "                if (redis.call('exists', key) == 0) then\n" +
                "                    redis.call('hset', key, holderId, 1)\n" +
                "                    redis.call('expire', key, leaseSeconds)\n" +
                "                    return 1\n" +
                "                end\n" +
                "                \n" +
                "                -- 锁存在，判断是否是当前持有者\n" +
                "                if (redis.call('hexists', key, holderId) == 1) then\n" +
                "                    if (reentrant == 1) then\n" +
                "                        -- 可重入：次数+1，刷新过期时间\n" +
                "                        redis.call('hincrby', key, holderId, 1)\n" +
                "                        redis.call('expire', key, leaseSeconds)\n" +
                "                        return 1\n" +
                "                    else\n" +
                "                        -- 不可重入：直接返回失败\n" +
                "                        return -1\n" +
                "                    end\n" +
                "                end\n" +
                "                \n" +
                "                -- 不是持有者，返回剩余过期时间(毫秒)\n" +
                "                return redis.call('pttl', key)");

        // 初始化解锁脚本
        unlockScript = new DefaultRedisScript<>();
        unlockScript.setResultType(Long.class);
        unlockScript.setScriptText("local key = KEYS[1]\n" +
                "                local holderId = ARGV[1]\n" +
                "                local leaseSeconds = tonumber(ARGV[2])\n" +
                "                \n" +
                "                -- 锁不存在，视为释放成功\n" +
                "                if (redis.call('exists', key) == 0) then\n" +
                "                    return 1\n" +
                "                end\n" +
                "                \n" +
                "                -- 不是当前持有者，无权释放\n" +
                "                if (redis.call('hexists', key, holderId) == 0) then\n" +
                "                    return 0\n" +
                "                end\n" +
                "                \n" +
                "                -- 重入次数减1\n" +
                "                local count = redis.call('hincrby', key, holderId, -1)\n" +
                "                if (count > 0) then\n" +
                "                    -- 还有重入次数，刷新过期时间\n" +
                "                    redis.call('expire', key, leaseSeconds)\n" +
                "                    return 0\n" +
                "                else\n" +
                "                    -- 次数为0，彻底删除锁\n" +
                "                    redis.call('del', key)\n" +
                "                    return 1\n" +
                "                end");

        // 初始化续期脚本
        renewScript = new DefaultRedisScript<>();
        renewScript.setResultType(Long.class);
        renewScript.setScriptText(" local key = KEYS[1]\n" +
                "                local holderId = ARGV[1]\n" +
                "                local leaseSeconds = tonumber(ARGV[2])\n" +
                "                \n" +
                "                if (redis.call('hexists', key, holderId) == 1) then\n" +
                "                    redis.call('expire', key, leaseSeconds)\n" +
                "                    return 1\n" +
                "                end\n" +
                "                return 0");
    }

    /**
     * 尝试获取锁
     * @param lockKey 锁的完整Key
     * @param waitTime 等待时间
     * @param leaseTime 过期时间
     * @param timeUnit 时间单位
     * @param reentrant 是否可重入
     * @param autoRenew 是否自动续期
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit timeUnit,
                           boolean reentrant, boolean autoRenew) {
        String holderId = holderIdThreadLocal.get();
        long waitMillis = timeUnit.toMillis(waitTime);
        long leaseSeconds = timeUnit.toSeconds(leaseTime);
        long startTime = System.currentTimeMillis();

        // 自旋等待
        while (true) {
            Long result = redisTemplate.execute(
                    lockScript,
                    Collections.singletonList(lockKey),
                    holderId,
                    leaseSeconds,
                    reentrant ? 1 : 0
            );

            // 加锁成功
            if (result != null && result == 1) {
                // 开启自动续期
                if (autoRenew) {
                    startWatchdog(lockKey, holderId, leaseSeconds);
                }
                return true;
            }

            // 超时判断
            if (System.currentTimeMillis() - startTime > waitMillis) {
                return false;
            }

            // 自旋休眠 50ms
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    /**
     * 释放锁
     * @param lockKey 锁Key
     * @param leaseTime 过期时间
     * @param timeUnit 时间单位
     * @return 是否完全释放
     */
    public boolean unlock(String lockKey, long leaseTime, TimeUnit timeUnit) {
        String holderId = holderIdThreadLocal.get();
        long leaseSeconds = timeUnit.toSeconds(leaseTime);

        try {
            // 停止看门狗
            stopWatchdog(lockKey, holderId);

            Long result = redisTemplate.execute(
                    unlockScript,
                    Collections.singletonList(lockKey),
                    holderId,
                    leaseSeconds
            );
            return result != null && result == 1;
        } finally {
            // 完全释放后清理ThreadLocal
            if (isFullyReleased(lockKey, holderId)) {
                holderIdThreadLocal.remove();
            }
        }
    }

    /**
     * 启动看门狗：每过期时间的1/3续期一次
     */
    private void startWatchdog(String lockKey, String holderId, long leaseSeconds) {
        String taskKey = lockKey + ":" + holderId;
        // 已经有续期任务则不重复创建
        if (renewTaskMap.containsKey(taskKey)) {
            return;
        }

        long renewInterval = leaseSeconds * 1000 / 3;
        ScheduledFuture<?> future = watchdogExecutor.scheduleAtFixedRate(() -> {
            try {
                redisTemplate.execute(
                        renewScript,
                        Collections.singletonList(lockKey),
                        holderId,
                        leaseSeconds
                );
            } catch (Exception e) {
                log.warn("锁续期失败，lockKey:{}", lockKey, e);
            }
        }, renewInterval, renewInterval, TimeUnit.MILLISECONDS);

        renewTaskMap.put(taskKey, future);
    }

    /**
     * 停止看门狗
     */
    private void stopWatchdog(String lockKey, String holderId) {
        String taskKey = lockKey + ":" + holderId;
        ScheduledFuture<?> future = renewTaskMap.remove(taskKey);
        if (future != null) {
            future.cancel(false);
        }
    }

    /**
     * 判断锁是否完全释放（重入次数归零）
     */
    private boolean isFullyReleased(String lockKey, String holderId) {
        Boolean hasKey = redisTemplate.opsForHash().hasKey(lockKey, holderId);
        return !Boolean.TRUE.equals(hasKey);
    }
}