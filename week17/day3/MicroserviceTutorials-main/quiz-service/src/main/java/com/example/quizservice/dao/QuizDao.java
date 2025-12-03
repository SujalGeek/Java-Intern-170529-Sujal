package com.example.quizservice.dao;


import org.springframework.data.jpa.repository.JpaRepository;

import com.example.quizservice.model.Quiz;

public interface QuizDao extends JpaRepository<Quiz,Integer> {
}
