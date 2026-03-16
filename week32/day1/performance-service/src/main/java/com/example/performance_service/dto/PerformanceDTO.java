package com.example.performance_service.dto;

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
	// Add these to your Performance DTO!
	private BigDecimal finalScore;
	private String finalGrade;
	private String predictedGrade;
	private String riskLevel;
	private BigDecimal predictionConfidence;
	private String bloomAnalysis;
	private String diagnosticFeedback;
	private String academicStatus;
	private Long updatedBy;
}
