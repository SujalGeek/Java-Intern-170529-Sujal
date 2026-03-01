package com.example.course_service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class CourseResponseDTO {

    private Long courseId;
    private String courseCode;
    private String courseName;
    private Long teacherId;
    private Integer semester;
    private Integer year;
    private Integer credits;
    private Integer maxStudents;
    private String description;
    private Boolean isActive;
    private Boolean isIndexed;
    private Date createdAt;
}