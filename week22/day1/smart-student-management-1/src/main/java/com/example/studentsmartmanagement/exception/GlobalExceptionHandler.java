package com.example.studentsmartmanagement.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.studentsmartmanagement.dto.ApiResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiResponse<Object>> handleBadCredials(BadCredentialsException ex)
	{
		log.error("Login Failed:{}",ex.getMessage());
		return new ResponseEntity<>(
				ApiResponse.error("Invalid Email or Password", HttpStatus.UNAUTHORIZED),
				HttpStatus.UNAUTHORIZED);
				
	}
	
	
	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleUserNotFound(UsernameNotFoundException ex)
	{
		return new ResponseEntity<>(
				ApiResponse.error(ex.getMessage(),HttpStatus.NOT_FOUND),
				HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String,String>>> handleValidException(MethodArgumentNotValidException ex)
		{
		Map<String, String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
		errors.put(error.getField(), error.getDefaultMessage())
		);
		
		return new ResponseEntity<>(
				ApiResponse.success(errors, "Validation Failed", HttpStatus.BAD_REQUEST),
				HttpStatus.BAD_REQUEST);	
		}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex)
	{
		log.error("Unexpected Error: ");
		return new ResponseEntity<>(
				ApiResponse.error("An unexpected Error: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR),
				HttpStatus.INTERNAL_SERVER_ERROR
				);
	}
}
