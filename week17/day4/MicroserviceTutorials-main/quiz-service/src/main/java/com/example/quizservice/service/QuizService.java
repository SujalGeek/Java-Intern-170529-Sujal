package com.example.quizservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.quizservice.dao.QuizDao;
import com.example.quizservice.feign.QuizInterface;
import com.example.quizservice.model.QuestionWrapper;
import com.example.quizservice.model.Quiz;
import com.example.quizservice.model.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    QuizDao quizDao;

    @Autowired
    QuizInterface quizInterface;


    public ResponseEntity<String> createQuiz(String category, int numQ, String title) {

        List<Integer> questions = quizInterface.getQuestionsForQuiz(category, numQ).getBody();
        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questions);
        quizDao.save(quiz);

        return new ResponseEntity<>("Success", HttpStatus.CREATED);

    }

   public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
     Quiz quiz = quizDao.findById(id).get();
     List<Integer> questionIds = quiz.getQuestionIds();
     // Fetch data from Question Service
     ResponseEntity<List<QuestionWrapper>> response = quizInterface.getQuestionsFromId(questionIds);
     
     return response; 
}
    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
        ResponseEntity<Integer> score = quizInterface.getScore(responses);
        return score;
    }

    public List<Quiz> getAll() {
        return quizDao.findAll();
    }

    public Quiz getQuizById(Integer id) {
        return quizDao.findById(id)
           .orElseThrow(() -> new RuntimeException("Quiz not found with id: " + id));
}

}
