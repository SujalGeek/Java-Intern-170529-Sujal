package com.example.enotes.service;

import com.example.enotes.dto.PasswordChangeRequest;
import com.example.enotes.dto.UserDto;
import com.example.enotes.entities.User;

public interface UserService {

	public Boolean register(UserDto userDto);
	
	public void changePassword(User user,PasswordChangeRequest request);
	
	public void sendResetPasswordEmail(String email);
	
	public void resetPassword(String token, String newPassword);
}
