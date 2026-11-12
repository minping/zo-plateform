package com.zo.platform.starter.session.configuration;

import com.zo.platform.starter.console.version.Version;
import com.zo.platform.starter.session.CookieManager;
import com.zo.platform.starter.session.UserInfoSession;
import com.zo.platform.starter.session.config.CookieConfig;
import com.zo.platform.starter.session.config.SessionConfig;
import com.zo.platform.starter.session.constant.SessionConstant;
import com.zo.platform.starter.session.jwt.JwtFilter;
import com.zo.platform.starter.session.jwt.JwtInfoSessionHolder;
import com.zo.platform.starter.session.jwt.JwtInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.FilterRegistration;

/**
 * @author zhoump
 * @date 2026/11/7
 * @purpose
 */
@Configuration
@EnableConfigurationProperties({SessionConfig.class, CookieConfig.class})
public class SessionAutoConfiguration {

    @Bean
    public Version sessionVersion(){
        return new Version(SessionConstant.VERSION_NAME,SessionConstant.VERSION_CODE);
    }

    @Bean
    @ConditionalOnMissingBean
    public JwtInfoSessionHolder jwtInfoHolder() {
        return new JwtInfoSessionHolder();
    }

    @Bean
    public CookieManager cookieManager(){
        return new CookieManager();
    }

    @Bean
    public JwtFilter jwtFilter(){
        return new JwtFilter();
    }

    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }


    @Bean
    public SessionWebMvcConfigurer sessionWebMvcConfigurer(JwtInterceptor jwtInterceptor) {
        return new SessionWebMvcConfigurer(jwtInterceptor);
    }

    @Bean
    public FilterRegistrationBean filterRegistrationBean(JwtFilter jwtFilter){
        FilterRegistrationBean registrationBean = new FilterRegistrationBean(jwtFilter);
        //过滤所有路径
        registrationBean.addUrlPatterns("/*");
        registrationBean.setName("jwtFilter");
        registrationBean.setOrder(SessionConstant.FILTER_ORDER);
        return registrationBean;
    }

    @Bean
    public UserInfoSession userInfoSession(){
        return new UserInfoSession();
    }
}
