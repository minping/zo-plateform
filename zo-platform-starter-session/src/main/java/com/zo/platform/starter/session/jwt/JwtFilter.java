package com.zo.platform.starter.session.jwt;

import com.zo.platform.sdk.common.kit.StrKit;
import com.zo.platform.starter.session.CookieManager;
import com.zo.platform.starter.session.config.SessionConfig;
import com.zo.platform.starter.session.constant.SessionConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zhoump
 * @date 2026/11/9
 * @purpose
 */
@Order(SessionConstant.FILTER_ORDER)
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    JwtInfoSessionHolder jwtInfoSessionHolder;

    CookieManager cookieManager;

    SessionConfig sessionConfig;

    @Autowired
    public void setSessionConfig(SessionConfig sessionConfig) {
        this.sessionConfig = sessionConfig;
    }

    @Autowired
    public void setCookieManager(CookieManager cookieManager) {
        this.cookieManager = cookieManager;
    }



    @Override
    public void destroy() {
        log.info("jwt filter destroy");
    }

    @Autowired
    public void setJwtInfoSessionHolder(JwtInfoSessionHolder jwtInfoSessionHolder) {
        this.jwtInfoSessionHolder = jwtInfoSessionHolder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenKey = sessionConfig.getTokenKey();
        String token = JwtBuilder.getToken(request,tokenKey);
        JwtBuildResult jwtBuildResult = JwtBuilder.buildToken(jwtInfoSessionHolder,request,tokenKey);
        String respToken = jwtBuildResult.getToken();
        request.setAttribute(SessionConstant.JWT_RESULT,jwtBuildResult);

        if(!StrKit.equals(token,respToken)){
            //续期刷新token
            cookieManager.setCookie(tokenKey,respToken);
        }
        try {
            filterChain.doFilter(request,response);
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            jwtInfoSessionHolder.release();
        }

    }

}
