package com.example.course_service.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Entity
@Table(name = "submissions")
@Data
public class Submission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "submission_id")
	private Long submissionId;


	@Column(name = "assignment_id",nullable = false)
	private Long assignmentId;
	
	@Column(name = "student_id", nullable = false)
	private Long studentId;
	
	@Column(name = "student_answer",columnDefinition = "TEXT")
	private String studentAnswer;
	
	@Column(name = "ai_feedback", columnDefinition = "TEXT")
	private String aiFeedback;
	
	@Column(name = "grade_obtained")
	private Double gradeObtained;
	
	@Enumerated(EnumType.STRING)
	private Status status = Status.PENDING;
	
	@Column(name = "submitted_at")
	@Temporal(TemporalType.TIMESTAMP)
	private Date submittedAt;
	
	@PrePersist
	protected void onCreate() {
		submittedAt = new Date();
	}
	
	public enum Status{
		PENDING,GRADED
	}

}
