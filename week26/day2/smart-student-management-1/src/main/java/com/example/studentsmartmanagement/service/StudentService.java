package com.example.studentsmartmanagement.service; // We will create this DTO below

import java.util.List;

import com.example.studentsmartmanagement.dto.CourseResponseDto;
import com.example.studentsmartmanagement.dto.StudentDto;


public interface StudentService {

	Long enrollmentCourse(Long studentId,Long courseId);
	
	List<StudentDto> searchStudents(String keyword);
	
	List<CourseResponseDto> getEnrolledCourses(Long studentId);
}
