package com.example.assignment_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class AssignmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AssignmentServiceApplication.class, args);
	}

}
