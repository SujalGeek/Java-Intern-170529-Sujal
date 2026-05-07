package com.example.product_microservice.exception;

public class NonRetryableException extends RuntimeException{

	public NonRetryableException(String message)
	{
		super(message);
	}
}
