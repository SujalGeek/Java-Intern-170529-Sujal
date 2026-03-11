package com.example.prediction_service.dto;

import lombok.Data;

@Data
public class PredictionResponseDTO {

	private String predictedGrade;
	private Double predictedScore;
	private String riskPrediction;
	private Double confidence;
	private Double passProbability;
	private String timeStamp;
	private String riskLevel;
}
