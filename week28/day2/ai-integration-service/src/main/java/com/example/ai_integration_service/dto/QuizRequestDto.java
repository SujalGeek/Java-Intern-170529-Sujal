package com.example.ai_integration_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizRequestDto {

	private String concept;
	private String description;
	private List<String> keywords;
	private String bloom_level;
	private int count;
	
}
