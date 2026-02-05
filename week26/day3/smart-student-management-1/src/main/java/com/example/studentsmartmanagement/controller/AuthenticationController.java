package com.example.studentsmartmanagement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.studentsmartmanagement.dto.ApiResponse;
import com.example.studentsmartmanagement.dto.LoginRequest;
import com.example.studentsmartmanagement.dto.LoginResponse;
import com.example.studentsmartmanagement.dto.RefreshTokenRequest;
import com.example.studentsmartmanagement.dto.RegisterRequest;
import com.example.studentsmartmanagement.dto.UserDto;
import com.example.studentsmartmanagement.endpoint.AuthenticationApi;
import com.example.studentsmartmanagement.repository.EnrollmentRepository;
import com.example.studentsmartmanagement.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthenticationController implements AuthenticationApi{

	private final UserService userService;
   

	@Override
	public ResponseEntity<ApiResponse<Boolean>> register(UserDto request) {
		
		Boolean success = userService.register(request);
		if(success)
		{
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ApiResponse.success(true, "User Registered Successfully", HttpStatus.CREATED));
		}
		else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error("Email Already exists", HttpStatus.BAD_REQUEST));
	}
}

	@Override
	public ResponseEntity<ApiResponse<LoginResponse>> login(LoginRequest request) {
		LoginResponse response = userService.login(request);
		return ResponseEntity.ok(ApiResponse.success(response, "Login Successful", HttpStatus.OK));	
	}

	@Override
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(RefreshTokenRequest request) {
        LoginResponse response = userService.refreshToken(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Token Refreshed", HttpStatus.OK));
    }

}
