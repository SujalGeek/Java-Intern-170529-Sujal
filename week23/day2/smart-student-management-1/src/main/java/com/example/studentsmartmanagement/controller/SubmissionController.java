package com.example.studentsmartmanagement.controller;

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
	public ResponseEntity<ApiResponse<Submission>> submitExam(ExamSubmissionDto dto) {
		// TODO Auto-generated method stub
		
		try {
			
			Submission result = submissionService.submitExam(dto);
			return ResponseEntity.ok(ApiResponse.success(result, "Exam Submitted Successfully!!", HttpStatus.OK));
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
