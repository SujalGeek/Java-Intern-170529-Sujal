package com.example.exam_result_service.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.exam_result_service.dto.ExamResultResponseDTO;
import com.example.exam_result_service.dto.ExamResultSimpleDTO;
import com.example.exam_result_service.dto.ExamSubmissionDTO;
import com.example.exam_result_service.entity.ExamResult;
import com.example.exam_result_service.service.ExamResultService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/exam-results")
@RequiredArgsConstructor
public class ExamResultController {

    private final ExamResultService examResultService;
    private final JdbcTemplate jdbcTemplate;


    // 🔥 STUDENT ONLY
    @PostMapping("/submit")
    public ResponseEntity<?> saveResults(
            @RequestHeader("X-User-Role") Integer role,
            @RequestHeader("X-User-Id") Long studentId,
            @RequestBody ExamSubmissionDTO dto) {

        if (role != 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only students can submit exams");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(examResultService.saveResult(studentId, dto));
    }

    // 🔥 Student can view own OR admin
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getByStudent(
            @RequestHeader("X-User-Role") Integer role,
            @RequestHeader("X-User-Id") Long loggedUserId,
            @PathVariable Long studentId) {

        if (role != 1 && !loggedUserId.equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied");
        }

        return ResponseEntity.ok(
                examResultService.getByStudent(studentId)
        );
    }

    // 🔥 Teacher or admin
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getByCourse(
            @RequestHeader("X-User-Role") Integer role,
            @PathVariable Long courseId) {

        if (role != 1 && role != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied");
        }

        return ResponseEntity.ok(
                examResultService.getByCourse(courseId)
        );
    }
	
 // 🔥 NEW: Required by Performance Service for AI Calculations
    @GetMapping("/average/{studentId}/{courseId}")
    public ResponseEntity<Double> getQuizAverage(
            @PathVariable Long studentId, 
            @PathVariable Long courseId) {
        
        // This calls your service to run: SELECT AVG(score) FROM exam_results WHERE ...
        Double average = examResultService.getAverageScore(studentId, courseId);
        
        // If no exams taken yet, return 0.0 instead of null to prevent 500s
        return ResponseEntity.ok(average != null ? average : 0.0);
    }
 // --- NEW ENDPOINT FOR GRADING HUB ---
    @GetMapping("/evaluations/course/{courseId}")
    public ResponseEntity<?> getEvaluationsByCourse(@PathVariable Long courseId) {
        try {
            List<Map<String, Object>> evaluations = examResultService.getEvaluationsByCourse(courseId);
            return ResponseEntity.ok(evaluations);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(Map.of("message", "Error fetching evaluations"));
        }
    }
    
 // 🔥 NEW: API endpoint for Frontend to hit when teacher clicks "Override"
    @PutMapping("/evaluations/{examResultId}/override")
    public ResponseEntity<?> overrideGrade(
            @PathVariable Long examResultId,
            @RequestBody Map<String, Object> payload) {
        try {
            BigDecimal newScore = new BigDecimal(payload.get("newScore").toString());
            ExamResult updatedResult = examResultService.updateScore(examResultId, newScore);
            return ResponseEntity.ok(Map.of("message", "Score updated successfully", "newScore", updatedResult.getScore()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to override score: " + e.getMessage()));
        }
    }
  
}
