package com.db;

import java.lang.annotation.*;

/**
 * @author zhoump
 * @date 2026/6/24
 * @purpose
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Namespace {

    /**
     * sql文件中对应namespace值
     * @return
     */
    String value();
}
