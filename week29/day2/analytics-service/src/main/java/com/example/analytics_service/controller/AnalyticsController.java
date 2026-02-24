package com.example.analytics_service.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.analytics_service.dto.AnalyticsDTO;
import com.example.analytics_service.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnalyticsController {

	private final AnalyticsService analyticsService;
	
	@GetMapping("/overview")
	public ResponseEntity<AnalyticsDTO> getOverallAnalytics(){
		return ResponseEntity.ok(analyticsService.getOverallAnalytics());
	}
	
	@GetMapping("/course/{courseId}")
	public ResponseEntity<Map<String, Object>> getCourseAnalytics(@PathVariable Long courseId)
	{
		return ResponseEntity.ok(analyticsService.getCourseAnalytics(courseId));
	}
	
	@GetMapping("/student/{studentId}")
    public ResponseEntity<Map<String, Object>> getStudentAnalytics(@PathVariable Long studentId) {
        return ResponseEntity.ok(analyticsService.getStudentAnalytics(studentId));
    }
}
