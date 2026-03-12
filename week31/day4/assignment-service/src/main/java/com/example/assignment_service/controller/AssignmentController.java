package com.example.assignment_service.controller;

import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import com.example.assignment_service.dto.GenerateAssignmentRequest; // 🔥 New DTO
import com.example.assignment_service.dto.SubmitAssignmentRequest;
import com.example.assignment_service.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import java.util.Map;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {

    private final AssignmentService assignmentService;
    
    private final JdbcTemplate jdbcTemplate;

    /**
     * 🔥 TEACHER CORE: Generate descriptive tasks using AI
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generate(
            @RequestHeader("X-User-Role") Integer role,
            @RequestBody GenerateAssignmentRequest request) {

        if (role != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Faculty access required for AI generation"));
        }

        return ResponseEntity.ok(
                assignmentService.generateAssignment(
                    request.getCourseId(), 
                    request.getDescription(),
                    request.getTotalMarks(),
                    request.getDifficulty()
                )
        );
    }

    /**
     * 🔥 STUDENT CORE: Submit subjective answers for NLP evaluation
     */
    @PostMapping("/submit")
    public ResponseEntity<?> submit(
            @RequestHeader("X-User-Id") Long studentId,
            @RequestHeader("X-User-Role") Integer role,
            @RequestBody SubmitAssignmentRequest request) {

        if (role != 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "Student identity required for submission"));
        }

        return ResponseEntity.ok(
                assignmentService.submitAssignment(studentId, request)
        );
    }

    /**
     * 🌐 GLOBAL SYNC: Fetch all assignments for registry mapping
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllAssignments() {
        return ResponseEntity.ok(assignmentService.getAllAssignments());
    }

    /**
     * 🔍 TARGETED FETCH: Get specific task details
     */
    @GetMapping("/{assignmentId}")
    public ResponseEntity<?> getAssignment(@PathVariable Long assignmentId) {
        return ResponseEntity.ok(
                assignmentService.getAssignmentById(assignmentId)
        );
    }
    
    @DeleteMapping("/reset/{assignmentId}/student/{studentId}")
    public ResponseEntity<?> resetAssignment(@PathVariable Long assignmentId, @PathVariable Long studentId) {
        try {
            // 1. Delete the mapped answers first to prevent Foreign Key constraints
            jdbcTemplate.update("DELETE FROM assignment_answer WHERE attempt_id IN (SELECT attempt_id FROM assignment_attempt WHERE assignment_id = ? AND student_id = ?)", assignmentId, studentId);
            
            // 2. Delete the attempt record
            jdbcTemplate.update("DELETE FROM assignment_attempt WHERE assignment_id = ? AND student_id = ?", assignmentId, studentId);
            
            return ResponseEntity.ok(Map.of("message", "Database cleared! You can now retake the assignment."));
        } catch(Exception e) {
            return ResponseEntity.status(500).body(Map.of("message", "Failed to reset DB: " + e.getMessage()));
        }
    }
  }