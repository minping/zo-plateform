package com.common.configuration;

import com.zo.platform.sdk.common.Result;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * SPA fallback：所有未匹配的请求（404 等）统一转发到 index.html，
 * 由 vue-router 接管。这是 Spring Boot 部署 SPA 的标准做法。
 *
 */
@Controller
public class SpaErrorController implements ErrorController {

    @RequestMapping("/error")
    @ResponseBody
    public Object handleError(HttpServletRequest request, HttpServletResponse response) {
        String accept = request.getHeader("Accept");
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) statusCode = 500;

        // json请求，接口
        if (accept != null && accept.contains("application/json")) {
            return Result.me().error().setState(statusCode).setMessage(getStatusMsg(statusCode));
        }
        // html页面请求，SPA转发
        return "forward:/index.html";
    }

    private String getStatusMsg(Integer code) {
        switch (code) {
            case 404: return "接口不存在";
            case 405: return "请求方法不允许";
            case 403: return "权限不足";
            case 400: return "参数错误";
            default: return "服务器异常";
        }
    }
}