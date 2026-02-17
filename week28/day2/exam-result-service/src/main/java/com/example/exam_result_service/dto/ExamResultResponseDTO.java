package com.example.exam_result_service.dto;


import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExamResultResponseDTO {

    private Long examResultId;
    private Long studentId;
    private Long courseId;
    private String question;
    private String studentAnswer;
    private String referenceAnswer;
    private BigDecimal score;
    private String feedback;
    private String bloomLevel;
}
