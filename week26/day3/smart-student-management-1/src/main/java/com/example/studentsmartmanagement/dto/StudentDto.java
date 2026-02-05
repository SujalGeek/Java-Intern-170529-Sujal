package com.example.studentsmartmanagement.dto;

import lombok.Data;

@Data
public class StudentDto {

	private Long studentId;
	
	private String firstName;
	
	private String lastName;
	
	private String email;
	
	private String department;
	
	private Integer currentSemester;
	
}
