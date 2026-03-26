package com.example.ai_integration_service.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "question_bank")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionBank {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String concept;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "bloom_level")
	private String bloomLevel;
	
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(
			name = "generated_questions",
			joinColumns = @JoinColumn(name = "question_bank_id")
			)
	@Column(name = "question_text", columnDefinition = "TEXT")
	private List<String> questions;
	
	@Column(name = "created_at",updatable = false)
	private LocalDateTime createdAt;
	
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
	
}

