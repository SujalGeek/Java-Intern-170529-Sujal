package com.example.ai_integration_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ai_integration_service.entity.MidtermExam;
import com.example.ai_integration_service.entity.MidtermExamQuestion;

@Repository
public interface MidtermExamQuestionRepository extends JpaRepository<MidtermExamQuestion, Long>{

	List<MidtermExamQuestion> findByMidtermId(Long midtermId);

	Optional<MidtermExamQuestion> findByMidtermIdAndExamQuestionId(Long midtermId, Long examQuestionId);

}
