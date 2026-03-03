package com.example.exam_result_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
	
}
