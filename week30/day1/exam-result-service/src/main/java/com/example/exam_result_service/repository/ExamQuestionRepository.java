package com.example.exam_result_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.exam_result_service.dto.ExamSubmissionDTO;
import com.example.exam_result_service.entity.ExamQuestion;

public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long>{

//	Optional<ExamQuestion> findByExamQuestionId(Long examQuestionId);
}
