package com.example.course_service.dto;

import jakarta.validation.constraints.*; 
import lombok.Data;

@Data
public class CourseRequestDTO {

    @NotBlank(message = "Course code is required")
    @Size(min = 3, max = 20)
    private String courseCode;

    @NotBlank(message = "Course name is required")
    @Size(min = 3, max = 100)
    private String courseName;

    @NotNull(message = "Semester is required")
    private Integer semester;

    @NotNull(message = "Year is required")
    private Integer year;

    @Min(value = 1, message = "Credits must be at least 1")
    private Integer credits;

    @Min(value = 1, message = "Max students must be at least 1")
    private Integer maxStudents;

    private String description;
}