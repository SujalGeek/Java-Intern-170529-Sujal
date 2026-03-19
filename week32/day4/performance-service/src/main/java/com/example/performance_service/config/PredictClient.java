package com.example.performance_service.config;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.performance_service.dto.PredictionRequest;

@FeignClient(name = "prediction-service")
public interface PredictClient {

	 @PostMapping("/api/predict/complete")
	  Map<String, Object> completePredict(@RequestBody PredictionRequest request);
}
