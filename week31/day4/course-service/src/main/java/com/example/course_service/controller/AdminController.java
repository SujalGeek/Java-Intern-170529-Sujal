package com.example.course_service.controller;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.course_service.entity.Course;
import com.example.course_service.repository.CourseRepository;

@RestController
@RequestMapping("/api/v1/admins")
//@CrossOrigin("*") // Adjust based on your security setup
public class AdminController {

   // Assuming you have this

    @Autowired
    private CourseRepository courseRepository; // Assuming you have this

    // 1. FETCH ALL USERS

    // 2. FETCH ALL COURSES
    @GetMapping("/courses")
    public ResponseEntity<?> getAllCourses() {
    	System.out.println("Getting there bro!!");
        return ResponseEntity.ok(courseRepository.findAll());
    }

    // 3. CREATE A NEW COURSE
    @PostMapping("/courses")
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
    	System.out.println("Getting there bro22!!");
        // e.g., course.setCode("CS-101"), course.setName("Intro...")
        Course savedCourse = courseRepository.save(course);
        return ResponseEntity.ok(savedCourse);
    }
}