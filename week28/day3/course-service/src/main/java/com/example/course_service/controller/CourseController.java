package com.example.course_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.course_service.dto.CourseDto;
import com.example.course_service.entity.Course;
import com.example.course_service.service.CourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

	private final CourseService courseService;
	
	@GetMapping
	public ResponseEntity<List<Course>> getAllCourses(){
		return ResponseEntity.ok(
					courseService.getAllCourses());
	}
	
	
	@GetMapping("/{courseId}")
	public ResponseEntity<Course> getCourseById(@PathVariable Long courseId)
	{
		return ResponseEntity.ok(courseService.getCourseById(courseId));
	}
	
	@GetMapping("/teacher/{teacherId}")
	public ResponseEntity<List<Course>> getCoursesByTeacher(@PathVariable Long teacherId)
	{
		return ResponseEntity.ok(
				courseService.getCourseByTeacher(teacherId));
	}
	
	@PostMapping
	public ResponseEntity<Course> createCourse(@RequestBody CourseDto dto)
	{
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(courseService.createCourse(dto));
				
	}
	
	@PutMapping("/{courseId}")
	public ResponseEntity<Course> updateCourse(@PathVariable Long courseId,@RequestBody CourseDto dto)
	{
		return ResponseEntity.ok(courseService.updateCourse(courseId, dto));
	}
	
	@DeleteMapping("/{courseId}")
	public ResponseEntity<String> deleteCourse(@PathVariable Long courseId)
	{
		courseService.deleteCourse(courseId);
		return ResponseEntity.ok("Course Deleted Successfully");
	}
	
	 @GetMapping("/semester")
	    public ResponseEntity<List<Course>> getCoursesBySemester(
	            @RequestParam Integer semester,
	            @RequestParam Integer year) {
	        List<Course> courses = courseService.getCoursesBySemester(semester, year);
	        return ResponseEntity.ok(courses);
	    }
	
	
}
