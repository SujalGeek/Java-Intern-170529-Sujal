package com.example.ai_integration_service.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "assignment-service")
public interface AssignmentClient {
    @GetMapping("/api/assignments/average/{studentId}/{courseId}")
    Double getAverageForStudent(@PathVariable("studentId") Long studentId, @PathVariable("courseId") Long courseId);
}