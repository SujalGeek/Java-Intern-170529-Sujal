package com.example.studentsmartmanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "students")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Student {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "student_id")
	private Long studentId;
	
	@Column(name = "first_name", nullable = false)
	private String firstName;
	
	@Column(name = "last_name",nullable = false)
	private String lastName;
	
	@Column(name = "roll_number",nullable = false,unique = true)
	private String rollNumber;
	
	@Column(nullable = false)
	private String department;
	
	@Column(name = "current_semester")
	private Integer currentSemester;
	
	@Column(name = "cgpa")
	private Double cgpa;
	
}
