package com.springcore.javaconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.springcore.javaconfig")
public class JavaConfig {
	
	@Bean
	public Test getTest() {
		Test t = new Test();
		return t;
	}
	@Bean
	public Student getStudent() {
		Student st = new Student(getTest());
		return st;
	}
	
}
