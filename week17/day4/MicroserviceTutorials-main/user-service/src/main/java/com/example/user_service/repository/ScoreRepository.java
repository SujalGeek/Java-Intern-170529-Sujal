package com.example.user_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.user_service.entity.Score;


public interface ScoreRepository extends JpaRepository<Score,Long>{
    
    List<Score> findByUserId(Long userId);
    List<Score> findByQuizId(Long quizId);
}
