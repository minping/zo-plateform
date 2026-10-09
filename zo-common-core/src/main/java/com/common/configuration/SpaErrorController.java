package com.common.configuration;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * SPA fallback：所有未匹配的请求（404 等）统一转发到 index.html，
 * 由 vue-router 接管。这是 Spring Boot 部署 SPA 的标准做法。
 *
 * 注意：会一并把 5xx 等错误也转成 index.html；通常 SPA 业务场景可接受。
 */
@Controller
public class SpaErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError() {
        return "forward:/index.html";
    }
}