package com.example.studentsmartmanagement.service;

import com.example.studentsmartmanagement.dto.LoginRequest;
import com.example.studentsmartmanagement.dto.LoginResponse;
import com.example.studentsmartmanagement.dto.RefreshTokenRequest;
import com.example.studentsmartmanagement.dto.UserDto;

public interface UserService {

	Boolean register(UserDto userDto);
	LoginResponse login(LoginRequest loginRequest);
	LoginResponse refreshToken(RefreshTokenRequest request);
	void sendRestPasswordEmail(String email);
	void resetPassword(String token,String newPassword);
}
