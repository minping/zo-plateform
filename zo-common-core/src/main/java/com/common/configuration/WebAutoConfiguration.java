package com.common.configuration;

import com.common.filter.LogFilter;
import com.common.util.StrKit;
import com.file.config.UploadProp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
            registry.addResourceHandler("/**").addResourceLocations("file:" + uploadProp.getMetaFilePath());
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
