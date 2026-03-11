package com.example.prediction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScorePredictionResponse {

	private Double predictionScore;
	private String timestamp;
}
