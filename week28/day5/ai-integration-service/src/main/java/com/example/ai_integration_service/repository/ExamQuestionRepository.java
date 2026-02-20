package com.example.ai_integration_service.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ai_integration_service.entity.ExamQuestion;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
	
	@Query(value = """
	        SELECT * FROM exam_question
	        WHERE course_id = :courseId
	        AND difficulty = :difficulty
	        ORDER BY RAND()
	        """,
	        nativeQuery = true)
	List<ExamQuestion> findRandomByDifficulty(
	        @Param("courseId") Long courseId,
	        @Param("difficulty") String difficulty,
	        Pageable pageable
	);

	
}