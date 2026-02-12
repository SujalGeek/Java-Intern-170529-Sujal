package com.example.exam_result_service.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.example.exam_result_service.repository.ExamResultRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ExamResultRepository examResultRepository;

    GlobalExceptionHandler(ExamResultRepository examResultRepository) {
        this.examResultRepository = examResultRepository;
    }

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> handleResouceNotFound(ResourceNotFoundException ex)
	{
	
		Map<String, Object> error = new HashMap<>();
		error.put("timestamp", LocalDateTime.now());
		error.put("error", "NOT_FOUND");
		error.put("message", ex.getMessage());
							
		return new ResponseEntity<>(error,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGeneric(Exception ex)
	{
		Map<String, Object> error = new HashMap<>();
		
		error.put("timestamp", LocalDateTime.now());
		error.put("error", "INTERNAL_SERVER_ERROR");
		error.put("message", ex.getMessage());
		
		return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
