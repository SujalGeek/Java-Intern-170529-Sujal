package com.example.performance_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.performance_service.dto.PerformanceDTO;
import com.example.performance_service.entity.Performance;
import com.example.performance_service.service.PerformanceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    @PostMapping("/upsert")
    public ResponseEntity<?> upsert(
    		@RequestHeader(value = "X-User-Role", required = false) Integer role, // 🔥 Make optional
    		@RequestBody PerformanceDTO dto) {

        if (role != 1 && role != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only teachers or admin can update performance");
        }

        Performance performance =
                performanceService.upsertPerformance(dto);

        return ResponseEntity.ok(
                performanceService.getPerformance(
                        performance.getStudentId(),
                        performance.getCourseId()
                )
        );
    }

    // 🔥 Student can see only own
    @GetMapping("/{studentId}/{courseId}")
    public ResponseEntity<?> getPerformance(
            @RequestHeader("X-User-Role") Integer role,
            @RequestHeader("X-User-Id") Long loggedUserId,
            @PathVariable Long studentId,
            @PathVariable Long courseId) {

        if (role != 1 && !loggedUserId.equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied");
        }

        return ResponseEntity.ok(
                performanceService.getPerformance(studentId, courseId)
        );
    }
    @GetMapping("/summary/{studentId}")
    public ResponseEntity<?> getGlobalSummary(@PathVariable Long studentId) {
        // 🔥 This is what the Overview Tab SHOULD be calling!
        return ResponseEntity.ok(performanceService.getStudentGlobalSummary(studentId));
    }
}
