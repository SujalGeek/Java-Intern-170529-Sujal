package com.example.studentsmartmanagement.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "question_bank")
public class Question {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String subject;
	
	private String topic;
	
	@Column(name = "question_text", columnDefinition = "TEXT")
	private String questionText;
	
	@Enumerated(EnumType.STRING)
	private QuestionType questionType;
	
	@Column(name = "bloom_level")
	private String bloomLevel;
	
	@Column(name = "option_a")
	private String optionA;
	
	@Column(name = "option_b")
	private String optionB;
	
	@Column(name = "option_c")
	private String optionC;
	
	@Column(name = "option_d")
	private String optionD;
	
	@Column(name = "correct_answer", columnDefinition = "TEXT")
	private String correctAnswer;
	
	@Column(columnDefinition = "TEXT")
	private String explanation;
	
	@Column(name = "created_at")
	private LocalDateTime createdAt;
	
	@PrePersist
	protected void onCreate() {
		createdAt= LocalDateTime.now();
	}
	
	
}
