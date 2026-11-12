package com.zo.platform.starter.session.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author zhoump
 * @date 2026/11/9
 * @purpose
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "zo.cookie")
public class CookieConfig {

    /**
     * cookie path 默认 "/"
     */
    private String path = "/";

    /**
     * cookie 过期时间 默认与浏览器生命周期一直
     */
    private int expiry = -1;
}
