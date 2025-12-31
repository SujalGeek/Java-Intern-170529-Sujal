package com.example.enotes.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.enotes.dto.EmailRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Email Management",description = "Operations Related to Sending System emails")
@RequestMapping("/api/v1/email")
public interface EmailEndpoint {
	
	@Operation(summary = "Send Email",description = "Sends a generic email to a specified recipiant")
	@ApiResponses(
			value = {
					@ApiResponse(responseCode = "200",description = "Email Sent Successfully"),
					@ApiResponse(responseCode = "400",description = "Bad Request (Invalid Email Format)"),
					@ApiResponse(responseCode = "500",description = "Internal Server Error (SendGrid/SMTP Failure)")
					})
	@PostMapping("/send")
	public ResponseEntity<?> sendEmail(@RequestBody EmailRequest emailRequest);
	
	

}
