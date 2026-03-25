package com.example.performance_service.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "performance",
uniqueConstraints = @UniqueConstraint(columnNames = {
		"student_id",
		"course_id"
}))
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Performance {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "performance_id")
	private Long performanceId;
	
	@Column(name = "student_id",nullable = false)
	private Long studentId;
	
	@Column(name = "course_id",nullable = false)
	private Long courseId;
	
	@Column(name = "quiz_average",precision = 5,scale = 2)
	private BigDecimal quizAverage;
	
	@Column(name = "attendance_percentage",precision = 5,scale = 2)
	private BigDecimal attendancePercentage;
	
	@Column(name = "assignment_average",precision = 5,scale = 2)
	private BigDecimal assignmentAverage;
	
	@Column(name = "midterm_score",precision = 5,scale = 2)
	private BigDecimal midtermScore;
	
	
//	private Integer participationScore;
	
	@Column(name = "final_score",precision = 5,scale = 2)
	private BigDecimal finalScore;
	
	@Column(name = "final_grade",length = 2)
	private String finalGrade;
	
	@Column(name = "participation_score")
	private Integer participationScore;
	
	@Column(name = "study_hours_per_week")
	private Integer studyHoursPerWeek;
	
	@Column(name = "previous_gpa",precision = 3,scale =2)
	private BigDecimal previousGpa;
	
	
	private String predictedGrade;
	
	private String riskLevel;
	
	private BigDecimal predictionConfidence;
	
	@Column(columnDefinition = "TEXT")
	private String bloomAnalysis;
	
	@Column(columnDefinition = "TEXT")
	private String diagnosticFeedback;
	
	@Column(name = "updated_by")
	private Long updatedBy;

	@Column(name = "academic_status")
	private String academicStatus;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	
	@PrePersist
	@PreUpdate
	public void updateTimestamp() {
		this.updatedAt = LocalDateTime.now();
	}
	
	
}
