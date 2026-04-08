package com.example.quiz_service.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class SubmitQuizRequest {

	private Long quizId;
	
//	private Long studentId;
	
	private Map<Long,String> answers;
}
