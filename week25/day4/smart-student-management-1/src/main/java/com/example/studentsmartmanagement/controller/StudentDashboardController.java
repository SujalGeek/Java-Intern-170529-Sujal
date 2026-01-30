package com.example.studentsmartmanagement.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.studentsmartmanagement.dto.ApiResponse;
import com.example.studentsmartmanagement.endpoint.StudentDashboardApi;
import com.example.studentsmartmanagement.service.StudentDashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StudentDashboardController implements StudentDashboardApi{
	
	private final StudentDashboardService studentDashboardService;
	
	
	@Override
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPendingExams(Long studentId, Long courseId) {

		List<Map<String, Object>> exams = studentDashboardService.getPendingExams(studentId, courseId);
		return ResponseEntity.ok(ApiResponse.success(exams, "Found" + exams.size() + "pending exams.", HttpStatus.OK));
	}

	@Override
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMyResults(Long studentId) {
		
		List<Map<String, Object>> results = studentDashboardService.getStudentResults(studentId);
		return ResponseEntity.ok(ApiResponse.success(results, "Found "+ results.size() + "past submissions.", HttpStatus.OK));
	}

	
}
