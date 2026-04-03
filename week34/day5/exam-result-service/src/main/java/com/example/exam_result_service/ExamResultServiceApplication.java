package com.example.exam_result_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ExamResultServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ExamResultServiceApplication.class, args);
	}

}
