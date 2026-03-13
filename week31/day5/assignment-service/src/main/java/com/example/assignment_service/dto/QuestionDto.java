package com.example.assignment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDto {

	private Long questionId;
	private String questionText;
	private Integer marks;
	private String bloomLevel;
	private String referenceAnswer;
	
}
