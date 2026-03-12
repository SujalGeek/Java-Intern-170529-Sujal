package com.example.quiz_service.dto;

import lombok.Data;

@Data
public class GenerateQuizRequest {

	private Long courseId;
	private String description;
	
}
