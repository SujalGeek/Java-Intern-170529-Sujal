package com.example.ai_integration_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.ai_integration_service.entity.ExamResult;


public interface ExamResultRepository extends JpaRepository<ExamResult, Long>{

	List<ExamResult> findByStudentId(Long studentId);
	
	List<ExamResult> findByCourseId(Long courseId);
	
	List<ExamResult> findByStudentIdAndCourseId(Long studentId,Long courseId);
	
	@Query("SELECT AVG(e.score) FROM ExamResult e WHERE e.studentId = :studentId AND e.courseId = :courseId")
	Double getAverageScore(@Param("studentId") Long studentId, @Param("courseId") Long courseId);

	@Modifying
    @Transactional
	void deleteByStudentIdAndCourseId(Long studentId, Long courseId);
	
	
}
