package com.example.ai_integration_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ai_integration_service.entity.QuestionBank;

@Repository
public interface QuestionBankRepository extends JpaRepository<QuestionBank, Long>  {

	List<QuestionBank> findByBloomLevel(String bloomLevel);

	List<QuestionBank> findByConceptContainingIgnoreCase(String concept);
}
