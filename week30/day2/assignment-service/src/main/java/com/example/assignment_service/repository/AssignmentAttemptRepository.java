package com.example.assignment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.assignment_service.entity.AssignmentAttempt;

@Repository
public interface AssignmentAttemptRepository extends JpaRepository<AssignmentAttempt, Long>{

	boolean existsByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

	
}
