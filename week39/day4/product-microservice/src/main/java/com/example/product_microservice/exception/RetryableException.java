package com.example.product_microservice.exception;

public class RetryableException extends RuntimeException{

	public RetryableException(String message)
	{
		super(message);
	}
}
