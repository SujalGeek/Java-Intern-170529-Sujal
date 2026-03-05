package com.example.ai_integration_service.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PerformanceDTO {

	private Long studentId;
	private Long courseId;
	
	private BigDecimal quizAverage;
	private BigDecimal assignmentAverage;
	private BigDecimal midtermScore;
	private BigDecimal attendancePercentage;
	
	private Integer participationScore;
	private Integer studyHoursPerWeek;
	
	private BigDecimal previousGpa;
	
	private Long updatedBy;
}
