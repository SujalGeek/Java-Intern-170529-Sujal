package com.example.user_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.user_service.entity.Score;
import com.example.user_service.repository.ScoreRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ScoreService {
    
    private final ScoreRepository scoreRepository;

    public List<Score> getUserScores(Long userId)
    {
        return scoreRepository.findByUserId(userId);
    }

    public List<Score> getScoresByQuiz(Long quizId)
    {
        return scoreRepository.findByQuizId(quizId);
    }


    public String saveScore(Score score)
    {
        scoreRepository.save(score);
        return "Score saved!";
    }
}
