package com.example.product_microservice;

import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import jakarta.annotation.PostConstruct;

@SpringBootTest
class ProductMicroserviceApplicationTests {

	@Test
	void contextLoads() {
	}
	
	@PostConstruct
	public void init() {
	    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
	}

}
