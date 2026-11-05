package com.zo.platform.starter.redis.demo;

import com.zo.platform.starter.redis.lock.RedisLock;

/**
 * @author zhoump
 * @date 2026/11/5
 * @purpose
 */
public class RedisLockDemo {

    /**
     * 根据订单号加锁，使用默认配置
     * 锁Key最终为：lock:1001
     */
    @RedisLock(key = "#orderId")
    public void cancelOrder(Long orderId) {
        // 业务逻辑：取消订单、释放库存
    }
}
