package com.example.studentsmartmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.studentsmartmanagement.entity.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>{

	List<Question> findBySubject(String subject);
	
	List<Question> findByBloomLevel(String bloomLevel);
	
	List<Question> findByQuestionType(String type);
	
}
