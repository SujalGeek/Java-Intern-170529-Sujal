package com.example.exam_result_service.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exam_result_service.dto.ExamSubmissionDTO;
import com.example.exam_result_service.entity.ExamResult;
import com.example.exam_result_service.service.ExamResultService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/exam-results")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ExamResultController {

	private final ExamResultService examResultService;
	
	@PostMapping
	public ResponseEntity<ExamResult> saveResults(@Valid @RequestBody ExamSubmissionDTO dto)
	{
		return ResponseEntity.ok(examResultService.saveResult(dto));
	}
	
	@GetMapping("/student/{studentId}")
	public ResponseEntity<List<ExamResult>> getByStudent(@PathVariable Long studentId)
	{
		return ResponseEntity.ok(examResultService.getByStudent(studentId));
	}
	
	@GetMapping("/course/{courseId}")
	public ResponseEntity<List<ExamResult>> getByCourse(@PathVariable Long courseId)
	{
		return ResponseEntity.ok(examResultService.getByStudent(courseId));
	}
	
	@GetMapping("/student/{studentId}/course/{courseId}")
	public ResponseEntity<List<ExamResult>> getByStudentAndCourse(@PathVariable Long studentId,@PathVariable Long courseId)
	{
		return ResponseEntity.ok(examResultService.getByStudentIdAndCourse(studentId, courseId));
	}
	
	
	
	
}
