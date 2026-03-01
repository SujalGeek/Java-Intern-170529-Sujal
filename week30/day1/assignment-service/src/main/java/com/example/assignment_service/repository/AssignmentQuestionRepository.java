package com.example.assignment_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.assignment_service.entity.AssignmentQuestion;

@Repository
public interface AssignmentQuestionRepository extends JpaRepository<AssignmentQuestion, Long> {

	List<AssignmentQuestion> findByAssignmentId(Long assignmentId);
	
}
