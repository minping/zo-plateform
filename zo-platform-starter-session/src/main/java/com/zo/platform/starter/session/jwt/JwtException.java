package com.zo.platform.starter.session.jwt;

import com.zo.platform.starter.exception.ZoException;

/**
 * @author zhoump
 * @date 2026/11/9
 * @purpose
 */
public class JwtException extends ZoException {

    private JwtCode code;


    private JwtException(String message) {
        super(message);
    }

    public JwtCode getCode() {
        return code;
    }

    public static JwtException create(JwtCode code) {
        JwtException jwtException = (JwtException) new JwtException(code.getMsg()).setState(code.getCode());
        return jwtException;
    }
}
