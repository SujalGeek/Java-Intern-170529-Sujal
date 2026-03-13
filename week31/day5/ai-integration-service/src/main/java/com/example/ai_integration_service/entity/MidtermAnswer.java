package com.example.ai_integration_service.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "midterm_answer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MidtermAnswer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long answerId;
	
	private Long attemptId;
	private Long examQuestionId;
	
	@Column(columnDefinition = "TEXT")
	private String studentAnswer;

	private BigDecimal score;
	
	@Column(columnDefinition = "TEXT")
	private String feedback;
}

