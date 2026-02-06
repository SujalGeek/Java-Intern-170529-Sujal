package com.example.studentsmartmanagement.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.example.studentsmartmanagement.dto.ApiResponse;
import com.example.studentsmartmanagement.dto.CourseResponseDto;
import com.example.studentsmartmanagement.endpoint.CourseApi;
import com.example.studentsmartmanagement.entity.Course;
import com.example.studentsmartmanagement.service.CourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CourseController implements CourseApi{

	private final CourseService courseService;
	
	@Override
	public ResponseEntity<ApiResponse<List<CourseResponseDto>>> getAllCourse() {
		List<CourseResponseDto> courses = courseService.getAllCourses();
		
		return ResponseEntity.ok(
				ApiResponse.success(courses , "Fetched"+ courses.size() + " courses successfully", HttpStatus.OK));		
	}

	@Override
	public ResponseEntity<ApiResponse<List<CourseResponseDto>>> getTeacherCourses(Long teacherId) {

		List<Course> courses = courseService.getCoursesByTeacher(teacherId);
		List<CourseResponseDto> courseDtos = courses.stream().map(this::mapToDto).collect(Collectors.toList());

        return ResponseEntity.ok(
            ApiResponse.success(courseDtos, "Found " + courseDtos.size() + " courses for teacher", HttpStatus.OK)
        );
	}

	private CourseResponseDto mapToDto(Course course) {
        String teacherName = "Unknown";
        if (course.getTeacher() != null) {
            teacherName = course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName();
        }

        return CourseResponseDto.builder()
                .courseId(course.getCourseId())
                .courseName(course.getCourseName())
                .courseCode(course.getCourseCode())
                .credits(course.getCredits())
                .teacherName(teacherName)
                .build();
    }
	
	
	@Override
	public ResponseEntity<ApiResponse<Course>> addCourse(Course course) {
		Course newCourse = courseService.createCourse(course);
		
		return ResponseEntity.ok(
				ApiResponse.success(newCourse, "Course created Successfully", HttpStatus.CREATED));
	}
	
}

