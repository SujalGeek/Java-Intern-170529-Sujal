package com.example.user_service.dto;

import lombok.Data;

@Data
public class ScoreRequest {
    private Long userId;
    private Long quizId;
    private int score;
    private int totalQuestions;

}
