package com.example.ai_integration_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ai_integration_service.dto.QuizRequestDto;
import com.example.ai_integration_service.dto.QuizResponseDto;
import com.example.ai_integration_service.service.AiIntegrationService;

@RestController
@RequestMapping("/api/v1/exams")
@CrossOrigin(origins = "*")
public class QuizController {

	@Autowired
	private AiIntegrationService aiIntegrationService;
	
	@PostMapping("/generate")
	public ResponseEntity<?> generateQuiz(@RequestBody QuizRequestDto requestDto)
	{
		try {
			QuizResponseDto aiResponse = aiIntegrationService.generateExamQuestions(requestDto);
			return ResponseEntity.ok(aiResponse);
		} catch (Exception e) {
			// TODO: handle exception
			return ResponseEntity.internalServerError().body("Error generating error: "+ e.getMessage());
		}
	}
}
