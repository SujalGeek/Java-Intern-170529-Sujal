package com.example.studentsmartmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.studentsmartmanagement.entity.Assignment;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long>{

	List<Assignment> findByCourse_CourseId(Long courseId);
	
	List<Assignment> findByTeacher_TeacherId(Long teacherId);
	
}
