package com.example.studentsmartmanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.studentsmartmanagement.entity.Submission;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long>{

	List<Submission> findByStudent_StudentId(Long studentId);
	
	List<Submission> findByAssignment_AssignmentId(Long assignmentId);
	
}
