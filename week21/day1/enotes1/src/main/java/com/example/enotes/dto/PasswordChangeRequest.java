package com.example.enotes.dto;

import lombok.Data;

@Data
public class PasswordChangeRequest {

	private String oldPassword;
	private String newPassword;
	
}
