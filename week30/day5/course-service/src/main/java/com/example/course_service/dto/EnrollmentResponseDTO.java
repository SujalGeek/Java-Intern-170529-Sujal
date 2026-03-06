package com.example.course_service.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnrollmentResponseDTO {
    private Long enrollmentId;
    private Long courseId;
    private String courseName;
    private String courseCode;
    private String instructorName;
    private Integer credits;
    private String status;
    private LocalDateTime enrollmentDate;
    private Double progress; // For the progress bar in your UI
}