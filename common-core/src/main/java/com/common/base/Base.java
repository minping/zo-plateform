package com.common.base;

import com.common.configuration.SpringContextHolder;
import com.common.log.ZoLog;
import com.common.util.Result;
import com.common.util.StrKit;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;

/**
 * @author zhoump
 * @date 2026/5/18
 * @purpose
 */
public class Base implements Serializable {

    public MultipartHttpServletRequest multipartHttpServletRequest() {
        MultipartResolver resolver = SpringContextHolder.getBean("multipartResolver", MultipartResolver.class);
        return resolver.resolveMultipart(request());
    }

    public HttpSession session() {
        return request().getSession();
    }

    public HttpServletRequest request() {
        return SpringContextHolder.request();
    }

    public HttpServletResponse response() {
        return SpringContextHolder.response();
    }

    /**
     * {
     * success:true,//请求执行成功
     * state:20000,//成功
     * message:"成功",
     * data:{}//返回数据结果集
     * }
     *
     * @return
     */
    public Result success() {
        return Result.me().success();
    }

    public Result success(Object data) {
        return Result.me().success(data);
    }

    /**
     * {
     * success:false,//请求执行失败
     * state:20001,   //失败
     * message:"失败",
     * data:{}//返回数据结果集
     * }
     *
     * @return
     */
    public Result error() {
        return Result.me().error();
    }

    public Result error(Object data) {
        return Result.me().error(data);
    }

    /**
     * log日志 info
     *
     * @param msg
     * @param args
     */
    public void info(String msg, Object... args) {
        ZoLog.info(msg, args);
    }

    /**
     * log日志 debug
     *
     * @param msg
     * @param args
     */
    public void debug(String msg, Object... args) {
        ZoLog.debug(msg, args);
    }

    /**
     * log日志 error
     *
     * @param msg
     * @param args
     */
    public void error(String msg, Object... args) {
        ZoLog.error(msg, args);
    }

    /**
     * 设置cookies
     *
     * @param response
     * @param key
     * @param value
     * @param expiry   过期时间（秒）  负数则代表关闭浏览器则失效  0 则是删除
     * @return
     */
    public void setCookies(HttpServletResponse response, String path, String key, String value, int expiry) {
        Cookie cookie = new Cookie(key, StrKit.urlEncode(value, "UTF-8"));
        cookie.setMaxAge(expiry);
        cookie.setPath(path);
        response.addCookie(cookie);
    }

    public void setCookies(HttpServletResponse response, String path, String key, String value) {
        setCookies(response, path, key, value, -1);
    }

    public void setCookies(String path, String key, String value) {
        setCookies(response(), path, key, value, -1);
    }

    public void setCookies(String key, String value) {
        setCookies(response(),"/", key, value, -1);
    }
}
