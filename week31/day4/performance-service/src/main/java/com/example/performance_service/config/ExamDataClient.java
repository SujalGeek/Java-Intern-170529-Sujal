package com.example.performance_service.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "exam-result-service")
public interface ExamDataClient {
	
    @GetMapping("/api/exam-results/average/{studentId}/{courseId}")
    Double getQuizAverage(@PathVariable("studentId") Long studentId, @PathVariable("courseId") Long courseId);
}