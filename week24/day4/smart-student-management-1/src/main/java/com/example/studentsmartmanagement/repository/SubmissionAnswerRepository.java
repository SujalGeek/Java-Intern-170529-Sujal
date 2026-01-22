package com.example.studentsmartmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.studentsmartmanagement.entity.SubmissionAnswer;

@Repository
public interface SubmissionAnswerRepository extends JpaRepository<SubmissionAnswer, Long>{

	
}
