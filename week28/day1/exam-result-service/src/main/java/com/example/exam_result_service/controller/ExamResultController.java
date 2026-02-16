package com.example.exam_result_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exam_result_service.dto.ExamResultResponseDTO;
import com.example.exam_result_service.dto.ExamSubmissionDTO;
import com.example.exam_result_service.entity.ExamResult;
import com.example.exam_result_service.service.ExamResultService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/exam-results")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
//@Validated
public class ExamResultController {

	private final ExamResultService examResultService;
	
	@PostMapping("/submit")
	public ResponseEntity<ExamResultResponseDTO> saveResults(
//			@Valid 
			@RequestBody ExamSubmissionDTO dto)
	{
		ExamResultResponseDTO response = examResultService.saveResult(dto);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
//		return ResponseEntity.ok(examResultService.saveResult(dto));
	}
	
	@GetMapping("/student/{studentId}")
	public ResponseEntity<List<ExamResult>> getByStudent(@PathVariable Long studentId)
	{
		List<ExamResult> results =
                examResultService.getByStudent(studentId);

		return ResponseEntity.ok(results);
	}
	
	@GetMapping("/course/{courseId}")
	public ResponseEntity<List<ExamResult>> getByCourse(@PathVariable Long courseId)
	{
		List<ExamResult> results =
                examResultService.getByCourse(courseId);

		return ResponseEntity.ok(results);
	}
	
	@GetMapping("/student/{studentId}/course/{courseId}")
	public ResponseEntity<List<ExamResult>> getByStudentAndCourse(@PathVariable Long studentId,@PathVariable Long courseId)
	{
		List<ExamResult> results =
                examResultService.getByStudentAndCourse(studentId,courseId);

		return ResponseEntity.ok(results);
	}
	
	
	
	
}
