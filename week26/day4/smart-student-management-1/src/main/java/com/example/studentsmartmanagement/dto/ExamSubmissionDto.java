package com.example.studentsmartmanagement.dto;

import java.util.List;

import lombok.Data;

@Data
public class ExamSubmissionDto {

	private Long studentId;
	
	private Long assignmentId;
	
	private List<StudentAnswerDto> answers;
	
	@Data
	public static class StudentAnswerDto{
		private Long questionId;
		
		private String selectedAnswer;
	}
}
