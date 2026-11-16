package com.zo.platform.starter.log.configuration;

import com.zo.platform.starter.console.version.Version;
import com.zo.platform.starter.log.constant.LogConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhoump
 * @date 2026/11/6
 * @purpose
 */
@Configuration
public class LogAutoConfiguration {

    @Bean
    public Version logVersion(){
        return new Version(LogConstant.VERSION_NAME,LogConstant.VERSION_CODE);
    }
}
