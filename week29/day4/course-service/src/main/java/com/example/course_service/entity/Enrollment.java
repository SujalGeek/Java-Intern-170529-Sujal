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
@Table(name = "enrollments")
@Data
public class Enrollment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "enrollment_id")
	private Long enrollmentId;
	
	@Column(name = "student_id",nullable = false)
	private Long studentId;
	
	@Column(name = "course_id",nullable = false)
	private Long courseId;
	
	@Enumerated(EnumType.STRING)
    @Column(name = "enrollment_status") 
    private Status status = Status.ACTIVE;	
	
	@Column(name = "enrollment_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date enrollmentDate;
	
	@PrePersist
	protected void onCreate() {
		enrollmentDate = new Date();
	}

	public enum Status{
		ACTIVE,COMPLETED, DROPPED
	}
}


