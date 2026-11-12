package com.zo.platform.starter.session.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.zo.platform.sdk.common.kit.DateKit;
import com.zo.platform.sdk.common.kit.StrKit;
import com.zo.platform.starter.session.config.SessionConfig;
import com.zo.platform.starter.session.constant.SessionConstant;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhoump
 * @date 2026/11/9
 * @purpose
 */
@Slf4j
public class JwtBuilder {

    /**
     * 构建token
     * @param jwtInfoSessionHolder
     * @param request
     * @return
     */
    public static JwtBuildResult buildToken(JwtInfoSessionHolder jwtInfoSessionHolder, HttpServletRequest request, String tokenKey){
        String token = getToken(request,tokenKey);
        return buildToken(jwtInfoSessionHolder,token);
    }

    /**
     * 构建token
     * @param jwtInfoSessionHolder
     * @param token
     * @return
     */
    public static JwtBuildResult buildToken(JwtInfoSessionHolder jwtInfoSessionHolder,String token){
        JwtBuildResult jwtBuildResult = new JwtBuildResult();

        jwtBuildResult.setToken(token);

        if (StrKit.isNotBlank(token)) {
            try {
                //初始化JwtResult
                JwtResult targetJwtResult = JwtAnalysis.analysis(token);

                jwtInfoSessionHolder.buildJwt(targetJwtResult);

                if(!StrKit.equals(jwtInfoSessionHolder.getToken(),token)){
                    //续期后 可能不一致
                    jwtBuildResult.setToken(jwtInfoSessionHolder.getToken());
                }
            }catch (JwtException e){
                jwtBuildResult.setJwtException(e);
            }

        }
        return jwtBuildResult;
    }

    /**
     * 获取token
     * @param request
     * @return
     */
    public static String getToken(HttpServletRequest request,String tokenKey){
        //boolean isGet = "GET".equalsIgnoreCase(request.getMethod());
        //Collections.list(request.getHeaderNames()).forEach(System.out::println);

        //if(isGet){
        //    Cookie[] cookies = request.getCookies();
        //    if(cookies!=null){
        //        for (Cookie cookie : cookies){
        //            if(StrKit.equals(tokenKey,cookie.getName())){
        //                return cookie.getValue();
        //            }
        //        }
        //    }
        //}else{
        //    return request.getHeader(tokenKey);
        //}
        return request.getHeader(tokenKey);
        //处理单点时动态设置token问题
        //return (String) request.getAttribute(tokenKey);
    }


    public static JwtResult newJwt(SessionConfig sessionConfig) {
        return newJwt(sessionConfig, null);
    }

    public static JwtResult newJwt(SessionConfig sessionConfig, JwtInfo jwtInfo) {
        JWTCreator.Builder jwtBuilder = JWT.create();

        if (jwtInfo == null) {
            jwtInfo = JwtInfo.create(sessionConfig);
        }

        /// 不验证未开始时间        withNotBefore(jwtInfo.getNotBefore())
        jwtBuilder.withExpiresAt(jwtInfo.getExpiresAt())
                .withIssuer(SessionConstant.ISSUER);

        //添加自定义参数
        if (jwtInfo.getData().size() > 0) {
            jwtInfo.getData().forEach((s, o) -> jwtBuilder.withClaim(s, o.asString()));
        }

        //添加头部 ID信息
        String sessionId = StrKit.uuid();
        if(StrKit.isNotBlank(jwtInfo.sessionId())){
            //续期 sessionId 与之前保持一致
            sessionId = jwtInfo.sessionId();
        }

        Map<String, Object> headerClaims = new HashMap<>(21);
        headerClaims.put(SessionConstant.ID_KEY, sessionId);

        JwtResult jwtResult = JwtResult.create()
                .setToken(jwtBuilder
                        .withHeader(headerClaims)
                        .sign(SessionConstant.ALGORITHM))
                .setSessionId(sessionId)
                .setData(jwtInfo.getData())
                .setExpiresAt(jwtInfo.getExpiresAt())
                .setNotBefore(jwtInfo.getNotBefore());
        return jwtResult;
    }


    /**
     * 是否续期
     *
     * @param sessionConfig
     * @param jwtResult
     * @return
     */
    public static Boolean isLengthen(SessionConfig sessionConfig, JwtResult jwtResult) {
        Date oldExpiresAt = jwtResult.getExpiresAt();

        long diffSecond = DateKit.compareSecond(oldExpiresAt, new Date());
        //检查是否续期
        if (diffSecond <= sessionConfig.getLengthenCheck().getSeconds()) {
            if (diffSecond < 0) {
                log.error("token diff second must greater than zero,you must check this error");
                throw JwtException.create(JwtCode.EXPIRED);
            }
            return true;
        }
        return false;
    }

    /**
     * 检查是否 token续期
     *
     * @param sessionConfig 续期参数
     * @param oldJwtResult  原始Jwt信息
     * @return
     */
    public static JwtResult lengthen(SessionConfig sessionConfig, JwtResult oldJwtResult) {

        Date oldExpiresAt = oldJwtResult.getExpiresAt();
        Date nowExpiresAt = DateKit.addSecond(oldExpiresAt, sessionConfig.getLengthenTime().getSeconds());
        JwtInfo jwtInfo = JwtInfo.create(sessionConfig);
        //继承之前的sessionId
        jwtInfo.setSessionId(oldJwtResult.sessionId());
        jwtInfo.setExpiresAt(nowExpiresAt);
        return newJwt(sessionConfig, jwtInfo);
    }

}
