package com.example.studentsmartmanagement.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.studentsmartmanagement.dto.ApiResponse;
import com.example.studentsmartmanagement.endpoint.AnalyticsApi;
import com.example.studentsmartmanagement.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AnalyticsController implements AnalyticsApi {

	private final AnalyticsService analyticsService;
	
	
	@Override
	public ResponseEntity<ApiResponse<Map<String, Object>>> predictPerformance(Long studentId) {
	
		Map<String, Object> analysis = analyticsService.getStudentRiskAnalysis(studentId);
		
		return ResponseEntity.ok(ApiResponse.success(analysis, "Performance Prediction Generated", HttpStatus.OK));
	}

}
