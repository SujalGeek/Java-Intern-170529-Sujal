package com.example.user_service.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.user_service.dto.QuestionDto;
import com.example.user_service.dto.SubmitQuizRequest;
import com.example.user_service.entity.Score;
import com.example.user_service.entity.User;
import com.example.user_service.repository.ScoreRepository;
import com.example.user_service.repository.UserRepository;
import com.nimbusds.jose.proc.SecurityContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserQuizService {

    private final ScoreRepository scoreRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public ResponseEntity<?> evaluateQuiz(SubmitQuizRequest submitQuizRequest) {

        String url ="http://question-service/question/quiz/" + submitQuizRequest.getQuizId();
        QuestionDto[] questions = restTemplate.getForObject(url, QuestionDto[].class);

        int total = questions.length;
        int correct = 0;
        StringBuilder report = new StringBuilder();
        report.append("QUIZ REPORT")

        for(QuestionDto q: questions)
        {
            String studentAnswer = submitQuizRequest.getAnswers().get(q.getId());
            boolean isCorrect = studentAnswer != null && studentAnswer.equals(q.getRightAnswer());
        
        if(isCorrect)
            correct++;

        report.append("Q: ").append(q.getQuestionTitle()).append("\n")
        .append("Your Answer: ").append(studentAnswer).append("\n")
        .append("Correct Answer: ").append(q.getRightAnswer()).append("\n")
        .append("Result: ").append(isCorrect ? "Correct": "Wrong").append("\n\n");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        
        Score score = new Score();
        score.setQuizId(submitQuizRequest.getQuizId());
        score.setScore(correct);
        score.setTotal(total);
        score.setUser(user);
    
        scoreRepository.save(score);

        try {
            Files.createDirectories(Paths.get("reports/"));
            String fileName = "report-" + user.getId()+"-" +System.currentTimeMillis() + ".txt";
            Path path = Paths.get("reports/", fileName);
            Files.writeString(path, report.toString());
            
            return ResponseEntity.ok().body(
                Map.of(
                    "score",correct,
                    "total",total,
                    "reportUrl","/files/" + fileName
                )
            );
        } catch (Exception e) {
             throw new RuntimeException("Report generation failed: " + e.getMessage());
        }

    }
    
}
