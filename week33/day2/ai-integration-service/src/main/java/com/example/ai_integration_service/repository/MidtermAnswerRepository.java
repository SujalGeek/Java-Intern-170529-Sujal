package com.example.ai_integration_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ai_integration_service.entity.MidtermAnswer;

@Repository
public interface MidtermAnswerRepository extends JpaRepository<MidtermAnswer, Long>{

}
