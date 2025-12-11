package com.example.user_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Maps the URL "/files/**" to the folder "/app/files/" inside the container
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:/app/files/");
    }
}