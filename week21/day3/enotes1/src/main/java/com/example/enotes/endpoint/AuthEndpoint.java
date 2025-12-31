package com.example.enotes.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.enotes.dto.LoginRequest;
import com.example.enotes.dto.PasswordResetRequest;
import com.example.enotes.dto.UserDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Authentication", description = "Authentication and Account Management Operations")
@RequestMapping("/api/v1/auth")
public interface AuthEndpoint {

	@Operation(summary = "Register a New User", description = "Creates a new user account in the system")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "201", description = "Register Success"),
					@ApiResponse(responseCode = "400", description = "Bad Request(Validation Failed or Email Exists)"),
					@ApiResponse(responseCode = "500", description = "Internal Server Error")
			})
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody UserDto userDto);
	
	
	@Operation(summary = "User Login", description = "Authenticates user credientals and returns a JWT token")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200",description = "Login Success"),
					@ApiResponse(responseCode = "400",description = "Invalid Email or Password"),
					@ApiResponse(responseCode = "500", description = "Internal Server Error")
			})
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception;
	
	@Operation(summary = "Forgot Password", description = "Sends a password reset link to the registered email")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200",description = "Reset Link Sent Successfully"),
					@ApiResponse(responseCode = "404", description = "Email Not found"),
					@ApiResponse(responseCode = "500",description = "Internal Server Error")
			})
	@PostMapping("/forgot-password")
	public ResponseEntity<?> forgotPassword(@RequestParam String email);
	
	
	@Operation(summary = "Reset Password",description = "Resets the password using the token sent via email")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200",description = "Password Reset Successfully"),
					@ApiResponse(responseCode = "400",description = "Invalid Token or Password mismatch"),
					@ApiResponse(responseCode = "500",description = "Internal Server Error")
			})
	@PostMapping("/reset-password")
	public ResponseEntity<?> resetPassword(@RequestBody PasswordResetRequest request);
	
	
	
}
