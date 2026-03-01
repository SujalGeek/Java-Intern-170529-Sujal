package com.example.quiz_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "quiz_question")
@Data
public class QuizQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long questionId;


	private Long quizId;
	
	@Column(columnDefinition = "TEXT")
	private String questionText;
	
	private String correctAnswer;
	
	private Integer marks;
	
	private String bloomLevel;
}
