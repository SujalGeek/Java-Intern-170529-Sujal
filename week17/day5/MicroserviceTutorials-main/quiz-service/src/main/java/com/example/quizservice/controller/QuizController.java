package com.example.quizservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.quizservice.model.QuestionWrapper;
import com.example.quizservice.model.Quiz;
import com.example.quizservice.model.QuizDto;
import com.example.quizservice.model.Response;
import com.example.quizservice.service.QuizService;
import java.util.List;

@RestController
@RequestMapping("quiz")
public class QuizController {

    @Autowired
    QuizService quizService;

    @PostMapping("create")
    public ResponseEntity<String> createQuiz(@RequestBody QuizDto quizDto){
        return quizService.createQuiz(
            quizDto.getCategoryName(), 
            quizDto.getNumQuestions(), 
            quizDto.getTitle(),
            quizDto.getQuestionIds() // Pass the list
        );
    }

    // ... existing endpoints (getQuizQuestions, submitQuiz, getAllQuizzes, getQuizById) ...
    @GetMapping("get/{id}") 
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(@PathVariable Integer id){
        return quizService.getQuizQuestions(id);
    }

    @PostMapping("submit/{id}")
    public ResponseEntity<Integer> submitQuiz(@PathVariable Integer id, @RequestBody List<Response> responses){
        return quizService.calculateResult(id, responses);
    }

    @GetMapping
    public List<Quiz> getAllQuizzes() {
        return quizService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Quiz> getQuizById(@PathVariable Integer id) {
        Quiz quiz = quizService.getQuizById(id);
        return ResponseEntity.ok(quiz);
    }
}