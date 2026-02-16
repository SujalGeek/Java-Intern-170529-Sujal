package com.example.course_service.dto;

import java.util.Date;

import lombok.Data;

@Data
public class AssignmentDto {

	private Long assignmentId;
	
	private Long courseId;
	
	private String title;
	
	private String description;
	
	private String correctAnswer;
	
	private Integer maxScore;
	
	private Date dueDate;
}
