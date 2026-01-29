package com.example.studentsmartmanagement.service;

import com.example.studentsmartmanagement.dto.CourseRequest;
import com.example.studentsmartmanagement.dto.GradeRequest;

public interface TeacherService {

	void addCourse(CourseRequest request);
	
	void assignGrade(GradeRequest request);
}
