package com.example.course_service.dto;

import java.util.Date;

import lombok.Data;

@Data
public class EnrollmentDto {

	private Long enrollmentId;
	
	private Long studentId;
	
	private Long courseId;
	
	private String status;
	
	private Date enrollmentDate;
}
