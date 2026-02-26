package com.example.quiz_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class SubmitQuizRequest {

	private Long quizId;
	
	private Long studentId;
	
	private List<String> answers;
}
