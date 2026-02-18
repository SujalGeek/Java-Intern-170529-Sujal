package com.example.ai_integration_service.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Table(name = "midterm_attempt")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MidtermAttempt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long attemptId;
	
	private Long midtermId;
	private Long studentId;
	private BigDecimal totalScore;
	
	private String status;
	
	@Column(updatable = false)
	private LocalDateTime createdAt;
	
}
