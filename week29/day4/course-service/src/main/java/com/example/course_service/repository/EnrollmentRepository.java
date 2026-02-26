package com.example.course_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.course_service.entity.Enrollment;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long>{

	List<Enrollment> findByStudentId(Long studentId);
	
	List<Enrollment> findByCourseId(Long courseId);
	
	Optional<Enrollment> findByStudentIdAndCourseId(Long studentId,Long courseId);
	
	@Query("select count(e) from Enrollment e where e.studentId = ?1and e.status = ?2")
	long countByStudentIdAndStatus(Long statusId,Enrollment.Status status);
	
	@Query("select count(e) from Enrollment e where e.courseId = ?1 and e.status = ?2")
	long countByCourseIdAndStatus(Long courseId,Enrollment.Status status);
	
}
