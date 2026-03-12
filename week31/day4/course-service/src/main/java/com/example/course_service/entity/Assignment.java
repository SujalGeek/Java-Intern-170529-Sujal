package com.example.course_service.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "assignments")
@Data
public class Assignment {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "assignment_id")
	private Long assignmentId;

	@Column(name = "course_id",nullable = false)
	private Long courseId;
	
	@Column(nullable = false)
	private String title;
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "correct_answer",columnDefinition = "TEXT")
	private String correctAnswer;
	
	@Column(name = "max_score")
	private Integer max_Score = 100;
	
	@Column(name = "due_date")
	private Date dueDate;
	

	
}


