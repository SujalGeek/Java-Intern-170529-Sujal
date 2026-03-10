package com.example.assignment_service.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDetailsResponse {

	private Long assignmentId;
	private Long courseId;
	private Integer totalMarks;
	private List<QuestionDto> questions;
	
}
