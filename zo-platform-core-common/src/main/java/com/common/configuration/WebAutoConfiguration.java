package com.common.configuration;

import com.common.filter.LogFilter;
import com.file.config.UploadProp;
import com.zo.platform.sdk.common.kit.StrKit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.PathResourceResolver;

import java.io.IOException;

/**
 * @author zhoump
 * @date 2026/6/23
 * @purpose
 */
@Configuration
public class WebAutoConfiguration implements WebMvcConfigurer {

    UploadProp uploadProp;

    @Autowired
    public void setUploadProp(UploadProp uploadProp) {
        this.uploadProp = uploadProp;
    }

    /**
     * 日志过滤器
     *
     * @return {@link LogFilter}
     */
    @Bean
    public LogFilter logFilter() {
        return new LogFilter();
    }

    /**
     * Add handlers to serve static resources such as images, js, and, css
     * files from specific locations under web application root, the classpath,
     * and others.
     *
     * @param registry
     * @see ResourceHandlerRegistry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (StrKit.isNotBlank(uploadProp.getMetaFilePath())){
            registry.addResourceHandler("/**").addResourceLocations("file:" + uploadProp.getMetaFilePath())
                    .resourceChain(true)
                    .addResolver(new PathResourceResolver() {
                        @Override
                        protected Resource getResource(String resourcePath, Resource location) throws IOException {
                            // 先用父类逻辑取真实文件（保留目录穿越等安全校验）
                            Resource resource = super.getResource(resourcePath, location);
                            if (resource != null) {
                                return resource;
                            }
                            // 无扩展名 → 视为前端路由，回退 index.html 交给 vue-router
                            if (!resourcePath.contains(".")) {
                                return super.getResource("index.html", location);
                            }
                            return null;   // 真缺的 js/css/图片仍返回 404
                        }
                    });;
        }
    }

    /**
     * 访问 /website/（不带 index.html）时转发到首页。
     * 注：自定义 /** 资源映射会使 Spring Boot 默认的欢迎页失效，所以需要手动补这个转发。
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}
