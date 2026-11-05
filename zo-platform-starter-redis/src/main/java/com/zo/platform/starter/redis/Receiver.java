package com.zo.platform.starter.redis;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhoump
 * @date 2026/11/5
 * @purpose
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Receiver {

    /**
     * 订阅的频道/主题
     */
    String channel();

    /**
     * 处理消息的方法名，默认 handle
     */
    String method() default "handle";

    /**
     * 是否是模式匹配（通配符订阅），默认否
     */
    boolean pattern() default false;
}
