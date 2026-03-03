package com.example.ai_integration_service.config;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.ai_integration_service.dto.PerformanceDTO;

@FeignClient(name = "performance-service")
public interface PerformanceClient {

    @PostMapping("/api/performance/upsert")
    void upsertPerformance(PerformanceDTO dto);
}