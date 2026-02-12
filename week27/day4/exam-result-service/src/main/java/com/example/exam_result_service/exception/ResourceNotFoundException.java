package com.example.exam_result_service.exception;

public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String message)
	{
		super(message);
	}
}
