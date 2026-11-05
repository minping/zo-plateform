package com.zo.platform.starter.exception.configuration;

import com.zo.platform.starter.exception.advice.GlobalExceptionHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhoump
 * @date 2026/11/3
 * @purpose
 */
@Configuration
public class ExceptionAutoConfiguration {

    @Bean
    @ConditionalOnClass(name="org.springframework.web.bind.annotation.ControllerAdvice")
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }

}
