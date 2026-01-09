package com.example.studentsmartmanagement.entity;

import java.time.LocalDate;

import jakarta.annotation.Generated;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "enrollments",
	uniqueConstraints = {
			@UniqueConstraint(columnNames = {
					"student_id","course_id"
			})
	})
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Enrollment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "enrollment_id")
	private Long enrollmentId;


	@ManyToOne(fetch= FetchType.LAZY)
	@JoinColumn(name = "student_id",nullable = false)
	private Student student;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "course_id",nullable = false)
	private Course course;
	
	@Column(nullable = false)
	private Integer semester;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private EnrollmentStatus status = EnrollmentStatus.ACTIVE;
	
	@Column(name = "enrolled_date")
	@Builder.Default
	private LocalDate enrolledDate = LocalDate.now();
	
}
