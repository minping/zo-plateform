package com.zo.platform.starter.session.ignore;

import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhoump
 * @date 2026/11/10
 * @purpose
 */
public class HandlerInfo {


    private HttpServletRequest request;
    private HttpServletResponse response;
    private HandlerMethod handler;
    private String token;

    public HandlerInfo(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, String token) {
        this.request = request;
        this.response = response;
        this.handler = handler;
        this.token = token;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public HandlerMethod getHandler() {
        return handler;
    }

    public String getToken() {
        return token;
    }
}
