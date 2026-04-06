package com.example.ai_integration_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

	@Bean
	@LoadBalanced
	@Primary
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Bean(name = "externalRestTemplate")
	public RestTemplate externalRestTemplate() {
		return new RestTemplate();
	}
}
