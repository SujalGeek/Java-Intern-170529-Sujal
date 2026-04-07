package com.example.course_service.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex)
	{
		return buildResponse(ex.getMessage(),HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(BusinessRuleException.class)
	public ResponseEntity<?> handleBusinessRule(BusinessRuleException ex)
	{
		return buildResponse(ex.getMessage(),HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGlobal(Exception ex)
	{
		return buildResponse("Internal Server Error: " + ex.getMessage() , HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<?> buildResponse(String message, HttpStatus status) {
		Map<String, Object> error = new HashMap<>();
		error.put("timestamp", LocalDateTime.now());
		error.put("status", status.value());
		error.put("error", status.getReasonPhrase());
		error.put("message", message);
	
		return new ResponseEntity<>(error,status);
	}
	
	
	
	
}
