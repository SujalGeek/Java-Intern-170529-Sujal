package com.example.studentsmartmanagement.service;

import java.util.List;

import com.example.studentsmartmanagement.dto.CourseResponseDto;
import com.example.studentsmartmanagement.entity.Course;

public interface CourseService {

	public List<CourseResponseDto> getAllCourses();
	
	public List<Course> getCoursesByTeacher(Long teacherId);
	
	public Course createCourse(Course course);
}
