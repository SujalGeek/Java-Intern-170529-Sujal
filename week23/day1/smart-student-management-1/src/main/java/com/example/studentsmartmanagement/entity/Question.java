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
	
	@Column(columnDefinition = "TEXT")
	private String questionText;
	
	@Enumerated(EnumType.STRING)
	private QuestionType questionType;
	
	private String bloomLevel;
	
	private String optionA;
	private String optionB;
	private String optionC;
	private String optionD;
	
	@Column(columnDefinition = "TEXT")
	private String correctAnswer;
	
	@Column(columnDefinition = "TEXT")
	private String explanation;
	
	private LocalDateTime createdAt;
	
	@PrePersist
	protected void onCreate() {
		createdAt= LocalDateTime.now();
	}
	
	
}
