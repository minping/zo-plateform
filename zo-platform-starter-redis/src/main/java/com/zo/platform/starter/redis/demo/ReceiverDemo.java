package com.zo.platform.starter.redis.demo;

import com.zo.platform.starter.redis.Receiver;
import com.zo.platform.starter.redis.lock.RedisLock;

/**
 * 消息订阅者demo
 */
@Receiver(channel = "order:timeout", method = "onTimeout")
public class ReceiverDemo {
    /**
     * 消息处理方法，参数自动反序列化
     */
    public void onTimeout(String message) {
        // 业务逻辑：取消订单、释放库存
        System.out.println("收到订单超时消息：" + message);
    }
}
