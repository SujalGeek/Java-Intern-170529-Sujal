package com.example.user_service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleException(RuntimeException ex)
    {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
