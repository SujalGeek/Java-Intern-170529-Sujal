package com.springcore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration()
public class AppConfig {

	@Bean(name = "com2")
	// the list of name can be used
//	@Bean(name ={"com2","desktop","Beast"}
	@Scope("prototype")
	public Student studentBean() {
		return new Student("Sujal Morwani","MP");
		
	}
}
