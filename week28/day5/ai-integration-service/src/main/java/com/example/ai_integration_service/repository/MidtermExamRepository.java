package com.example.ai_integration_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ai_integration_service.entity.MidtermExam;

@Repository
public interface MidtermExamRepository extends JpaRepository<MidtermExam, Long>{

}
