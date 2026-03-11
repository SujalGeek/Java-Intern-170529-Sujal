package com.example.analytics_service.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.analytics_service.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    // 🔥 ADMIN ONLY
    @GetMapping("/overview")
    public ResponseEntity<?> getOverallAnalytics(
            @RequestHeader("X-User-Role") Integer role) {

        if (role != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Admin access required");
        }

        return ResponseEntity.ok(
                analyticsService.getOverallAnalytics()
        );
    }

    // 🔥 TEACHER OR ADMIN
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getCourseAnalytics(
            @RequestHeader("X-User-Role") Integer role,
            @PathVariable Long courseId) {

        if (role != 1 && role != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied");
        }

        return ResponseEntity.ok(
                analyticsService.getCourseAnalytics(courseId)
        );
    }

    // 🔥 STUDENT own OR ADMIN
    @GetMapping("/student/{studentId}")
    public ResponseEntity<?> getStudentAnalytics(
            @RequestHeader("X-User-Role") Integer role,
            @RequestHeader("X-User-Id") Long loggedUserId,
            @PathVariable Long studentId) {

        if (role != 1 && !loggedUserId.equals(studentId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied");
        }

        return ResponseEntity.ok(
                analyticsService.getStudentAnalytics(studentId)
        );
    }
}
