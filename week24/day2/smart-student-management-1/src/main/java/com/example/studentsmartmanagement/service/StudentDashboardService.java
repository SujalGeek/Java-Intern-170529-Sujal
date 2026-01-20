package com.example.studentsmartmanagement.service;

import java.util.List;
import java.util.Map;

public interface StudentDashboardService {

	// 1. Get Exams that are waiting to be taken
	public List<Map<String,Object>> getPendingExams(Long studentId,Long courseId); 

	// 2. Get Past Results (History)
	public List<Map<String, Object>> getStudentResults(Long studentId);
}
