package com.example.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*") // 허용할 출처 : 특정 도메인만 받을 수 있음
                .allowedMethods("GET", "POST", "PUT", "PATCH","DELETE", "OPTIONS"); // 허용할 HTTP method
    }
}