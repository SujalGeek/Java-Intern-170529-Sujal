package com.example.course_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.course_service.dto.EnrollmentDto;
import com.example.course_service.entity.Enrollment;
import com.example.course_service.service.EnrollmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

	private final EnrollmentService enrollmentService;
	
	@PostMapping
	public ResponseEntity<Enrollment> enrollStudent(@RequestBody EnrollmentDto dto)
	{
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(enrollmentService.enrollStudent(dto));
	}
	
	@GetMapping("/student/{studentId}")
	public ResponseEntity<List<Enrollment>> getStudentEnrollments(@PathVariable Long studentId)
	{
		return ResponseEntity.ok(
				enrollmentService.getStudentEnrollment(studentId)
				);
	}
	
	@PutMapping("/{enrollmentId}/status")
	public ResponseEntity<Enrollment> updateStatus(@PathVariable Long enrollmentId, @RequestParam Enrollment.Status status)
	{
		return ResponseEntity.ok(enrollmentService.updateStatus(enrollmentId,status));
	}
	
	
	@DeleteMapping("/{enrollmentId}")
	public ResponseEntity<String> dropEnrollment(@PathVariable Long enrollmentUd)
	{
		return ResponseEntity.ok("Enrollment dropped successfully");
	}
	
	
}
