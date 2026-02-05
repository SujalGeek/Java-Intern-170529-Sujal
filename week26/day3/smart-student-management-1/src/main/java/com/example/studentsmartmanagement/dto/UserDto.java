package com.example.studentsmartmanagement.dto;

import com.example.studentsmartmanagement.entity.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

	private Long id;
	
	@NotBlank(message = "First Name is Required")
	private String firstName;
	
	@NotBlank(message = "Last Name is Required")
	private String lastName;
	
	@NotBlank(message = "Email is Required")
	@Email(message = "Invlaid email format")
	private String email;
	
	@NotBlank(message = "Password is Required")
	private String password;
	
	@NotNull(message ="Role is Required")
	private Role role;
	
	private String department;
	
	private String qualification;
	
	private Integer currentSemester;
}
