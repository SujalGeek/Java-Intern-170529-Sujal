package com.example.exam_result_service.dto;

import java.math.BigDecimal; 

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamSubmissionDTO {

    @NotNull(message = "Student ID is required")
    private Long studentId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    @NotBlank(message = "Question cannot be empty")
    private String question;

    @NotBlank(message = "Student answer cannot be empty")
    private String studentAnswer;

    @NotBlank(message = "Bloom level is required")
    private String bloomLevel;
}
