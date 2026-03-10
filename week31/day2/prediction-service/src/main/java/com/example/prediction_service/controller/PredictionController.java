package com.example.prediction_service.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.prediction_service.dto.GradePredictionResponse;
import com.example.prediction_service.dto.PredictionRequest;
import com.example.prediction_service.dto.PredictionResponseDTO;
import com.example.prediction_service.util.ModelLoader;
import com.example.prediction_service.util.ModelLoader.RiskPrediction;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/predict")
@RequiredArgsConstructor
public class PredictionController {

//	@Autowired
	private final ModelLoader modelLoader;
	
	@GetMapping("/health")
	public ResponseEntity<Map<String, String>> health(){
		Map<String, String> response = new HashMap<>();
		response.put("status", "UP");
		response.put("service", "prediction-service");
		response.put("version", "1.0.0");
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("/model-info")
	public ResponseEntity<Map<String, Object>> getModelInfo(){
		Map<String, Object> info = new HashMap<>();
		info.put("features", modelLoader.getFeatures());
		info.put("gradeClasses", modelLoader.getGradeClasses());
		info.put("featureCount", modelLoader.getFeatures().size());
		
		return ResponseEntity.ok(info);
	}
	

	@GetMapping("/grade")
	public ResponseEntity<?> predictGrade(@ModelAttribute
			PredictionRequest request)
	{
	try {
		if(!request.isValid())
		{
//			return null;
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(request);
		};
	
		double [] features = request.toFeatureArray();
		
		String predictionGrade = modelLoader.predictGrade(features);
		return ResponseEntity.ok(
				new GradePredictionResponse(predictionGrade));
	}
	catch(Exception e)
	{
		Map<String, String> error = new HashMap<>();
		error.put("error", e.getMessage());
		return ResponseEntity.status(
				HttpStatus.INTERNAL_SERVER_ERROR
				).body(error);
	}
	
	}
	
	@GetMapping("/risk")
	public ResponseEntity<?> accessRisk(@ModelAttribute
			PredictionRequest request)
	{
		try {
			if(!request.isValid())
			{
//				return null;
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(request);
			};
		
			double [] features = request.toFeatureArray();
			
			RiskPrediction risk = modelLoader.assessRisk(features);
			
			Map<String, Object> response = new HashMap<>();
			response.put("prediction",risk.prediction);
			response.put("riskLevel", risk.riskLevel);
			response.put("confidence", risk.confidence);
			response.put("passProbability", Math.round(risk.confidence * 10000.0)/10000.0);
			response.put("timeStamp", java.time.Instant.now().toString());
			
			return ResponseEntity.ok(response);
			
		} catch (Exception e) {
			  Map<String, String> error = new HashMap<>();
	            error.put("error", e.getMessage());
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
		}
		
	}
	
	@PostMapping("/complete")
	public ResponseEntity<?> completePredict(@RequestBody PredictionRequest request)
	{
	    try {
	        if (!request.isValid()) {
	            return ResponseEntity.badRequest()
	                    .body(Map.of("error", "Invalid input values"));
	        }

	        double[] features = request.toFeatureArray();

	        String grade = modelLoader.predictGrade(features);
	        double score = modelLoader.predictScore(features);
	        RiskPrediction risk = modelLoader.assessRisk(features);

	        PredictionResponseDTO response = new PredictionResponseDTO();
	        response.setPredictedGrade(grade);
	        response.setPredictedScore(Math.round(score * 100.0) / 100.0);
	        response.setRiskPrediction(risk.prediction);
	        response.setRiskLevel(risk.riskLevel);
	        response.setConfidence(Math.round(risk.confidence * 10000.0) / 10000.0);
	        response.setPassProbability(Math.round(risk.passProbability * 10000.0) / 10000.0);
	        response.setTimeStamp(java.time.Instant.now().toString());

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("error", e.getMessage()));
	    }
	}
	
	@PostMapping("/batch")
	public ResponseEntity<?> batchPredict(@RequestBody PredictionRequest[] requests)
	{
		try {
			Map<String, Object>[] results = new HashMap[requests.length];
			
			for(int i=0;i<requests.length;i++)
			{
				PredictionRequest request = requests[i];
			
			
			if(!request.isValid()) {
				Map<String, Object> error = new HashMap<>();
				error.put("error", "Invalid input at index "+ i);
				results[i]= error;
				continue;
			}
			
			double[] features = request.toFeatureArray();
			
			String grade = modelLoader.predictGrade(features);
			double score = modelLoader.predictScore(features);
			RiskPrediction risk = modelLoader.assessRisk(features);
			
			results[i] = new HashMap<>();
			results[i].put("index", i);
			results[i].put("predictionGrade", grade);
			results[i].put("predictedScore", Math.round(score * 100.0)/100.0);
			results[i].put("riskLevel", risk.riskLevel);
			}
			
			Map<String, Object> finalResponse = new HashMap<>();
			finalResponse.put("results", results);
			finalResponse.put("count", results.length);
		
			return ResponseEntity.ok(finalResponse);
		}catch (Exception e) {
			
			Map<String, String> error = new HashMap<>();
			error.put("error", e.getMessage());
			return ResponseEntity.status(
					HttpStatus.INTERNAL_SERVER_ERROR
					).body(error);
		}
	}
	
}
