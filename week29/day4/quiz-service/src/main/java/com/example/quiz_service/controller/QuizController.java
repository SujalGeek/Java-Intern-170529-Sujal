package com.example.quiz_service.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz_service.dto.GenerateQuizRequest;
import com.example.quiz_service.dto.QuizResponse;
import com.example.quiz_service.dto.SubmitQuizRequest;
import com.example.quiz_service.entity.Quiz;
import com.example.quiz_service.entity.QuizAttempt;
import com.example.quiz_service.entity.QuizQuestion;
import com.example.quiz_service.repository.QuizAttemptRepository;
import com.example.quiz_service.repository.QuizQuestionRepository;
import com.example.quiz_service.service.QuizService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QuizController {

	private final QuizService quizService;
	
	private final QuizQuestionRepository quizQuestionRepository;
	
	private final QuizAttemptRepository quizAttemptRepository;
	
	@PostMapping("/generate")
	public ResponseEntity<?> generateQuiz(@RequestBody GenerateQuizRequest request
			){
		Quiz quiz = quizService.generateQuiz(request.getCourseId(),request.getDescription());
		return ResponseEntity.ok(quiz);
		
	}
	
	
	@PostMapping("/submit")
	public ResponseEntity<?> submitQuiz(@RequestBody SubmitQuizRequest request) {

	    QuizAttempt attempt = quizService.submitQuiz(request);

	    return ResponseEntity.ok(attempt);
	}
	
	@GetMapping("/{quizId}")
	public ResponseEntity<?> getQuiz(@PathVariable Long quizId) {

	    QuizResponse quiz = quizService.getQuizById(quizId);

	    return ResponseEntity.ok(quiz);
	}
	
}
