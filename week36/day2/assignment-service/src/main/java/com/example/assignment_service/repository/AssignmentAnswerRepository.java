package com.example.assignment_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.assignment_service.entity.AssignmentAnswer;

@Repository
public interface AssignmentAnswerRepository extends JpaRepository<AssignmentAnswer, Long>{

//	boolean existsByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

}
