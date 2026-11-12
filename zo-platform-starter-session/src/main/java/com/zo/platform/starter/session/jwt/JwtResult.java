package com.zo.platform.starter.session.jwt;

import com.auth0.jwt.interfaces.Claim;
import com.zo.platform.sdk.common.kit.DateKit;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoump
 * @date 2026/11/9
 * @purpose
 */
public class JwtResult {

    private String token;

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


    private JwtResult() {

    }

    public static JwtResult create() {
        return new JwtResult();
    }


    public String getToken() {
        return token;
    }

    public JwtResult setToken(String token) {
        this.token = token;
        return this;
    }


    public Date getNotBefore() {
        return notBefore == null ? new Date() : notBefore;
    }

    public JwtResult setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
        return this;
    }

    public Date getExpiresAt() {
        return expiresAt == null ? DateKit.addMinute(getNotBefore(), 1440) : expiresAt;
    }

    public JwtResult setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
        return this;
    }

    public Map<String, Claim> getData() {
        return data;
    }

    public JwtResult setData(Map<String, Claim> data) {
        this.data = data;
        return this;
    }

    public JwtResult setSessionId(String value) {
        this.sessionId = value;
        return this;
    }

    public String sessionId() {
        return this.sessionId;
    }

    /**
     * 有效时间
     *
     * @return
     */
    public long expires() {
        return DateKit.compareSecond(this.expiresAt, new Date());
    }
}
