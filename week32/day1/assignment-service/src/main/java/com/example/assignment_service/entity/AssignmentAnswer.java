package com.example.assignment_service.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "assignment_answer")
@Data
public class AssignmentAnswer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long answerId;
	
	private Long attemptId;
	
	private Long questionId;
	
	@Column(columnDefinition = "TEXT")
	private String studentAnswer;
	
	private BigDecimal score;
	
	@Column(columnDefinition = "TEXT")
	private String feedback;
	
	
}
