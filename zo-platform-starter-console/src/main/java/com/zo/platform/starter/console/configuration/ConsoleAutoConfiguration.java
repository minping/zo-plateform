package com.zo.platform.starter.console.configuration;

import com.zo.platform.starter.console.constant.ConsoleConstant;
import com.zo.platform.starter.console.runner.ConsoleApplicationRunner;
import com.zo.platform.starter.console.version.Version;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhoump
 * @date 2026/11/6
 * @purpose
 */
@Configuration
public class ConsoleAutoConfiguration {

    @Bean
    public Version consoleVersion() {
        return new Version(ConsoleConstant.VERSION_NAME, ConsoleConstant.VERSION_CODE);
    }

    @Bean
    public ConsoleApplicationRunner applicationRunner(){
        return new ConsoleApplicationRunner();
    }
}
