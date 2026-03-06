package com.example.ai_integration_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "midterm_exam")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MidtermExam {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long midtermId;
	
	private Long courseId;
	private int totalMarks;
	private int totalQuestions;
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@PrePersist
	protected void onCreate() {
	    this.createdAt = LocalDateTime.now();
	}
}
