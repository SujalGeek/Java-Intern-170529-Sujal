package com.example.exam_result_service.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExamResultSimpleDTO {

	private Long examResultId;
	private Long studentId;
	private Long courseId;
	private Long examQuestionId;
	private BigDecimal score;
	private String feedback;
	private String bloomLevel;
	
	
}
