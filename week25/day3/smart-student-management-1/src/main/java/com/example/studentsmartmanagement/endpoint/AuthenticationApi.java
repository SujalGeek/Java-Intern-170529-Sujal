package com.example.studentsmartmanagement.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.studentsmartmanagement.dto.ApiResponse;
import com.example.studentsmartmanagement.dto.LoginRequest;
import com.example.studentsmartmanagement.dto.LoginResponse;
import com.example.studentsmartmanagement.dto.RefreshTokenRequest;
import com.example.studentsmartmanagement.dto.RegisterRequest;
import com.example.studentsmartmanagement.dto.UserDto;

@RequestMapping("/api/v1/auth")
public interface AuthenticationApi {

	@PostMapping("/register")
	ResponseEntity<ApiResponse<Boolean>> register(@RequestBody UserDto request);
	
	@PostMapping("/login")
	ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request);

	@PostMapping("/refresh")
	ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@RequestBody RefreshTokenRequest request);
	}
