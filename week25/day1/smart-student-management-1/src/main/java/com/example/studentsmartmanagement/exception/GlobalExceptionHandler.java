package com.example.studentsmartmanagement.exception;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.example.studentsmartmanagement.controller.StudentDashboardController;
import com.example.studentsmartmanagement.dto.ApiResponse;
import com.example.studentsmartmanagement.repository.EnrollmentRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final StudentDashboardController studentDashboardController;

    private final EnrollmentRepository enrollmentRepository;

    GlobalExceptionHandler(EnrollmentRepository enrollmentRepository, StudentDashboardController studentDashboardController) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentDashboardController = studentDashboardController;
    }

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
	
	
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException ex)
	{
		return new ResponseEntity<>(
				ApiResponse.error(ex.getMessage(), HttpStatus.BAD_REQUEST)
				,HttpStatus.BAD_REQUEST);
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
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException ex)
	{
		return new ResponseEntity<>(
				ApiResponse.error(ex.getMessage(), HttpStatus.NOT_FOUND),
				HttpStatus.NOT_FOUND);
				
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse<Object>> handleAccessDenied(AccessDeniedException ex)
	{
		return new ResponseEntity<>(
				ApiResponse.error("Access Denied: You dont have permission to perform this action.",HttpStatus.FORBIDDEN),
				HttpStatus.FORBIDDEN);
	}
	
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Object>> handleBadJson(HttpMessageNotReadableException ex)
	{
		return new ResponseEntity<>(
				ApiResponse.error("Malformed JSON request. Check your data format.", HttpStatus.BAD_REQUEST),
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
