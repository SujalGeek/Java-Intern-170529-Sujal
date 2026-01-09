package com.example.studentsmartmanagement.controller;

import com.example.studentsmartmanagement.dto.ApiResponse;
import com.example.studentsmartmanagement.dto.CourseRequest;
import com.example.studentsmartmanagement.dto.GradeRequest;
import com.example.studentsmartmanagement.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    // Endpoint 1: Create a new Course
    // URL: POST http://localhost:8080/api/teachers/courses
    @PostMapping("/courses")
    public ResponseEntity<ApiResponse<String>> addCourse(@RequestBody @Valid CourseRequest request) {
        teacherService.addCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Course added successfully", HttpStatus.CREATED));
    }

    // Endpoint 2: Grade a Student
    // URL: POST http://localhost:8080/api/teachers/grade
    @PostMapping("/grade")
    public ResponseEntity<ApiResponse<String>> assignGrade(@RequestBody @Valid GradeRequest request) {
        teacherService.assignGrade(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Grade assigned successfully", HttpStatus.CREATED));
    }
}