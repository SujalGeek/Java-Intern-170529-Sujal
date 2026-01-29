package com.example.studentsmartmanagement.endpoint;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.studentsmartmanagement.dto.ApiResponse;

@RequestMapping("/api/analytics")
public interface AnalyticsApi {

	@GetMapping("/predict/{studentId}")
	public ResponseEntity<ApiResponse<Map<String, Object>>> predictPerformance(@PathVariable Long studentId);
	
}
