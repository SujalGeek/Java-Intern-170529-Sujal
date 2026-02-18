package com.example.prediction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradePredictionResponse {

	private String predictionGrade;
	private String timeStamp;


	public GradePredictionResponse(String predictedGrade) {
        this.predictionGrade = predictedGrade;
        this.timeStamp = java.time.Instant.now().toString();
    }

}
