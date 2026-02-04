package com.example.prediction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GradePredictionResponse {

	private String predictionGrade;
	private String timeStamp;
}
