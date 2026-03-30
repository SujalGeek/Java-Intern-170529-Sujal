package com.example.course_service.dto;

import java.util.Date;

import lombok.Data;

@Data
public class SubmissionDto {

	private Long submissionId;
	
	private Long assignmentId;
	
	private Long studentId;
	
	private String studentAnswer;
	
	private String aiFeedback;
	
	private Double gradeObtained;
	
	private String status;
	
	private Date submittedAt;
}
