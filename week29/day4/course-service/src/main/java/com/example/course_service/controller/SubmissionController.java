package com.example.course_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	public ResponseEntity<Submission> submitAssignment(@RequestBody SubmissionDto dto)
	{
		return ResponseEntity.ok(
				submissionService.submitAssignment(dto)
				);
	}
	
	
	@PostMapping("/grade/{submissionId}")
	public ResponseEntity<Submission> gradeSubmission(@PathVariable Long submissionId,@RequestBody SubmissionDto gradeData)
	{
		return ResponseEntity.ok(submissionService.gradeSubmission(
                submissionId, 
                gradeData.getGradeObtained(), 
                gradeData.getAiFeedback()
        ));
	}
	
}
