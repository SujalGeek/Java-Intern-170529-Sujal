package com.example.studentsmartmanagement.endpoint;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.studentsmartmanagement.dto.ApiResponse;
import com.example.studentsmartmanagement.dto.CourseResponseDto;
import com.example.studentsmartmanagement.entity.Course;

@RequestMapping("/api/v1/courses") // Standard versioned API path
public interface CourseApi {

	@GetMapping("/available")
	public ResponseEntity<ApiResponse<List<CourseResponseDto>>> getAllCourse();
	
	@GetMapping("/teacher")
	public ResponseEntity<ApiResponse<List<CourseResponseDto>>> getTeacherCourses(@RequestParam Long teacherId);
	
	@PostMapping
	public ResponseEntity<ApiResponse<Course>> addCourse(@RequestBody Course course);
	
}
