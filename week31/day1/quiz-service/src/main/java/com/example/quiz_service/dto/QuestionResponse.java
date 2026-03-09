package com.example.quiz_service.dto;

import lombok.Data;

@Data
public class QuestionResponse {

	private Long questionId;
	private String questionText;
	private Integer marks;
	private String bloomLevel;
}
