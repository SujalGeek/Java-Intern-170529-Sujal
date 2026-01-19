package com.example.studentsmartmanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "submission_answers")
@Data
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // <--- ADD THIS
public class SubmissionAnswer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long answerId;
	
	@ManyToOne
	@JoinColumn(name = "submission_id")
	@JsonIgnore
	private Submission submission;
	
	@ManyToOne
	@JoinColumn(name = "question_id")
	private Question question;
	
	private String studentAnswer;
	
	private boolean isCorrect;
	
	private Double marksAwarded;
	

}
