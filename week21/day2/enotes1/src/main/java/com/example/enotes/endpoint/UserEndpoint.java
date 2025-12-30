package com.example.enotes.endpoint;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.enotes.dto.PasswordChangeRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "User Management",description = "Operations related to user profile and settings")
@RequestMapping("/api/v1/user")
public interface UserEndpoint {

	@Operation(summary =  "Change Password",description = "Update the Logged in user's password")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200",description = "Password Changed SuccessFully"),
					@ApiResponse(responseCode = "400",description = "Invalid Old Password"),
					@ApiResponse(responseCode = "500",description = "Internal Server Error")
			})
	@PostMapping("/change-password")
	public ResponseEntity<?> changePassword(@RequestBody PasswordChangeRequest request, Principal principal);
	
}
