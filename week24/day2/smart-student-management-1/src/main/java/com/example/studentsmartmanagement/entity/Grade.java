package com.example.studentsmartmanagement.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grades")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Grade {

	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "grade_id")
	private Long gradeId;


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "enrollment_id",nullable = false)
	private Enrollment enrollment;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "exam_type",nullable = false)
	private ExamType examType;

	@Column(name = "scoreObtained",nullable = false)
	private Double scoreObtained;
	
	@Column(name = "max_score",nullable = false)
	private Double maxScore;
	
	@Column(columnDefinition = "TEXT")
	private String feedback;
	
	@Column(name = "graded_at")
	@Builder.Default
	private LocalDateTime gradedAt = LocalDateTime.now();
	
	
}

