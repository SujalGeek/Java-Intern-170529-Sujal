package com.example.prediction_service.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import com.example.prediction_service.PredictionServiceApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

@Component
public class ModelLoader {

    private final PredictionServiceApplication predictionServiceApplication;

	private final ObjectMapper objectMapper = new ObjectMapper();
	
	private JsonNode gradeClassifierModel;
	private JsonNode scorePredictiorModel;
	private JsonNode riskAssessorModel;
	
	private List<String> features;
	private List<String> gradeClasses;

    ModelLoader(PredictionServiceApplication predictionServiceApplication) {
        this.predictionServiceApplication = predictionServiceApplication;
    }
	
	@PostConstruct
	public void init() {
		System.out.println("Loading ML models");
		try {
			loadGradeClassifier();
			loadScorePredictor();
			loadRiskAssessor();
			System.out.println("All models loaded successfully");
		} catch(IOException ex)
		{
			System.out.println("Error loading models:"+ex.getMessage());
			ex.printStackTrace();
		}
		
		
	}

	private void loadRiskAssessor() throws IOException{
		// TODO Auto-generated method stub
		InputStream is = new ClassPathResource("models/grade_classifier.json").getInputStream();
		gradeClassifierModel = objectMapper.readTree(is);
		
		features = new ArrayList<>();
		
		
	}

	private void loadScorePredictor() {
		// TODO Auto-generated method stub
		
	}

	private void loadGradeClassifier() {
		// TODO Auto-generated method stub
		
	}
}
