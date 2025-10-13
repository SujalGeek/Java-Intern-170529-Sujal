package com.codingshuttle.youtube.hospitalManagement.security;

import com.codingshuttle.youtube.hospitalManagement.entity.type.RoleType;
import com.codingshuttle.youtube.hospitalManagement.service.OAuth2SuccessHandler;
import org.objectweb.asm.Handle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerExceptionResolver;

import static com.codingshuttle.youtube.hospitalManagement.entity.type.RoleType.ADMIN;
import static com.codingshuttle.youtube.hospitalManagement.entity.type.RoleType.DOCTOR;

@Configuration
public class WebSecurityConfig {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public WebSecurityConfig(OAuth2SuccessHandler oAuth2SuccessHandler, HandlerExceptionResolver handlerExceptionResolver) {
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**","/auth/**", "/oauth2/**")
//                        .requestMatchers("/doctors/**").hasAnyRole(DOCTOR.name(), ADMIN.name())
                        .permitAll()
                                .requestMatchers("/admin/**").hasRole(ADMIN.name())
                                .requestMatchers("/doctors/**").hasAnyRole(DOCTOR.name(),ADMIN.name())
                                .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler)
                )
                .exceptionHandling(exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer.accessDeniedHandler(((request, response, accessDeniedException) -> {
                            handlerExceptionResolver.resolveException(request,response,null,accessDeniedException);
                                })
                        ))
        ;

        return http.build();
    }
}
