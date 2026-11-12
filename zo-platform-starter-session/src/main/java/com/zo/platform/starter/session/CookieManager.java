package com.zo.platform.starter.session;

import com.zo.platform.sdk.common.kit.StrKit;
import com.zo.platform.starter.session.config.CookieConfig;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhoump
 * @date 2026/11/9
 * @purpose
 */
public class CookieManager {

    private CookieConfig cookieConfig;

    private HttpServletResponse response;

    @Autowired
    public void setCookieConfig(CookieConfig cookieConfig) {
        this.cookieConfig = cookieConfig;
    }

    @Autowired
    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public void setCookie(String key,String value){
        Cookie cookie = new Cookie(key, StrKit.urlEncode(value, "UTF-8"));
        cookie.setMaxAge(cookieConfig.getExpiry());
        cookie.setPath(cookieConfig.getPath());
        response.addCookie(cookie);
    }

    public void delCookie(String key){
        Cookie cookie = new Cookie(key, null);
        cookie.setMaxAge(0);
        cookie.setPath(cookieConfig.getPath());
        response.addCookie(cookie);
    }

}
