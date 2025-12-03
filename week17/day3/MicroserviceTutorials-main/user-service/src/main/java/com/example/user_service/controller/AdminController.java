package com.example.user_service.controller;

import com.example.user_service.entity.Score;
import com.example.user_service.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ScoreService scoreService;

    @GetMapping("/scores/quiz/{quizId}")
    public List<Score> getScoresByQuiz(@PathVariable Long quizId) {
        return scoreService.getScoresByQuiz(quizId);
    }

    @GetMapping("/scores/user/{userId}")
    public List<Score> getUserScores(@PathVariable Long userId) {
        return scoreService.getUserScores(userId);
    }
}
