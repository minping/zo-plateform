package com.zo.platform.starter.session.configuration;

import com.zo.platform.starter.session.jwt.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author zhoump
 * @date 2026/11/10
 * @purpose
 */
public class SessionWebMvcConfigurer implements WebMvcConfigurer {

    JwtInterceptor jwtInterceptor;

    @Autowired
    public SessionWebMvcConfigurer(JwtInterceptor jwtInterceptor){
        this.jwtInterceptor = jwtInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor).addPathPatterns("/**").order(getOrder(jwtInterceptor));
    }

    private int getOrder(Object obj){
        Order order = obj.getClass().getAnnotation(Order.class);
        return order == null ? 100 : order.value();
    }

}
