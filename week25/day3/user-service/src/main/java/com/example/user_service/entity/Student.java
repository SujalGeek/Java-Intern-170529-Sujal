package com.example.user_service.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "student_id")
	private Long studentId;
	
	@OneToOne
	@JoinColumn(name = "user_id",referencedColumnName = "user_id",nullable = false,unique = true)
	private User user;
	
	@Column(name = "student_number",unique = true,nullable = false,length = 20)
	private String studentNumber;
	
	@Column(length = 100)
	private String major;
	
	@Column(name = "year")
	private Integer year;
	
	@Column(name = "semester")
	@Builder.Default
	private Integer semester = 1;
	
	@Column(name = "max_courses_per_semester")
	@Builder.Default
	private Integer maxCoursePerSemester = 3;
	
	@CreationTimestamp
	@Column(name = "created_at",updatable = false)
	private LocalDateTime createdAt;
	
}
