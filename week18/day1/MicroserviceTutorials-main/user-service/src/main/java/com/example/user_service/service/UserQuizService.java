package com.example.user_service.service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserQuizService {

    private final ScoreRepository scoreRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public ResponseEntity<?> evaluateQuiz(SubmitQuizRequest submitQuizRequest) {

        // ---------------------------------------------------------
        // CHANGE 1: URL Updated to point to QUIZ-SERVICE
        // ---------------------------------------------------------
        // Old: "http://question-service/question/quiz/" + id
        // New: "http://quiz-service/quiz/get/" + id
        String url = "http://quiz-service/quiz/get/" + submitQuizRequest.getQuizId();
        
        // We fetch the questions associated with this Quiz ID
        QuestionDto[] questions = restTemplate.getForObject(url, QuestionDto[].class);

        // Safety check in case service returns null
        if (questions == null) {
            throw new RuntimeException("Could not fetch questions for this quiz.");
        }

        int total = questions.length;
        int correct = 0;

        StringBuilder report = new StringBuilder();
        report.append("QUIZ REPORT\n\n");

        // Evaluate answers
      for (QuestionDto q : questions) {
    // String studentAnswer = submitQuizRequest.getAnswers().get(q.getId());
    
String studentAnswer = submitQuizRequest.getAnswers().get(Long.valueOf(q.getId()));
    // --- DEBUGGING LOGS ---
    System.out.println("Question ID: " + q.getId());
    System.out.println("Expected Answer (DB): " + q.getRightAnswer()); // Is this null?
    System.out.println("Student Answer: " + studentAnswer);
    System.out.println("Match? " + (studentAnswer != null && studentAnswer.equals(q.getRightAnswer())));
    // ----------------------

    boolean isCorrect = studentAnswer != null && studentAnswer.equals(q.getRightAnswer());
    if (isCorrect) correct++;

            report.append("Q: ").append(q.getQuestionTitle()).append("\n")
                  .append("Your Answer: ").append(studentAnswer != null ? studentAnswer : "Not Attempted").append("\n")
                  .append("Correct Answer: ").append(q.getRightAnswer()).append("\n")
                  .append("Result: ").append(isCorrect ? "Correct" : "Wrong").append("\n\n");
        }

        // Save score
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Score score = new Score();
        score.setQuizId(submitQuizRequest.getQuizId());
        score.setScore(correct);
        score.setTotal(total);
        score.setUser(user);
        
        // Optional: Save report URL in DB if you have a field for it
        String reportFileName = "report-" + user.getId() + "-" + System.currentTimeMillis() + ".txt";
        score.setReportUrl("/files/" + reportFileName); 

        scoreRepository.save(score);

        // Generate report file on disk
        try {
            Files.createDirectories(Paths.get("reports/"));
            Path path = Paths.get("reports/" + reportFileName);
            Files.writeString(path, report.toString());

            return ResponseEntity.ok(
                Map.of(
                    "score", correct,
                    "total", total,
                    "reportUrl", "/files/" + reportFileName
                )
            );

        } catch (Exception e) {
            throw new RuntimeException("Report generation failed: " + e.getMessage());
        }
    }

    public ResponseEntity<?> getMyScores() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        List<Score> scores = scoreRepository.findByUserId(user.getId());
        return ResponseEntity.ok(scores);
    }
}