package com.zo.platform.starter.session.jwt;

/**
 * @author zhoump
 * @date 2026/11/9
 * @purpose
 */
public enum JwtCode {
    /**
     * jwt
     */
    ERROR(29400, "异常会话"),
    EXPIRED(29401, "会话已过期"),
    DECODE(29402, "会话分析异常"),
    NOT_BEFORE(29403, "会话暂未生效"),
    NO_TOKEN(29404, "未找到有效会话"),
    USER_LOSE(29405, "会话已失效，请重新登录[0]"),
    USER_LOSE_IN_REDIS(29406, "会话已失效，请重新登录[1]"),
    LOGIN_REPEAT(29407, "账号已在其他设备登录"),
    BIZ_SESSION_ERROR(29408, "业务异常会话");

    private int code;
    private String msg;

    JwtCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }
}
