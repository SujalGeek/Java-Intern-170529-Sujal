package com.example.ai_integration_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "midterm_exam_questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MidtermExamQuestion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private Long midtermId;
	private Long examQuestionId;
	private int marks;
	
	
}
