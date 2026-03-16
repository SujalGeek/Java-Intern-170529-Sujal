package com.example.ai_integration_service.dto;

import lombok.Data;

@Data
public class GenerateQuestionRequest {

	private Long courseId;
	private String concept;
	private String bloomLevel;
	private String difficulty;
	private int count;
	
}
