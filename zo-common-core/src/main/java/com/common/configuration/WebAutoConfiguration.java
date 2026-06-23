package com.common.configuration;

import com.common.filter.LogFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhoump
 * @date 2026/6/23
 * @purpose
 */
@Configuration
public class WebAutoConfiguration implements WebMvcConfigurer {

    /**
     * 日志过滤器
     *
     * @return {@link LogFilter}
     */
    @Bean
    public LogFilter logFilter() {
        return new LogFilter();
    }
}
