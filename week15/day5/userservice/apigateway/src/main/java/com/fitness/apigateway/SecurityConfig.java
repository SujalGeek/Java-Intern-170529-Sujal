package com.fitness.apigateway;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity httpSecurity)
    {
        return httpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange->
                {
                    exchange.anyExchange().authenticated();
                }).oauth2ResourceServer(oauth2-> oauth2.jwt(Customizer.withDefaults()))
                .build();
    }
}
