package com.example.ai_integration_service.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//Inside com.example.ai_integration_service.client (if using Feign)
@FeignClient(name = "quiz-service")
public interface QuizClient {
 @GetMapping("/api/quiz/average/{studentId}/{courseId}")
 Double getAverageForStudent(@PathVariable Long studentId, @PathVariable Long courseId);
}