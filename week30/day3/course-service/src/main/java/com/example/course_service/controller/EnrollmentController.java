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
import org.springframework.web.bind.annotation.RequestHeader;
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
	
//	private final StudentRepoistory studentRepository;
	
//	@PostMapping
//	public ResponseEntity<Enrollment> enrollStudent(@RequestBody EnrollmentDto dto)
//	{
//		return ResponseEntity.status(HttpStatus.CREATED)
//				.body(enrollmentService.enrollStudent(dto));
//	}
	@PostMapping
	public ResponseEntity<?> enrollStudent(
	        @RequestHeader("X-User-Id") Long userId,
	        @RequestHeader("X-User-Role") Integer role,
	        @RequestBody EnrollmentDto dto) {

	    if (role != 3) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                .body("Only students can enroll in courses");
	    }
	    
//	    Long actualStudentId = studentRepository.findByUser_UserId(userId)
//                .orElseThrow(() -> new RuntimeException("Student Profile not found"))
//                .getStudentId();

	    return ResponseEntity.status(HttpStatus.CREATED)
	            .body(enrollmentService.enrollStudent(userId, dto.getCourseId()));
	}
	
	@GetMapping("/my")
	public ResponseEntity<?> getMyEnrollments(
	        @RequestHeader("X-User-Id") Long userId,
	        @RequestHeader("X-User-Role") Integer role) {

	    if (role != 3) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                .body("Only students can view enrollments");
	    }

	    return ResponseEntity.ok(
	            enrollmentService.getStudentEnrollment(userId)
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
		enrollmentService.dropCourse(enrollmentUd);
		return ResponseEntity.ok("Enrollment dropped successfully");
	}
	
	
}
