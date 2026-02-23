package com.example.analytics_service.dto;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Data;

@Data
public class AnalyticsDTO {

	private long totalStudents;
	private long totalCourses;
	private long totalEnrollments;
	
	private BigDecimal averageAttendance;
	private BigDecimal averageExamScore;
	private BigDecimal averageAssignmentScore;
	
	private Map<String, Long> riskDistribution;
	private Map<String,Long> gradeDistribution;
}
