package com.example.assignment_service.controller;

import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.assignment_service.dto.AssignmentDetailsResponse;
import com.example.assignment_service.dto.SubmitAssignmentRequest;
import com.example.assignment_service.entity.Assignment;
import com.example.assignment_service.entity.AssignmentAttempt;
import com.example.assignment_service.service.AssignmentService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;

    // 🔥 TEACHER ONLY
    @PostMapping("/generate")
    public ResponseEntity<?> generate(
            @RequestHeader("X-User-Role") Integer role,
            @RequestParam Long courseId,
            @RequestParam String description) {

        if (role != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only teachers can generate assignments");
        }

        return ResponseEntity.ok(
                assignmentService.generateAssignment(courseId, description)
        );
    }

    // 🔥 STUDENT ONLY
    @PostMapping("/submit")
    public ResponseEntity<?> submit(
            @RequestHeader("X-User-Id") Long studentId,
            @RequestHeader("X-User-Role") Integer role,
            @RequestBody SubmitAssignmentRequest request) {

        if (role != 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only students can submit assignments");
        }

        return ResponseEntity.ok(
                assignmentService.submitAssignment(studentId, request)
        );
    }

    @GetMapping("/{assignmentId}")
    public ResponseEntity<?> getAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(
                assignmentService.getAssignmentById(assignmentId)
        );
    }
} 