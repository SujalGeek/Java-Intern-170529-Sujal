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

	@ExceptionHandler(ExamResultException.class)
	public ResponseEntity<Map<String, String>> handleExamResultException(ExamResultException ex)
	{
	
		Map<String, String> error = new HashMap<>();
//		error.put("timestamp", LocalDateTime.now());
//		error.put("error", "NOT_FOUND");
//		error.put("message", ex.getMessage());
		error.put("message", ex.getMessage());
		return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);	
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleGeneralException(Exception ex)
	{
		Map<String, String> error = new HashMap<>();
		
//		error.put("timestamp", LocalDateTime.now());
//		error.put("error", "INTERNAL_SERVER_ERROR");
//		error.put("message", ex.getMessage());
		error.put("message", "Internal Server Error");
		
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
	}
}
