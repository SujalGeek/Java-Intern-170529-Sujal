package com.example.course_service.controller;

import java.io.IOException;  
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.course_service.dto.CourseDto;
import com.example.course_service.dto.CourseRequestDTO;
import com.example.course_service.dto.CourseResponseDTO;
import com.example.course_service.entity.Course;
import com.example.course_service.service.CourseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/course")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    // 🔥 Create Course (Teacher Only)
    @PostMapping("/create")
    public ResponseEntity<?> createCourse(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") Integer role,
            @Valid @RequestBody CourseRequestDTO request) {

    	System.out.println("Role form controller: "+role);
        if (role != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only teachers can create courses");
        }

        return ResponseEntity.ok(
                courseService.createCourse(userId, request)
        );
    }

    // 🔥 Teacher View Own Courses
    @GetMapping("/my")
    public ResponseEntity<?> getMyCourses(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") Integer role) {

        if (role != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only teachers can view their courses");
        }

        return ResponseEntity.ok(
                courseService.getCourseByTeacher(userId)
        );
    }

    // 🔥 Get All Courses (Admin Only)
    @GetMapping("/all")
    public ResponseEntity<?> getAllCourses(
            @RequestHeader("X-User-Role") Integer role) {

//        if (role != 1) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Only admin can view all courses");
//    }

        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // 🔥 Upload & Index Book (Teacher Only)
    @PostMapping("/{courseId}/index")
    public ResponseEntity<?> indexBook(
            @RequestHeader("X-User-Role") Integer role,
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file) throws IOException {

        if (role != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only teachers can index books");
        }

        courseService.uploadAndIndexBook(courseId, file);

        return ResponseEntity.ok("Book indexed successfully");
    }
}
