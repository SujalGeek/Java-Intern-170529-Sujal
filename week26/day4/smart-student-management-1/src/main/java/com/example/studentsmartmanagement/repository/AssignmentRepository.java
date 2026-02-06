package com.example.studentsmartmanagement.repository;

import java.util.List; 

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.studentsmartmanagement.entity.Assignment;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long>{

	List<Assignment> findByCourse_CourseId(Long courseId);
	
	List<Assignment> findByTeacher_TeacherId(Long teacherId);
	
	@Query("SELECT a FROM Assignment a WHERE a.course.courseId = :courseId " +
		       "AND a.assignmentId NOT IN (SELECT s.assignment.assignmentId FROM Submission s WHERE s.student.studentId = :studentId)")
	List<Assignment> findPendingExamsForStudent(@Param("courseId") Long courseId, @Param("studentId") Long studentId);
}
