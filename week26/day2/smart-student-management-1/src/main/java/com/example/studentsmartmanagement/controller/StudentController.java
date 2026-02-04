package com.example.studentsmartmanagement.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.studentsmartmanagement.dto.ApiResponse;
import com.example.studentsmartmanagement.dto.CourseResponseDto;
import com.example.studentsmartmanagement.dto.StudentDto;
import com.example.studentsmartmanagement.endpoint.StudentApi;
import com.example.studentsmartmanagement.service.StudentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StudentController implements StudentApi{

	private final StudentService studentService;
	
	@Override
	public ResponseEntity<ApiResponse<Long>> enroll(Long studentId, Long courseId) {
	Long newEnrollmentId = studentService.enrollmentCourse(studentId, courseId);
	
	return ResponseEntity.ok(
            ApiResponse.success(newEnrollmentId, "Enrolled successfully", HttpStatus.OK)
    );
	}

	@Override
	public ResponseEntity<ApiResponse<List<StudentDto>>> searchStudents(String query) {
		List<StudentDto> results = studentService.searchStudents(query);
		
		return ResponseEntity.ok(
				ApiResponse.success(results, "Found "+ results.size() + "records ", HttpStatus.OK));
				
	}

	@Override
	public ResponseEntity<ApiResponse<List<CourseResponseDto>>> getMyCourses(Long studentId) {
		List<CourseResponseDto> courses = studentService.getEnrolledCourses(studentId);
		return ResponseEntity.ok(
				ApiResponse.success(courses, "Fetched enrolled courses ", HttpStatus.OK));
	}

}
