package com.example.user_service.service;

import java.io.File;
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

    // CRITICAL: This must match your Volume Mount path in Kubernetes
    private static final String STORAGE_PATH = "/app/files/";

    public ResponseEntity<?> evaluateQuiz(SubmitQuizRequest submitQuizRequest) {

        // 1. Fetch questions from Quiz Service
        String url = "http://quiz-service/quiz/get/" + submitQuizRequest.getQuizId();
        QuestionDto[] questions = restTemplate.getForObject(url, QuestionDto[].class);

        if (questions == null) {
            throw new RuntimeException("Could not fetch questions for this quiz.");
        }

        int total = questions.length;
        int correct = 0;

        StringBuilder report = new StringBuilder();
        report.append("QUIZ REPORT\n\n");

        // 2. Evaluate answers
        for (QuestionDto q : questions) {
            // Handle Long vs Integer ID mismatch safely
            String studentAnswer = submitQuizRequest.getAnswers().get(Long.valueOf(q.getId()));
            
            boolean isCorrect = studentAnswer != null && studentAnswer.equals(q.getRightAnswer());
            if (isCorrect) correct++;

            report.append("Q: ").append(q.getQuestionTitle()).append("\n")
                      .append("Your Answer: ").append(studentAnswer != null ? studentAnswer : "Not Attempted").append("\n")
                  .append("Correct Answer: ").append(q.getRightAnswer()).append("\n")
                  .append("Result: ").append(isCorrect ? "Correct" : "Wrong").append("\n\n");
        }

        // 3. Get Current User
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 4. Generate Filename and URL
        String reportFileName = "report-" + user.getId() + "-" + System.currentTimeMillis() + ".txt";
        String downloadUrl = "/files/" + reportFileName; // This URL will be used by Frontend

        // 5. Save Score to DB
        Score score = new Score();
        score.setQuizId(submitQuizRequest.getQuizId());
        score.setScore(correct);
        score.setTotal(total);
        score.setUser(user);
        score.setReportUrl(downloadUrl);

        scoreRepository.save(score);

        // 6. Write File to Persistent Storage
        try {
            // Ensure the folder exists inside the container
            File folder = new File(STORAGE_PATH);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            // Write the file
            Path path = Paths.get(STORAGE_PATH + reportFileName);
            Files.writeString(path, report.toString());

            return ResponseEntity.ok(
                Map.of(
                    "score", correct,
                    "total", total,
                    "reportUrl", downloadUrl
                )
            );

        } catch (Exception e) {
            e.printStackTrace();
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