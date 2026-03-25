package com.example.quiz_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "quiz")
@Data
public class Quiz {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long quizId;
	
	private Long courseId;
	
	private Integer totalMarks;
	
	private LocalDateTime createdAt;
	
	@Column(name = "is_published", columnDefinition = "boolean default false")
    private Boolean isPublished = false;
	
	@PrePersist
	public void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
}
