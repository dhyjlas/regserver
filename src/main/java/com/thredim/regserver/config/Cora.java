package com.thredim.regserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

/**
 * 解决跨域问题
 */
@Configuration
public class Cora implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //注册自定义拦截器，添加拦截路径和排除拦截路径
        registry.addInterceptor(new InterceptorConfig())
                .addPathPatterns("/server/**", "/check")
                .excludePathPatterns("/registrar", "/auth");

        registry.addInterceptor(new CoraConfig())
                .addPathPatterns("/auth");
    }
}