package com.example.enotes.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Account Verification",description = "Public Endpoints for account activation")
@RequestMapping("/api/v1/home")
public interface HomeEndpoint {

	@Operation(summary = "Verify Account",description = "Activates a user account")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200",description = "Account Verified SuccessFully"),
					@ApiResponse(responseCode = "400",description = "Invalid or Expired Verification Code"),
					@ApiResponse(responseCode = "500",description = "Internal Server Error")
			})
	@GetMapping("/verify")
	public ResponseEntity<?> verifyAccount(@RequestParam Integer userId,@RequestParam String code) throws Exception;
}
