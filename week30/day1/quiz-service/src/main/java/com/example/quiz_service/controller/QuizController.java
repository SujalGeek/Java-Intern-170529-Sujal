package com.example.quiz_service.controller;

import java.math.BigDecimal; 
import java.math.RoundingMode;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
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
public class QuizController {

    private final QuizService quizService;

    // 🔥 TEACHER ONLY
    @PostMapping("/generate")
    public ResponseEntity<?> generateQuiz(
            @RequestHeader("X-User-Role") Integer role,
            @RequestBody GenerateQuizRequest request) {

        if (role != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only teachers can generate quizzes");
        }

        return ResponseEntity.ok(
                quizService.generateQuiz(
                        request.getCourseId(),
                        request.getDescription()
                )
        );
    }

    // 🔥 STUDENT ONLY
    @PostMapping("/submit")
    public ResponseEntity<?> submitQuiz(
            @RequestHeader("X-User-Id") Long studentId,
            @RequestHeader("X-User-Role") Integer role,
            @RequestBody SubmitQuizRequest request) {

        if (role != 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only students can submit quizzes");
        }

        return ResponseEntity.ok(
                quizService.submitQuiz(studentId, request)
        );
    }

    @GetMapping("/{quizId}")
    public ResponseEntity<?> getQuiz(@PathVariable Long quizId) {
        return ResponseEntity.ok(
                quizService.getQuizById(quizId)
        );
    }

//	@PostMapping("/generate")
//	public ResponseEntity<?> generateQuiz(@RequestBody GenerateQuizRequest request
//			){
//		Quiz quiz = quizService.generateQuiz(request.getCourseId(),request.getDescription());
//		return ResponseEntity.ok(quiz);
//		
//	}
//	
//	
//	@PostMapping("/submit")
//	public ResponseEntity<?> submitQuiz(@RequestBody SubmitQuizRequest request) {
//
//	    QuizAttempt attempt = quizService.submitQuiz(request);
//
//	    return ResponseEntity.ok(attempt);
//	}
//	
	
}
