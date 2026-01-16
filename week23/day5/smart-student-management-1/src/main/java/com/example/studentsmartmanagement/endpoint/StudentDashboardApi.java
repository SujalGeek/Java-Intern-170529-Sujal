package com.example.studentsmartmanagement.endpoint;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.studentsmartmanagement.dto.ApiResponse;

@RequestMapping("/api/student-dashboard")
public interface StudentDashboardApi {

	@GetMapping("/pending-exams")
	public ResponseEntity<ApiResponse<List<Map<String,Object>>>> getPendingExams(@RequestParam Long studentId,@RequestParam Long courseId);
	
	@GetMapping("/my-results")
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getMyResults(@RequestParam Long studentId);
	
}
