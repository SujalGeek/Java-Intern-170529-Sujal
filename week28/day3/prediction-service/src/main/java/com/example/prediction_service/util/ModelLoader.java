package com.example.prediction_service.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class ModelLoader {

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    private JsonNode gradeClassifierModel;
    private JsonNode scorePredictorModel;
    private JsonNode riskAssessorModel;
    
    private List<String> features;
    private List<String> gradeClasses;
    
    @PostConstruct
    public void init() {
        System.out.println("Loading ML models...");
        try {
            loadGradeClassifier();
            loadScorePredictor();
            loadRiskAssessor();
            System.out.println(" All models loaded successfully!");
        } catch (IOException e) {
            System.err.println("Error loading models: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadGradeClassifier() throws IOException {
        InputStream is = new ClassPathResource("models/grade_classifier.json").getInputStream();
        gradeClassifierModel = objectMapper.readTree(is);
        
        // Extract features
        features = new ArrayList<>();
        gradeClassifierModel.get("features").forEach(node -> features.add(node.asText()));
        
        // Extract classes
        gradeClasses = new ArrayList<>();
        gradeClassifierModel.get("classes").forEach(node -> gradeClasses.add(node.asText()));
        
        System.out.println("Loaded Grade Classifier: " + gradeClassifierModel.get("model_type").asText());
    }
    
    private void loadScorePredictor() throws IOException {
        InputStream is = new ClassPathResource("models/score_predictor.json").getInputStream();
        scorePredictorModel = objectMapper.readTree(is);
        System.out.println("Loaded Score Predictor: " + scorePredictorModel.get("model_type").asText());
    }
    
    private void loadRiskAssessor() throws IOException {
        InputStream is = new ClassPathResource("models/risk_assessor.json").getInputStream();
        riskAssessorModel = objectMapper.readTree(is);
        System.out.println("Loaded Risk Assessor: " + riskAssessorModel.get("model_type").asText());
    }
    
   
    public String predictGrade(double[] inputFeatures) {
        try {
            // Get coefficients and intercepts
            JsonNode coefficients = gradeClassifierModel.get("coefficients");
            JsonNode intercepts = gradeClassifierModel.get("intercept");
            
            // Calculate scores for each class
            double[] scores = new double[gradeClasses.size()];
            for (int i = 0; i < gradeClasses.size(); i++) {
                double score = intercepts.get(i).asDouble();
                JsonNode classCoef = coefficients.get(i);
                for (int j = 0; j < inputFeatures.length; j++) {
                    score += classCoef.get(j).asDouble() * inputFeatures[j];
                }
                scores[i] = score;
            }
            
            // Apply softmax and find max
            double[] probabilities = softmax(scores);
            int maxIndex = argmax(probabilities);
            
            return gradeClasses.get(maxIndex);
        } catch (Exception e) {
            System.err.println("Error in grade prediction: " + e.getMessage());
            return "C"; // Default fallback
        }
    }
    
    /**
     * Predict numeric score using Linear Regression
     */
    public double predictScore(double[] inputFeatures) {
        try {
            JsonNode coefficients = scorePredictorModel.get("coefficients");
            double intercept = scorePredictorModel.get("intercept").asDouble();
            
            double score = intercept;
            for (int i = 0; i < inputFeatures.length; i++) {
                score += coefficients.get(i).asDouble() * inputFeatures[i];
            }
            
            // Clip to valid range
            return Math.max(0, Math.min(100, score));
        } catch (Exception e) {
            System.err.println("Error in score prediction: " + e.getMessage());
            return 75.0; // Default fallback
        }
    }
    
    /**
     * Assess risk (Pass/Fail prediction) using Logistic Regression
     */
    public RiskPrediction assessRisk(double[] inputFeatures) {
        try {
            JsonNode coefficients = riskAssessorModel.get("coefficients");
            JsonNode intercepts = riskAssessorModel.get("intercept");
            JsonNode classes = riskAssessorModel.get("classes");
            
            // Binary classification
            double score0 = intercepts.get(0).asDouble();
            double score1 = intercepts.get(1).asDouble();
            
            JsonNode coef0 = coefficients.get(0);
            JsonNode coef1 = coefficients.get(1);
            
            for (int i = 0; i < inputFeatures.length; i++) {
                score0 += coef0.get(i).asDouble() * inputFeatures[i];
                score1 += coef1.get(i).asDouble() * inputFeatures[i];
            }
            
            double[] probabilities = softmax(new double[]{score0, score1});
            int predictedClass = argmax(probabilities);
            
            String prediction = classes.get(predictedClass).asText();
            double confidence = probabilities[predictedClass];
            
            // Determine risk level
            String riskLevel;
            if (prediction.equals("Fail") || probabilities[0] > 0.3) {
                riskLevel = "High";
            } else if (probabilities[0] > 0.15) {
                riskLevel = "Medium";
            } else {
                riskLevel = "Low";
            }
            
            return new RiskPrediction(prediction, confidence, riskLevel, probabilities[1]);
        } catch (Exception e) {
            System.err.println("Error in risk assessment: " + e.getMessage());
            return new RiskPrediction("Pass", 0.75, "Medium", 0.75);
        }
    }
    
    /**
     * Softmax function for probability calculation
     */
    private double[] softmax(double[] scores) {
        double max = Double.NEGATIVE_INFINITY;
        for (double score : scores) {
            if (score > max) max = score;
        }
        
        double sum = 0.0;
        double[] result = new double[scores.length];
        for (int i = 0; i < scores.length; i++) {
            result[i] = Math.exp(scores[i] - max);
            sum += result[i];
        }
        
        for (int i = 0; i < result.length; i++) {
            result[i] /= sum;
        }
        
        return result;
    }
    
    /**
     * Find index of maximum value
     */
    private int argmax(double[] array) {
        int maxIndex = 0;
        double maxValue = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > maxValue) {
                maxValue = array[i];
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    
    public List<String> getFeatures() {
        return features;
    }
    
    public List<String> getGradeClasses() {
        return gradeClasses;
    }
    
    /**
     * Inner class for Risk Prediction results
     */
    public static class RiskPrediction {
        public final String prediction;
        public final double confidence;
        public final String riskLevel;
        public final double passProbability;
        
        public RiskPrediction(String prediction, double confidence, String riskLevel, double passProbability) {
            this.prediction = prediction;
            this.confidence = confidence;
            this.riskLevel = riskLevel;
            this.passProbability = passProbability;
        }
    }
}