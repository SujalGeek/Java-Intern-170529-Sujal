package com.example.assignment_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "assignment_question")
@Data
public class AssignmentQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long questionId;
	
	private Long assignmentId;
	
	@Column(columnDefinition = "TEXT")
	private String questionText;

	private Integer marks;
	
	private String bloomLevel;
}


