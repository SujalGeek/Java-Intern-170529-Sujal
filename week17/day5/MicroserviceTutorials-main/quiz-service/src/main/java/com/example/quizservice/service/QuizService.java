package com.example.quizservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.example.quizservice.dao.QuizDao;
import com.example.quizservice.feign.QuizInterface;
import com.example.quizservice.model.QuestionWrapper;
import com.example.quizservice.model.Quiz;
import com.example.quizservice.model.Response;
import java.util.List;

@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;

    @Autowired
    QuizInterface quizInterface;

    // Updated Create Method
    public ResponseEntity<String> createQuiz(String category, int numQ, String title, List<Integer> specificQuestionIds) {

        List<Integer> questions;

        // 1. If Admin selected specific questions, use them
        if (specificQuestionIds != null && !specificQuestionIds.isEmpty()) {
            questions = specificQuestionIds;
        } 
        // 2. Otherwise, generate random ones using Feign Client
        else {
            questions = quizInterface.getQuestionsForQuiz(category, numQ).getBody();
        }

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questions);
        
        // Ensure category and numQuestions are saved for display in dashboard
        quiz.setCategoryName(category); 
        quiz.setNumQuestions(questions.size());

        quizDao.save(quiz);

        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Quiz quiz = quizDao.findById(id).get();
        List<Integer> questionIds = quiz.getQuestionIds();
        return quizInterface.getQuestionsFromId(questionIds);
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        return quizInterface.getScore(responses);
    }

    public List<Quiz> getAll() {
        return quizDao.findAll();
    }

    public Quiz getQuizById(Integer id) {
        return quizDao.findById(id)
           .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
    }
}