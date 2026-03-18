package com.example.user_service.dto;
import lombok.*;

@Data
public class ResetPasswordRequest {

	private String token;
    private String newPassword;
}
