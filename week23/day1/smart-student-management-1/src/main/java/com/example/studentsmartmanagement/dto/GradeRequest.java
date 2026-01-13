package com.example.studentsmartmanagement.dto;

import com.example.studentsmartmanagement.entity.ExamType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GradeRequest {

	@NotNull(message = "Enrollment Id is required")
	private Long enrollmentId;
	
	@NotNull(message = "Exam Type is required")
	private ExamType examType;
	
	@NotNull(message = "Score is required")
	@Min(value = 0, message = "Score cannot be negative")
    private Double scoreObtained;

    @NotNull(message = "Max Score is required")
    @Min(value = 1, message = "Max Score must be at least 1")
    private Double maxScore;

    private String feedback;}
