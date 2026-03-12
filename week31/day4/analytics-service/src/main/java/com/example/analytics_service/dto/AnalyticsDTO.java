package com.example.analytics_service.dto;

import java.math.BigDecimal;
import java.util.Map;

import lombok.Data;

@Data
public class AnalyticsDTO {

    private long totalStudents;       // React uses this ✅
    private long totalCourses;
    private long totalEnrollments;
    
    private BigDecimal averageAttendance;
    private BigDecimal averageScore;  // 🔥 Added for React ✅
    private BigDecimal averageAssignmentScore;
    private BigDecimal averageFinalScore;
    
    private long highRiskCount;       // 🔥 Added for React ✅
    
    private Map<String, Long> riskDistribution;
    private Map<String,Long> gradeDistribution;
}