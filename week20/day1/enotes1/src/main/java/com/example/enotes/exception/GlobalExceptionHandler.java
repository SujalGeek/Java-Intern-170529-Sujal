package com.example.enotes.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import java.io.FileNotFoundException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.example.enotes.Enotes1Application;
import com.example.enotes.util.CommonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleException(Exception e)
	{
		log.error("GlobalExceptionHandler :: handleException ::",e.getMessage());
		return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(NullPointerException.class)
	public ResponseEntity<?> handleNullPointerException(Exception e)
	{
		log.error("GlobalExceptionHandler :: handleNullPointerException ::",e.getMessage());
		return new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR); 
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<?> handleResorceNotFoundException(Exception e)
	{
		log.error("GlobalExceptionHandler :: handleResourceNotFoundException ::",e.getMessage());
		return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
	}
	
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String,String>> handleValidationExceptions(MethodArgumentNotValidException ex)
	{
		Map<String,String> errors = new LinkedHashMap<>();
		
		ex.getBindingResult().getAllErrors().forEach((error) ->{
			String fieldName = ((FieldError) error).getField();
			String errorMessage = error.getDefaultMessage();
			errors.put(fieldName, errorMessage);
		});
		return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(FileNotFoundException.class)
	public ResponseEntity<?> handleFileNotFoundException(FileNotFoundException e)
	{
		return CommonUtil.createErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
	}
	
	
}

