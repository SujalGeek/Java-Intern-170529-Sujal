package com.example.performance_service.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PerformanceResponseDTO {

	private Long performanceId;
	private Long studentId;
	private Long courseId;
	
	private BigDecimal finalScore;
	private String finalGrade;
	
	private String predictedGrade;
	private String riskLevel;
	private BigDecimal predictionConfidence;
	
	private String academicStatus;
	
	private String diagnosticFeedback;
	private String bloomAnalysis;
	
	
}
