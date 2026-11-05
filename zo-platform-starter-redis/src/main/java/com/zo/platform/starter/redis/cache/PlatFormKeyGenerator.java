package com.zo.platform.starter.redis.cache;

import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;

/**
 * @author zhoump
 * @date 2026/11/5
 * @purpose
 */
public class PlatFormKeyGenerator implements KeyGenerator {

    private static final String SEPARATOR = "_";

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder sb = new StringBuilder();
        // 拼接类名 + 方法名
        sb.append(target.getClass().getName())
                .append(".")
                .append(method.getName());

        // 拼接参数，参数间加分隔符防碰撞
        if (params != null && params.length > 0) {
            sb.append(":");
            for (int i = 0; i < params.length; i++) {
                if (i > 0) {
                    sb.append(SEPARATOR);
                }
                sb.append(params[i] == null ? "null" : params[i].toString());
            }
        }
        return sb.toString();
    }
}
