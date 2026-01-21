package com.example.studentsmartmanagement.endpoint;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.studentsmartmanagement.dto.ApiResponse;
import com.example.studentsmartmanagement.dto.ExamSubmissionDto;
import com.example.studentsmartmanagement.entity.Submission;

@RequestMapping("/api/submissions")
public interface SubmissionApi {

	@PostMapping("/submit-exam")
	public ResponseEntity<ApiResponse<Map<String, Object>>> submitExam(@RequestBody ExamSubmissionDto dto);
	
	
}
