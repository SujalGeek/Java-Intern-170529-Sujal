package com.example.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
    
    @Bean
    public RouteLocator customRoute(RouteLocatorBuilder builder)
    {
        return builder.routes()
        .route("quiz-service",r->r
            .path("/quiz/**")
            .uri("lb://quiz-service")
        )
        .route("question-service",r->r
            .path("/question/**")
            .uri("lb://question-service")
        )
        .build();
    }
}
