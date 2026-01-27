package com.example.studentsmartmanagement.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "assignments")
@Data
public class Assignment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long assignmentId;
	
	@Column(nullable = false)
	private String title;
	
	
	@Column(columnDefinition = "TEXT")
	private String description;
	
	private LocalDateTime dueDate;
	
	private Double totalMarks;
	
	
	@Enumerated(EnumType.STRING)
	private AssignmentType type;
	
	@ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;
	
	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;
	
	@ManyToMany
	@JoinTable(
			name = "assignment_questions",
			joinColumns = @JoinColumn(name = "assignment_id"),
			inverseJoinColumns = @JoinColumn(name = "question_id")
			)
	private List<Question> questions;
	
	@OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL)
    private List<Submission> submissions;
    
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
	
}
