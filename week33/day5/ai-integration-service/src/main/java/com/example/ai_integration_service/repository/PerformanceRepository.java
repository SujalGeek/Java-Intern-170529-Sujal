package com.example.ai_integration_service.repository;

import java.util.List; 
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.ai_integration_service.entity.Performance;


@Repository
public interface PerformanceRepository extends JpaRepository<Performance, Long>{

	Optional<Performance> findByStudentIdAndCourseId(Long studentId,Long courseId);

	List<Performance> findAllByStudentId(Long studentId);
	
	@Query(value = "SELECT u.username as studentName, c.course_name as courseName, " +
		       "p.final_score as score, p.final_grade as grade, p.risk_level as risk " +
		       "FROM student_analytics_db.performance p " +
		       "JOIN student_analytics_user.users u ON p.student_id = u.user_id " +
		       "JOIN student_analytics_db.courses c ON p.course_id = c.course_id " +
		       "WHERE c.teacher_id = :teacherId", nativeQuery = true)
		List<Object[]> findPerformanceRadarData(@Param("teacherId") Long teacherId);

		
		// Logic: Calculate the average of all final_scores (divided by 25 to get a 4.0 scale) 
		// for every course EXCEPT the current one.
		@Query("SELECT AVG(p.finalScore) / 25.0 FROM Performance p " +
		       "WHERE p.studentId = :studentId AND p.courseId != :courseId")
		Double calculateAverageGpaForStudent(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
	
	
}
