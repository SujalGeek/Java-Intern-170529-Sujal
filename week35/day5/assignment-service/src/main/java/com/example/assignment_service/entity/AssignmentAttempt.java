package com.example.assignment_service.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "assignment_attempt")
@Data
public class AssignmentAttempt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long attemptId;

	private Long assignmentId;
	
	private Long studentId;
	
	private BigDecimal totalScore;
	
	private BigDecimal percentage;
	
	private String grade;
	
	private LocalDateTime submittedAt;

	@PrePersist
	public void setCreatedAt() {
	    this.submittedAt = LocalDateTime.now();
	}
}

