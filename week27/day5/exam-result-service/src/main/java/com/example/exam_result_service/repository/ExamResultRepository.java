package com.example.exam_result_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.exam_result_service.entity.ExamResult;

public interface ExamResultRepository extends JpaRepository<ExamResult, Long>{

	List<ExamResult> findByStudentId(Long studentId);
	
	List<ExamResult> findByCourseId(Long courseId);
	
	List<ExamResult> findByStudentIdAndCourseId(Long studentId,Long courseId);
}
