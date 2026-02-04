package com.example.studentsmartmanagement.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "submissions")
@Data
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // <--- ADD THIS
public class Submission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long submissionId;
	
	@ManyToOne
	@JoinColumn(name = "student_id")
	private Student student;
	
	@ManyToOne
	@JoinColumn(name = "assignment_id")
	private Assignment assignment;
	
	private LocalDateTime submissionDate;
	
	private String fileUrl;
	
	private Double gradeObtained;
	
	private String submissionType;
	
	private String status;
	
	@OneToMany(mappedBy = "submission",cascade = CascadeType.ALL)
	private List<SubmissionAnswer> answers;
}
