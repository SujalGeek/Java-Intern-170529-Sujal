package com.example.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator customRoute(RouteLocatorBuilder builder) {
        return builder.routes()

            // QUIZ SERVICE
            .route("quiz-service", r -> r
                .path("/quiz/**")
                .uri("lb://quiz-service")
            )

            // QUESTION SERVICE
            .route("question-service", r -> r
                .path("/question/**")
                .uri("lb://question-service")
            )

            // USER SERVICE
            .route("user-service", r -> r
                .path("/auth/**", "/student/**", "/admin/**", "/files/**", "/oauth2/**", "/login/**")
                .filters(f -> f
                    // RETAIN_UNIQUE prevents the "multiple values" error
                    // by stripping the duplicate header coming from the User Service
                    .dedupeResponseHeader("Access-Control-Allow-Origin", "RETAIN_UNIQUE")
                    .dedupeResponseHeader("Access-Control-Allow-Credentials", "RETAIN_UNIQUE")
                )
                .uri("lb://user-service")
            )
            .build();
    }
}