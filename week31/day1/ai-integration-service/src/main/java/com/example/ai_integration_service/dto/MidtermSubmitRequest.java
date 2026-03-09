package com.example.ai_integration_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class MidtermSubmitRequest {

	private Long midtermId;
	private Long studentId;
	
	private List<QuestionAnswer> answers;
	
	@Data
	public static class QuestionAnswer
	{
		private Long examQuestionId;
		private String studentAnswer;
	}
}
