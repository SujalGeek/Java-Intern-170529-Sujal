package com.example.course_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class CorsConfig implements WebMvcConfigurer {

	@Value("${cors.allowed-origins}")
	private String allowedOrigins;
	
	public void addCorsMapping(CorsRegistry registry)
	{
		registry.addMapping("/**")
		.allowedOrigins(allowedOrigins.split(","))
		.allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
		.allowedHeaders("*")
		.allowCredentials(true);
	}
	
	
}
