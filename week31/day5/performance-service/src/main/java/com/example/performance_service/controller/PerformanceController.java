package com.example.performance_service.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.performance_service.dto.PerformanceDTO;
import com.example.performance_service.entity.Performance;
import com.example.performance_service.service.AnalyticsService;
import com.example.performance_service.service.PerformanceService;
//import com.example.performance_service.service.PredictionPipelineService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceController {

    // Inject all three core services
    private final PerformanceService performanceService;
    private final AnalyticsService analyticsService;
//    private final PredictionPipelineService predictionPipelineService;

    // ==========================================
    // 1. STANDARD PERFORMANCE ROUTES (Your Old Code)
    // ==========================================

    @PostMapping("/upsert")
    public ResponseEntity<?> upsert(
            @RequestHeader(value = "X-User-Role", required = false) Integer role, // 🔥 Made optional per your request
            @RequestBody PerformanceDTO dto) {

        if (role != null && role != 1 && role != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only teachers or admin can update performance");
        }

        Performance performance = performanceService.upsertPerformance(dto);

        return ResponseEntity.ok(
                performanceService.getPerformance(
                        performance.getStudentId(),
                        performance.getCourseId()
                )
        );
    }

    // 🔥 Student can see only their own performance, Admin can see anyone's
    @GetMapping("/{studentId}/{courseId}")
    public ResponseEntity<?> getPerformance(
            @RequestHeader("X-User-Role") Integer role,
            @RequestHeader("X-User-Id") Long loggedUserId,
            @PathVariable Long studentId,
            @PathVariable Long courseId) {

        if (role != 1 && role != 2 && !loggedUserId.equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied");
        }

        return ResponseEntity.ok(
                performanceService.getPerformance(studentId, courseId)
        );
    }

    // 🔥 Summary for the Student Overview Tab
    @GetMapping("/summary/{studentId}")
    public ResponseEntity<?> getGlobalSummary(@PathVariable Long studentId) {
        return ResponseEntity.ok(performanceService.getStudentGlobalSummary(studentId));
    }


    // ==========================================
    // 2. ANALYTICS DASHBOARD ROUTES
    // ==========================================

    // 🔥 ADMIN ONLY: Global System Analytics
    @GetMapping("/overview")
    public ResponseEntity<?> getOverallAnalytics(
            @RequestHeader("X-User-Role") Integer role) {

        if (role != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Admin access required");
        }
        return ResponseEntity.ok(analyticsService.getOverallAnalytics());
    }

    // 🔥 TEACHER OR ADMIN: Specific Course Analytics
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getCourseAnalytics(
            @RequestHeader("X-User-Role") Integer role,
            @PathVariable Long courseId) {

        if (role != 1 && role != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        return ResponseEntity.ok(analyticsService.getCourseAnalytics(courseId));
    }

    // 🔥 STUDENT OR ADMIN: Deep Student Analytics
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentAnalytics(
            @RequestHeader("X-User-Role") Integer role,
            @RequestHeader("X-User-Id") Long loggedUserId,
            @PathVariable Long studentId) {

        if (role != 1 && !loggedUserId.equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }
        return ResponseEntity.ok(analyticsService.getStudentAnalytics(studentId));
    }


    // ==========================================
    // 3. 🔥 THE AI PREDICTION PIPELINE TRIGGER 🔥
    // ==========================================

    @PostMapping("/predict/{studentId}/{courseId}")
    public ResponseEntity<?> generateLivePrediction(
            @RequestHeader("X-User-Role") Integer role,
            @PathVariable Long studentId,
            @PathVariable Long courseId) {

        if (role != 1 && role != 2 && role != 3) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

        try {
            // Create a blank DTO just to trigger your existing upsert pipeline
            PerformanceDTO triggerDto = new PerformanceDTO();
            triggerDto.setStudentId(studentId);
            triggerDto.setCourseId(courseId);
            
            // This runs your ENTIRE beautiful pipeline (Bloom, Feign Clients, AI Model)
            Performance updatedPerf = performanceService.upsertPerformance(triggerDto);
            
            // Return the fully updated performance
            return ResponseEntity.ok(performanceService.getPerformance(studentId, courseId));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "AI Pipeline Failed", "details", e.getMessage()));
        }
    }
}

