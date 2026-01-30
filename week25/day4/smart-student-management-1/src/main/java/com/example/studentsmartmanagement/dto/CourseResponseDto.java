package com.example.studentsmartmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseResponseDto {

	private Long courseId;
	private String courseName;
	private String courseCode;
	private Integer credits;
	
	private String teacherName;
}
