package com.example.assignment_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.assignment_service.dto.AssignmentDetailsResponse;
import com.example.assignment_service.dto.SubmitAssignmentRequest;
import com.example.assignment_service.entity.Assignment;
import com.example.assignment_service.entity.AssignmentAttempt;
import com.example.assignment_service.service.AssignmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AssignmentController {

	private final AssignmentService assignmentService;
	
	@PostMapping("/generate")
	public Assignment generate(@RequestParam Long courseId,@RequestParam String description)
	{
		return assignmentService.generateAssignment(courseId, description);
	}
	
	@PostMapping("/submit")
	public AssignmentAttempt submit(@RequestBody SubmitAssignmentRequest request)
	{
		return assignmentService.submitAssignment(request);
	}
	
	@GetMapping("/{assignmentId}")
	public AssignmentDetailsResponse getAssignment(@PathVariable Long assignmentId)
	{
		return assignmentService.getAssignmentById(assignmentId);
	}
}
