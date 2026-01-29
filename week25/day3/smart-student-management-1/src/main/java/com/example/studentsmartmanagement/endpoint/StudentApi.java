package com.example.studentsmartmanagement.endpoint;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.studentsmartmanagement.dto.ApiResponse;
import com.example.studentsmartmanagement.dto.StudentDto;

@RequestMapping("/api/students")
public interface StudentApi {

	@PostMapping("/{studentId}/enroll/{courseId}")
	public ResponseEntity<ApiResponse<Long>> enroll(@PathVariable Long studentId, @PathVariable Long courseId);
	
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<StudentDto>>> searchStudents(@RequestParam("query") String query);
	
}
