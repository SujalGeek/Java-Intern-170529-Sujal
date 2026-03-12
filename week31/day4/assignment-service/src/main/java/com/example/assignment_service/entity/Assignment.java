package com.example.assignment_service.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "assignment")
@Data
public class Assignment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long assignmentId;
	
	private Long courseId;
	
	private Integer totalMarks;
	
	private LocalDateTime createdAt;
	
	@PrePersist
	public void setCreatedAt() {
	    this.createdAt = LocalDateTime.now();
	}
}
