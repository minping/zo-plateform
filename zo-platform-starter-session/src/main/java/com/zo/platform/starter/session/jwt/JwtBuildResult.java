package com.zo.platform.starter.session.jwt;

import lombok.Data;

/**
 * @author zhoump
 * @date 2026/11/9
 * @purpose
 */
@Data
public class JwtBuildResult {

    private String token;
    private JwtException jwtException;
}
