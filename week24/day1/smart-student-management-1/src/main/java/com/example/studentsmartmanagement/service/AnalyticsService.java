package com.example.studentsmartmanagement.service;

import java.util.Map;

public interface AnalyticsService {

	public Map<String, Object> getStudentRiskAnalysis(Long studentId);
	
}
