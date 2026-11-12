package com.zo.platform.starter.session.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoump
 * @date 2026/11/9
 * @purpose
 */
@ConfigurationProperties(
        prefix = "zo.session"
)
@Data
public class SessionConfig {

    /**
     * session或者token过期时间默认30分钟<br/>
     * 每次续期时间
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration timeout = Duration.ofMinutes(30);

    /**
     * token续期时间：默认续期30分钟
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration lengthenTime = Duration.ofMinutes(30);

    /**
     * 检查续期时间 默认离失效5分钟内检查
     */
    @DurationUnit(ChronoUnit.SECONDS)
    private Duration lengthenCheck =  Duration.ofMinutes(5);;

    /**
     * 全局忽略权限验证
     */
    private boolean authIgnore = false;

    /**
     * session token key
     */
    private String tokenKey = "authorization_token";

    /**
     * 放行url的白名单
     */
    List<String> urlWhiteList=new ArrayList<>();
}
