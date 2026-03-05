package com.example.user_service.controller;


import jakarta.validation.Valid; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.user_service.dto.LoginRequest;
import com.example.user_service.dto.LoginResponse;
import com.example.user_service.dto.UserDTO;
import com.example.user_service.entity.User;
import com.example.user_service.service.AuthService;
import com.example.user_service.service.UserService;
import com.example.user_service.dto.*;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
        	System.out.println("Here it is going on!!");
            LoginResponse response = authService.login(loginRequest);
            System.out.println("Here after the login respnse going on!!!");
            return ResponseEntity.ok(response);            
        } catch (RuntimeException e) {
        	System.out.println("Here it is not working: "+e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO) {
        try {
            User user = userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Collections.singletonMap("message", "User registered successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
          
            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            boolean isValid = authService.validateToken(jwtToken);
            return ResponseEntity.ok(Collections.singletonMap("valid", isValid));
        } catch (Exception e) {
            return ResponseEntity.ok(Collections.singletonMap("valid", false));
        }
    }
    
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authService.processForgotPassword(request.getEmail());
        return ResponseEntity.ok("If an account exists, a reset token has been generated.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        authService.updatePassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("Password updated successfully.");
    }
}