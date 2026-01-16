package com.example.studentsmartmanagement.endpoint;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.studentsmartmanagement.dto.ApiResponse;

@RequestMapping("/api/students")
public interface StudentApi {

	@PostMapping("/{studentId}/enroll/{courseId}")
	public ResponseEntity<ApiResponse<Long>> enroll(@PathVariable Long studentId, @PathVariable Long courseId);
}
