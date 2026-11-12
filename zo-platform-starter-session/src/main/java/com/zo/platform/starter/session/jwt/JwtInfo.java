package com.zo.platform.starter.session.jwt;

import com.auth0.jwt.interfaces.Claim;
import com.zo.platform.sdk.common.kit.DateKit;
import com.zo.platform.starter.session.config.SessionConfig;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoump
 * @date 2026/11/9
 * @purpose
 */
public class JwtInfo {
    /**
     * 生效时间
     * 默认当前时间
     */
    private Date notBefore;

    /**
     * 失效时间
     * 默认notBefore + 30分钟
     */
    private Date expiresAt;

    /**
     * 返回前端的参数
     */
    private Map<String, Claim> data = new HashMap<>();

    private String sessionId;

    /**
     * 用户唯一标识
     */
    private String userId;

    private JwtInfo(SessionConfig sessionConfig) {
        this.notBefore = DateKit.addSecond(new Date(),-30);
        this.expiresAt = DateKit.addSecond(this.notBefore, sessionConfig.getTimeout().getSeconds());
    }

    public static JwtInfo create(SessionConfig sessionConfig) {
        return new JwtInfo(sessionConfig);
    }


    public Date getNotBefore() {
        return this.notBefore;
    }

    public JwtInfo setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
        return this;
    }

    public Date getExpiresAt() {
        return this.expiresAt;
    }

    public JwtInfo setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public Map<String, Claim> getData() {
        return data;
    }

    public JwtInfo setData(Map<String, Claim> data) {
        this.data = data;
        return this;
    }

    JwtInfo setSessionId(String value) {
        this.sessionId = value;
        return this;
    }

    public String sessionId() {
        return this.sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public JwtInfo setUserId(String userId) {
        this.userId = userId;
        return this;
    }
}
