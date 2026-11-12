package com.zo.platform.starter.session.constant;

import com.auth0.jwt.algorithms.Algorithm;

/**
 * @author zhoump
 * @date 2026/11/7
 * @purpose
 */
public class SessionConstant {

    public static final String VERSION_NAME = "zo-platform-starter-session";
    public static final String VERSION_CODE = "1.0.0";

    public static final Algorithm ALGORITHM = Algorithm.HMAC256("ZoPlatForm");
    public static final String SESSION_REDIS_TEMPLATE_BEAN_NAME = "SESSION_REDIS_TEMPLATE_BEAN";
    /**
     * sessionId
     */
    public static final String ID_KEY = "SESSIONID";
    /**
     * 发行人
     */
    public static final String ISSUER = "Meking";

    /**
     * token key  [sessionId]
     */
    public static final String SESSION_TOKEN_KEY = "zo:session:token:";
    /**
     * 规则rule key [userId]
     */
    public static final String SESSION_RULE_KEY = "zo:session:rule:";
    /**
     * session 内容 [userId]
     */
    public static final String SESSION_CONTENT_KEY = "zo:session:content:";

    public static final String RULE_CONTENT_KEY = "session_content_key";
    public static final String RULE_TOKEN_KEY = "session_token_key";

    public static final String SESSION_RULE_ERROR_REDIS_KEY = "SESSION_ERROR";

    public static final String JWT_RESULT = "__jwt_result";
    public static final int FILTER_ORDER = 10;
    public static final int INTERCEPTOR_ORDER = 10;
}

