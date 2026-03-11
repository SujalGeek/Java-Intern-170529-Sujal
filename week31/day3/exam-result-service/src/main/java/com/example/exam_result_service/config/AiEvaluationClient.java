package com.example.exam_result_service.config;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "ai-integration-service")
public interface AiEvaluationClient {

    @PostMapping("/api/v1/exams/evaluate")
    Map<String, Object> evaluate(Map<String, Object> request);
}
