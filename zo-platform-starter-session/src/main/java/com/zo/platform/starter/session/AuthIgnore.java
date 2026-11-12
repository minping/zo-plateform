package com.zo.platform.starter.session;

import java.lang.annotation.*;

/**
 * @author zhoump
 * @date 2026/11/10
 * @purpose
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AuthIgnore {


}
