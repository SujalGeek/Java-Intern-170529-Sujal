package com.example.ai_integration_service.controller;

import java.util.Map; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.ai_integration_service.dto.GenerateQuestionRequest;
import com.example.ai_integration_service.dto.MidTermRequest;
import com.example.ai_integration_service.dto.QuizRequestDto;
import com.example.ai_integration_service.dto.QuizResponseDto;
import com.example.ai_integration_service.service.AiIntegrationService;

@RestController
@RequestMapping("/api/v1/exams")
@CrossOrigin(origins = "*")
public class QuizController {

	@Autowired
	private AiIntegrationService aiIntegrationService;
	
	@Autowired
	private RestTemplate restTemplate;
	
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
	
	@PostMapping("/evaluate")
	public ResponseEntity<?> evaluateAnswer(@RequestBody Map<String, Object> request)
	{
		String NLP_URL = "http://localhost:5001/evaluate-answer";
		
		ResponseEntity<Map> response = restTemplate.postForEntity(
				NLP_URL
				, request, Map.class);
		
		return ResponseEntity.ok(response.getBody());
	}
	
	@PostMapping("/generate-question-bank")
	public ResponseEntity<?> generateQuestionBank(@RequestBody GenerateQuestionRequest dto)
	{
		return ResponseEntity.ok(aiIntegrationService.generateAndStore(dto));
	}
	
	@PostMapping("/build-midterm")
	public ResponseEntity<?> buildMidterm(@RequestBody MidTermRequest request) {
	    return ResponseEntity.ok(aiIntegrationService.buildMidterm(request));
	}

}
