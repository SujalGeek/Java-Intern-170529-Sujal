package com.example.performance_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.performance_service.dto.PerformanceDTO;
import com.example.performance_service.dto.PerformanceResponseDTO;
import com.example.performance_service.entity.Performance;
import com.example.performance_service.service.PerformanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/performance")
@CrossOrigin("*")
@RequiredArgsConstructor
public class PerformanceController {

	private final PerformanceService performanceService;
	
	@PostMapping("/upsert")
	public ResponseEntity<PerformanceResponseDTO> upsert(@RequestBody PerformanceDTO dto)
	{
		Performance performance = performanceService.upsertPerformance(dto);
		return ResponseEntity.ok(
				performanceService.getPerformance(performance.getStudentId(), 
						performance.getCourseId())
				);
	}
	
	@GetMapping("/{studentId}/{courseId}")
	public ResponseEntity<PerformanceResponseDTO> getPerformance(
	        @PathVariable Long studentId,
	        @PathVariable Long courseId) {

	    return ResponseEntity.ok(
	            performanceService.getPerformance(studentId, courseId)
	    );
	}
	
	
}
