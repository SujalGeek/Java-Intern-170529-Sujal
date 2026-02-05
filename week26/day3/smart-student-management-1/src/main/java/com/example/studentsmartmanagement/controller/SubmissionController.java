package com.example.studentsmartmanagement.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.studentsmartmanagement.dto.ApiResponse;
import com.example.studentsmartmanagement.dto.ExamSubmissionDto;
import com.example.studentsmartmanagement.endpoint.StudentApi;
import com.example.studentsmartmanagement.endpoint.SubmissionApi;
import com.example.studentsmartmanagement.entity.Submission;
import com.example.studentsmartmanagement.service.SubmissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SubmissionController implements SubmissionApi {

	private final SubmissionService submissionService;
	
	@Override
	public ResponseEntity<ApiResponse<Map<String, Object>>> submitExam(ExamSubmissionDto dto) {
		// TODO Auto-generated method stub
		
		try {
			
			Submission result = submissionService.submitExam(dto);
			
			Map<String, Object> response = new HashMap<>();
	        response.put("submissionId", result.getSubmissionId());
	        response.put("gradeObtained", result.getGradeObtained());
	        response.put("status", result.getStatus());
	        response.put("studentId", result.getStudent().getStudentId());
	        response.put("message", "AI Auto-Grading Complete");

	        return ResponseEntity.ok(ApiResponse.success(response, "Exam Submitted Successfully!", HttpStatus.CREATED));

		}catch(RuntimeException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST)
					.body(ApiResponse.error(e.getMessage(), HttpStatus.BAD_REQUEST));
		}
		catch(Exception e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(ApiResponse.error("Submission failed" + e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR));
		}
		
	}

	
}
