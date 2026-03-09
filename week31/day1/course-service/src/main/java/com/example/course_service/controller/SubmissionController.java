package com.example.course_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.course_service.dto.SubmissionDto;
import com.example.course_service.entity.Submission;
import com.example.course_service.repository.AssignmentRepository;
import com.example.course_service.service.SubmissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
public class SubmissionController {

	private final SubmissionService submissionService;
	
	@PostMapping
	public ResponseEntity<?> submitAssignment(
	        @RequestHeader("X-User-Id") Long userId,
	        @RequestHeader("X-User-Role") Integer role,
	        @RequestBody SubmissionDto dto) {

	    if (role != 3) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                .body("Only students can submit assignments");
	    }

	    return ResponseEntity.ok(
	            submissionService.submitAssignment(userId, dto)
	    );
	}
	
	@PostMapping("/grade/{submissionId}")
	public ResponseEntity<?> gradeSubmission(
	        @RequestHeader("X-User-Role") Integer role,
	        @PathVariable Long submissionId,
	        @RequestBody SubmissionDto gradeData) {

	    if (role != 2) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                .body("Only teachers can grade submissions");
	    }

	    return ResponseEntity.ok(
	            submissionService.gradeSubmission(
	                    submissionId,
	                    gradeData.getGradeObtained(),
	                    gradeData.getAiFeedback()
	            )
	    );
	}
	
}
