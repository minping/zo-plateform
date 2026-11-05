package com.zo.platform.starter.redis.lock;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Redis 分布式锁切面
 * 拦截 @RedisLock 注解，自动完成加锁、释放锁
 */
@Slf4j
@Aspect
public class RedisLockAspect {

    private final RedisLockService redisLockService;

    /**
     * SpEL 解析器
     */
    private final SpelExpressionParser spelParser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    public RedisLockAspect(RedisLockService redisLockService) {
        this.redisLockService = redisLockService;
    }

    @Around("@annotation(redisLock)")
    public Object around(ProceedingJoinPoint point, RedisLock redisLock) throws Throwable {
        // 1. 解析生成完整锁Key
        String lockKey = buildLockKey(point, redisLock);

        // 2. 尝试获取锁
        boolean lockSuccess = redisLockService.tryLock(
                lockKey,
                redisLock.waitTime(),
                redisLock.leaseTime(),
                redisLock.timeUnit(),
                redisLock.reentrant(),
                redisLock.autoRenew()
        );

        // 3. 获取锁失败处理
        if (!lockSuccess) {
            String msg = String.format("获取分布式锁失败，lockKey: %s，等待时间: %d%s",
                    lockKey, redisLock.waitTime(), redisLock.timeUnit().name());
            log.warn(msg);
            if (redisLock.throwIfFail()) {
                throw new RedisLockException(msg);
            } else {
                return null;
            }
        }

        // 4. 执行业务方法，finally保证锁一定释放
        try {
            return point.proceed();
        } finally {
            try {
                redisLockService.unlock(lockKey, redisLock.leaseTime(), redisLock.timeUnit());
            } catch (Exception e) {
                log.error("释放分布式锁异常，lockKey: {}", lockKey, e);
            }
        }
    }

    /**
     * 解析 SpEL 生成完整锁 Key
     */
    private String buildLockKey(ProceedingJoinPoint point, RedisLock redisLock) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        String keyExpression = redisLock.key();

        // 纯字符串，不是SpEL表达式，直接拼接
        if (!keyExpression.contains("#")) {
            return redisLock.prefix() + keyExpression;
        }

        // SpEL 解析
        EvaluationContext context = new StandardEvaluationContext();
        Object[] args = point.getArgs();
        String[] paramNames = parameterNameDiscoverer.getParameterNames(signature.getMethod());
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        Expression expression = spelParser.parseExpression(keyExpression);
        Object keyValue = expression.getValue(context);
        return redisLock.prefix() + (keyValue == null ? "null" : keyValue.toString());
    }
}
