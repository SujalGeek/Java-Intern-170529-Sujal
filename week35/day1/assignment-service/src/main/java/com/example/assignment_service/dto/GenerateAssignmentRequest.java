package com.example.assignment_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenerateAssignmentRequest {
    private Long courseId;
    private String description;
    private Integer totalMarks = 20; // Default values handled in DTO
    private String difficulty = "medium";
}