package com.zo.platform.starter.session.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.zo.platform.starter.session.constant.SessionConstant;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @author zhoump
 * @date 2026/11/9
 * @purpose
 */
@Slf4j
public class JwtAnalysis {

    /**
     * 获取token 解析后的信息
     *
     * @param token
     * @return
     */
    public static JwtResult analysis(String token) {
        JWTVerifier verifier = JWT.require(SessionConstant.ALGORITHM)
                .withIssuer(SessionConstant.ISSUER)
                .build();
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            Date expiresAt = decodedJWT.getExpiresAt();
            Date notBefore = decodedJWT.getNotBefore();
            return JwtResult.create()
                    .setToken(token)
                    .setExpiresAt(expiresAt)
                    .setNotBefore(notBefore)
                    .setData(decodedJWT.getClaims())
                    .setSessionId(decodedJWT.getHeaderClaim(SessionConstant.ID_KEY).asString());
        } catch (TokenExpiredException expired) {
            //token已过期
            throw JwtException.create(JwtCode.EXPIRED);
        } catch (JWTDecodeException decodeException) {
            //token解析异常
            throw JwtException.create(JwtCode.DECODE);
        } catch (InvalidClaimException invalid) {
            //token暂未生效
            throw JwtException.create(JwtCode.NOT_BEFORE);
        } catch (Exception e) {
            log.error("token异常", e);
            throw JwtException.create(JwtCode.ERROR);
        }
    }
}
