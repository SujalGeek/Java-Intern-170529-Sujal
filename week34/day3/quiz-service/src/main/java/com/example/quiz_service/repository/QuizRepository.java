package com.example.quiz_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.quiz_service.entity.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long>{

	List<Quiz> findByCourseId(Long courseId);

}
