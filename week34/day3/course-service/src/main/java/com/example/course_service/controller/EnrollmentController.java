package com.example.course_service.controller;

import java.util.Collections; // 🔥 ADDED: This was missing and caused the crash
import java.util.List;
import java.util.Map; // 🔥 ADDED: Needed for error response mapping

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.course_service.dto.EnrollmentDto;
import com.example.course_service.dto.EnrollmentResponseDTO;
import com.example.course_service.entity.Enrollment;
import com.example.course_service.service.EnrollmentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // --- ENROLL STUDENT ---
    @PostMapping
    public ResponseEntity<?> enrollStudent(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") Integer role,
            @RequestBody EnrollmentDto dto) {

        if (role != 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only students can enroll in courses"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentService.enrollStudent(userId, dto.getCourseId()));
    }

    // --- GET CURRENT USER ENROLLMENTS ---
    @GetMapping("/my")
    public ResponseEntity<?> getMyEnrollments(
            @RequestHeader("X-User-Id") Long userId,
            @RequestHeader("X-User-Role") Integer role) {

        if (role != 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only students can view enrollments"));
        }

        return ResponseEntity.ok(enrollmentService.getStudentEnrollment(userId));
    }

    // --- FETCH BY STUDENT ID (FOR DASHBOARD) ---
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getEnrollmentsByStudent(@PathVariable Long studentId) {
        try {
            // 🔥 This call is likely triggering the Hibernate "select ... teacher_id" log
            List<EnrollmentResponseDTO> enrollments = enrollmentService.getEnrollments(studentId);
            
            // Return empty list [] if null, never throw exception
            return ResponseEntity.ok(enrollments != null ? enrollments : Collections.emptyList());
            
        } catch (Exception e) {
            // Log the stack trace in console so you can see the real error bro
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "error", "Nexus Sync Failed",
                        "details", e.getMessage(),
                        "studentId", studentId
                    ));
        }
    }

    @PutMapping("/{enrollmentId}/status")
    public ResponseEntity<Enrollment> updateStatus(@PathVariable Long enrollmentId, @RequestParam Enrollment.Status status) {
        return ResponseEntity.ok(enrollmentService.updateStatus(enrollmentId, status));
    }

 // 🔥 NEW: FETCH ROSTER BY COURSE ID
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getEnrollmentsByCourse(
            @RequestHeader("X-User-Role") Integer role,
            @PathVariable Long courseId) {

        if (role != 1 && role != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Only admin and teachers can view rosters"));
        }

        try {
            // NOTE: Make sure getEnrollmentsByCourse exists in your EnrollmentService!
            // It should look like: enrollmentRepository.findByCourse_CourseId(courseId)
            return ResponseEntity.ok(enrollmentService.getEnrollmentsByCourseId(courseId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to fetch roster"));
        }
    }
    
    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<?> dropEnrollment(@PathVariable Long enrollmentId) {
        enrollmentService.dropCourse(enrollmentId);
        return ResponseEntity.ok(Map.of("message", "Enrollment dropped successfully"));
    }
}