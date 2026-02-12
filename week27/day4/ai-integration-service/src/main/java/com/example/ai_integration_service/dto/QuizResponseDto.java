package com.example.ai_integration_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizResponseDto {

	private String status;
	private String concept;
	private String bloom_level;
	private int question_count;
	private List<String> generated_questions;
	
}
