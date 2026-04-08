package com.example.quiz_service.dto;

import java.util.List;

import lombok.Data;

@Data
public class QuizResponse {

	private Long quizId;
	private Long courseId;
	private Integer totalMarks;
	private List<QuestionResponse> questions;
}
