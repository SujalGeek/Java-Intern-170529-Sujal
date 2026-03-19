package com.example.ai_integration_service.config;


import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {
    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // 🔥 This is the missing link! 
            // It bypasses the "MissingRequestHeaderException" in the Performance Controller.
            requestTemplate.header("X-User-Role", "1"); // Role 1 = Admin/System
            requestTemplate.header("X-User-Id", "1");
        };
    }
}
