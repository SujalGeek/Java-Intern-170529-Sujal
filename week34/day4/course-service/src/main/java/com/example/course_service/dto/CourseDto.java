package com.example.course_service.dto;

import lombok.Data;

@Data
public class CourseDto {

	private Long courseId;
	
	private String courseCode;
	
	private String courseName;
	
	private Long teacherId;
	
	private String description;
	
	private Integer semester;
	
	private Integer year;

    private Integer maxStudents; // Your default

    private Integer credits;
    
    private Boolean isActive;
}

