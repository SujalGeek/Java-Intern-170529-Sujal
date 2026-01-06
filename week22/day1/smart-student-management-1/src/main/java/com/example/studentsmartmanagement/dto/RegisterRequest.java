package com.example.studentsmartmanagement.dto;

import com.example.studentsmartmanagement.entity.Role;

import lombok.Data;

@Data
public class RegisterRequest {

	private String firstName;
	
	private String lastName;
	
	private String username;
	
	private String password;
	
	private Role role;
	
	private String department;
}
