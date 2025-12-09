package com.example.user_service.repository;

import com.example.user_service.entity.Score;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Integer> {
    List<Score> findByUserId(Long userId);
    List<Score> findByQuizId(Integer quizId);
}