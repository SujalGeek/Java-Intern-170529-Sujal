package com.example.enotes.dto;

import lombok.Data;

@Data
public class PasswordResetRequest {

	private String token;
	
	private String newPassword;
}
