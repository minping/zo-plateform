package com.zo.platform.starter.redis;

import com.zo.platform.starter.exception.RedisOperationException;
import com.zo.platform.starter.exception.ZoException;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 通用工具类
 * 基于 RedisTemplate<String, Object> 封装，Value 统一 JSON 序列化
 */
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 释放锁 Lua 脚本：保证判断锁归属 + 删除锁原子性
     */
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    private final DefaultRedisScript<Long> unlockScript;

    public RedisUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.unlockScript = new DefaultRedisScript<>();
        this.unlockScript.setScriptText(UNLOCK_SCRIPT);
        this.unlockScript.setResultType(Long.class);
    }

    // ====================== 1. 通用 Key 操作 ======================

    /**
     * 判断 Key 是否存在
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            throw new RedisOperationException("Redis hasKey 异常", e);
        }
    }

    /**
     * 删除单个/多个 Key
     */
    public void delete(String... keys) {
        if (keys == null || keys.length == 0) {
            return;
        }
        try {
            if (keys.length == 1) {
                redisTemplate.delete(keys[0]);
            } else {
                redisTemplate.delete(Arrays.asList(keys));
            }
        } catch (Exception e) {
            throw new RedisOperationException("Redis delete 异常", e);
        }
    }

    /**
     * 设置过期时间
     * @param key 键
     * @param timeout 时长
     * @param unit 时间单位
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(redisTemplate.expire(key, timeout, unit));
        } catch (Exception e) {
            throw new RedisOperationException("Redis expire 异常", e);
        }
    }

    /**
     * 获取剩余过期时间（秒）
     * @return -1 永久有效，-2 Key不存在
     */
    public long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return expire == null ? -2 : expire;
        } catch (Exception e) {
            throw new RedisOperationException("Redis getExpire 异常", e);
        }
    }

    /**
     * 移除过期时间，Key 变为永久有效
     */
    public boolean persist(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.persist(key));
        } catch (Exception e) {
            throw new RedisOperationException("Redis persist 异常", e);
        }
    }

    // ====================== 2. String / 对象操作 ======================

    /**
     * 普通缓存放入
     */
    public void set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            throw new RedisOperationException("Redis set 异常", e);
        }
    }

    /**
     * 普通缓存放入并设置过期时间
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
        } catch (Exception e) {
            throw new RedisOperationException("Redis set 异常", e);
        }
    }

    /**
     * 仅当 Key 不存在时设置（SETNX），原子操作
     * @return true 设置成功，false Key已存在
     */
    public boolean setIfAbsent(String key, Object value, long timeout, TimeUnit unit) {
        try {
            return Boolean.TRUE.equals(
                    redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit));
        } catch (Exception e) {
            throw new RedisOperationException("Redis setIfAbsent 异常", e);
        }
    }

    /**
     * 普通缓存获取
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            throw new RedisOperationException("Redis get 异常", e);
        }
    }

    /**
     * 带泛型的缓存获取，避免强转
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return value == null ? null : (T) value;
        } catch (Exception e) {
            throw new RedisOperationException("Redis get 异常", e);
        }
    }

    /**
     * 原子递增
     * @param delta 递增步长，正数递增，负数递减
     * @return 递增后的值
     */
    public long increment(String key, long delta) {
        try {
            Long result = redisTemplate.opsForValue().increment(key, delta);
            return result == null ? 0 : result;
        } catch (Exception e) {
            throw new RedisOperationException("Redis increment 异常", e);
        }
    }

    /**
     * 原子递减
     */
    public long decrement(String key, long delta) {
        return increment(key, -delta);
    }

    // ====================== 3. Hash 哈希操作 ======================

    /**
     * 向 Hash 中放入一个字段
     */
    public void hSet(String key, String hashKey, Object value) {
        try {
            redisTemplate.opsForHash().put(key, hashKey, value);
        } catch (Exception e) {
            throw new RedisOperationException("Redis hSet 异常", e);
        }
    }

    /**
     * 向 Hash 中批量放入多个字段
     */
    public void hMSet(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
        } catch (Exception e) {
            throw new RedisOperationException("Redis hMSet 异常", e);
        }
    }

    /**
     * 获取 Hash 中某个字段的值
     */
    public Object hGet(String key, String hashKey) {
        try {
            return redisTemplate.opsForHash().get(key, hashKey);
        } catch (Exception e) {
            throw new RedisOperationException("Redis hGet 异常", e);
        }
    }

    /**
     * 带泛型获取 Hash 字段值
     */
    @SuppressWarnings("unchecked")
    public <T> T hGet(String key, String hashKey, Class<T> clazz) {
        try {
            Object value = redisTemplate.opsForHash().get(key, hashKey);
            return value == null ? null : (T) value;
        } catch (Exception e) {
            throw new RedisOperationException("Redis hGet 异常", e);
        }
    }

    /**
     * 获取 Hash 所有键值对
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> hGetAll(String key) {
        try {
            Map<Object, Object> original = redisTemplate.opsForHash().entries(key);
            Map<String, Object> result = new HashMap<>(original.size());
            for (Map.Entry<Object, Object> entry : original.entrySet()) {
                result.put(String.valueOf(entry.getKey()), entry.getValue());
            }
            return result;
        } catch (Exception e) {
            throw new RedisOperationException("Redis hGetAll 异常", e);
        }
    }

    /**
     * Hash 字段原子递增
     */
    public long hIncrement(String key, String hashKey, long delta) {
        try {
            return redisTemplate.opsForHash().increment(key, hashKey, delta);
        } catch (Exception e) {
            throw new RedisOperationException("Redis hIncrement 异常", e);
        }
    }

    /**
     * 判断 Hash 中是否存在某个字段
     */
    public boolean hHasKey(String key, String hashKey) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, hashKey));
        } catch (Exception e) {
            throw new RedisOperationException("Redis hHasKey 异常", e);
        }
    }

    /**
     * 删除 Hash 中一个/多个字段
     */
    public long hDelete(String key, Object... hashKeys) {
        try {
            return redisTemplate.opsForHash().delete(key, hashKeys);
        } catch (Exception e) {
            throw new RedisOperationException("Redis hDelete 异常", e);
        }
    }

    /**
     * 获取 Hash 字段数量
     */
    public long hSize(String key) {
        try {
            Long size = redisTemplate.opsForHash().size(key);
            return size == null ? 0 : size;
        } catch (Exception e) {
            throw new RedisOperationException("Redis hSize 异常", e);
        }
    }

    // ====================== 4. List 列表操作 ======================

    /**
     * 从列表左侧推入（头部）
     */
    public long lPush(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForList().leftPushAll(key, values);
            return count == null ? 0 : count;
        } catch (Exception e) {
            throw new RedisOperationException("Redis lPush 异常", e);
        }
    }

    /**
     * 从列表右侧推入（尾部）
     */
    public long rPush(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForList().rightPushAll(key, values);
            return count == null ? 0 : count;
        } catch (Exception e) {
            throw new RedisOperationException("Redis rPush 异常", e);
        }
    }

    /**
     * 获取列表指定范围元素
     * @param start 起始索引，0开始
     * @param end 结束索引，-1表示最后一个
     */
    public List<Object> lRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            throw new RedisOperationException("Redis lRange 异常", e);
        }
    }

    /**
     * 左侧弹出一个元素
     */
    public Object lPop(String key) {
        try {
            return redisTemplate.opsForList().leftPop(key);
        } catch (Exception e) {
            throw new RedisOperationException("Redis lPop 异常", e);
        }
    }

    /**
     * 右侧弹出一个元素
     */
    public Object rPop(String key) {
        try {
            return redisTemplate.opsForList().rightPop(key);
        } catch (Exception e) {
            throw new RedisOperationException("Redis rPop 异常", e);
        }
    }

    /**
     * 获取列表长度
     */
    public long lSize(String key) {
        try {
            Long size = redisTemplate.opsForList().size(key);
            return size == null ? 0 : size;
        } catch (Exception e) {
            throw new RedisOperationException("Redis lSize 异常", e);
        }
    }

    /**
     * 移除列表中 count 个值为 value 的元素
     * count>0 从左删，count<0 从右删，count=0 删除全部
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long num = redisTemplate.opsForList().remove(key, count, value);
            return num == null ? 0 : num;
        } catch (Exception e) {
            throw new RedisOperationException("Redis lRemove 异常", e);
        }
    }

    // ====================== 5. Set 集合操作 ======================

    /**
     * 向集合中添加元素
     */
    public long sAdd(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            return count == null ? 0 : count;
        } catch (Exception e) {
            throw new RedisOperationException("Redis sAdd 异常", e);
        }
    }

    /**
     * 获取集合所有元素
     */
    public Set<Object> sMembers(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            throw new RedisOperationException("Redis sMembers 异常", e);
        }
    }

    /**
     * 判断元素是否在集合中
     */
    public boolean sIsMember(String key, Object value) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
        } catch (Exception e) {
            throw new RedisOperationException("Redis sIsMember 异常", e);
        }
    }

    /**
     * 获取集合大小
     */
    public long sSize(String key) {
        try {
            Long size = redisTemplate.opsForSet().size(key);
            return size == null ? 0 : size;
        } catch (Exception e) {
            throw new RedisOperationException("Redis sSize 异常", e);
        }
    }

    /**
     * 移除集合中元素
     */
    public long sRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count == null ? 0 : count;
        } catch (Exception e) {
            throw new RedisOperationException("Redis sRemove 异常", e);
        }
    }

    // ====================== 6. ZSet 有序集合操作 ======================

    /**
     * 添加有序集合元素，已存在则更新分数
     */
    public boolean zAdd(String key, Object value, double score) {
        try {
            return Boolean.TRUE.equals(redisTemplate.opsForZSet().add(key, value, score));
        } catch (Exception e) {
            throw new RedisOperationException("Redis zAdd 异常", e);
        }
    }

    /**
     * 按索引升序获取范围内元素
     */
    public Set<Object> zRange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key, start, end);
        } catch (Exception e) {
            throw new RedisOperationException("Redis zRange 异常", e);
        }
    }

    /**
     * 获取元素排名（升序，从0开始）
     */
    public Long zRank(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().rank(key, value);
        } catch (Exception e) {
            throw new RedisOperationException("Redis zRank 异常", e);
        }
    }

    /**
     * 获取元素分数
     */
    public Double zScore(String key, Object value) {
        try {
            return redisTemplate.opsForZSet().score(key, value);
        } catch (Exception e) {
            throw new RedisOperationException("Redis zScore 异常", e);
        }
    }

    /**
     * 移除有序集合元素
     */
    public long zRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForZSet().remove(key, values);
            return count == null ? 0 : count;
        } catch (Exception e) {
            throw new RedisOperationException("Redis zRemove 异常", e);
        }
    }

    // ====================== 7. 分布式锁 ======================

    /**
     * 尝试获取分布式锁（原子操作：加锁+设置过期时间）
     * @param lockKey 锁Key
     * @param requestId 请求标识（锁持有者，释放锁时校验）
     * @param expireTime 锁过期时间
     * @param unit 时间单位
     * @return true 获取成功
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime, TimeUnit unit) {
        return setIfAbsent(lockKey, requestId, expireTime, unit);
    }

    /**
     * 释放分布式锁（Lua脚本保证原子性：校验归属+删除）
     * @param lockKey 锁Key
     * @param requestId 请求标识，必须和加锁时一致
     * @return true 释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        try {
            Long result = redisTemplate.execute(unlockScript,
                    Collections.singletonList(lockKey), requestId);
            return result != null && result == 1;
        } catch (Exception e) {
            throw new RedisOperationException("Redis 释放锁异常", e);
        }
    }

    // ====================== 8. 批量操作 Pipeline 入口 ======================

    /**
     * 执行批量操作（高性能批量读写）
     * 示例：redisUtil.executePipelined(operations -> { operations.opsForValue().set("k1","v1"); ... });
     */
    public List<Object> executePipelined(java.util.function.Consumer<RedisOperations<String, Object>> action) {
        try {
            return redisTemplate.executePipelined(new SessionCallback<Object>() {
                @Override
                public Object execute(RedisOperations operations) {
                    action.accept(operations);
                    return null;
                }
            });
        } catch (Exception e) {
            throw new RedisOperationException("Redis pipeline 执行异常", e);
        }
    }
}
