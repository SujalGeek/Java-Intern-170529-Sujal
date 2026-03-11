package com.example.performance_service.service;
//
//import org.springframework.dao.EmptyResultDataAccessException;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.http.ResponseEntity;
//
//import lombok.RequiredArgsConstructor;
//import java.util.Map;
//import java.util.HashMap;

//@Service
//@RequiredArgsConstructor
////public class PredictionPipelineService {
//
//    // private final JdbcTemplate jdbcTemplate;
//    
//    // 🔥 Injecting RestTemplate to communicate across microservices!
//    private final RestTemplate restTemplate; 
//
//    @Transactional
//    public Map<String, Object> generateAndSavePrediction(Long studentId, Long courseId) {
//        
//        // 1. EXTRACT REAL DATABASE METRICS
//        double quizAvg = getSafeDouble("SELECT AVG(score) FROM quiz_attempts WHERE student_id = ? AND course_id = ?", studentId, courseId);
//        if (quizAvg == 0.0) quizAvg = 65.0; 
//
//        double assignmentAvg = getSafeDouble("SELECT AVG(score) FROM submissions WHERE student_id = ? AND course_id = ?", studentId, courseId);
//        if (assignmentAvg == 0.0) assignmentAvg = quizAvg; 
//
//        double midtermScore = getSafeDouble("SELECT AVG(score) FROM exam_result WHERE student_id = ? AND course_id = ?", studentId, courseId);
//        if (midtermScore == 0.0) midtermScore = quizAvg - 5.0; 
//
//        double attendance = getSafeDouble("SELECT attendance_percentage FROM performance WHERE student_id = ? AND course_id = ?", studentId, courseId);
//        if (attendance == 0.0) attendance = 85.0; 
//
//        // 2. ESTIMATE SOFT METRICS
//        double participation = Math.max(1.0, Math.min(10.0, attendance / 10.0));
//        double studyHours = Math.min(20.0, (quizAvg / 100.0) * 15.0);
//        double previousGpa = 3.0;
//
//        // 3. BUILD THE JSON PAYLOAD FOR PREDICTION SERVICE
//        // These keys must perfectly match your PredictionRequest DTO in prediction-service
//        Map<String, Object> predictionRequest = new HashMap<>();
//        predictionRequest.put("attendancePercentage", attendance);
//        predictionRequest.put("quizAverage", quizAvg);
//        predictionRequest.put("assignmentAverage", assignmentAvg);
//        predictionRequest.put("midtermScore", midtermScore);
//        predictionRequest.put("participationScore", participation);
//        predictionRequest.put("studyHoursPerWeek", studyHours);
//        predictionRequest.put("previousGpa", previousGpa);
//
//        // 4. 🔥 MAKE REST CALL TO PREDICTION SERVICE 🔥
//        // Assuming your prediction service is registered with Eureka as "PREDICTION-SERVICE"
//        // If not using Eureka load balancing, change this to "http://localhost:808X/api/predict/complete"
//        String aiEngineUrl = "http://PREDICTION-SERVICE/api/predict/complete";
//        
//        ResponseEntity<Map> response = restTemplate.postForEntity(aiEngineUrl, predictionRequest, Map.class);
//        Map<String, Object> predictionResult = response.getBody();
//
//        // 5. EXTRACT THE AI RESPONSE
//        String predictedGrade = (String) predictionResult.get("predictedGrade");
//        Double predictedScore = ((Number) predictionResult.get("predictedScore")).doubleValue();
//        String riskLevel = (String) predictionResult.get("riskLevel");
//        Double passProbability = ((Number) predictionResult.get("passProbability")).doubleValue();
//        Double confidence = ((Number) predictionResult.get("confidence")).doubleValue();
//
//        // 6. UPSERT THE RESULTS INTO PERFORMANCE TABLE
//        String upsertSql = "INSERT INTO performance (student_id, course_id, attendance_percentage, current_average, " +
//                           "predicted_grade, final_score, risk_level, prediction_confidence, updated_at) " +
//                           "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW()) " +
//                           "ON DUPLICATE KEY UPDATE " +
//                           "attendance_percentage = VALUES(attendance_percentage), " +
//                           "current_average = VALUES(current_average), " +
//                           "predicted_grade = VALUES(predicted_grade), " +
//                           "final_score = VALUES(final_score), " +
//                           "risk_level = VALUES(risk_level), " +
//                           "prediction_confidence = VALUES(prediction_confidence), " +
//                           "updated_at = NOW()";
//
//        jdbcTemplate.update(upsertSql, 
//            studentId, courseId, attendance, ((quizAvg + assignmentAvg + midtermScore) / 3.0),
//            predictedGrade, predictedScore, riskLevel, confidence);
//
//        // 7. RETURN SUCCESS RESPONSE
//        return Map.of(
//            "studentId", studentId,
//            "courseId", courseId,
//            "predictedGrade", predictedGrade,
//            "predictedScore", predictedScore,
//            "riskLevel", riskLevel,
//            "passProbability", passProbability,
//            "confidence", confidence // ADD THIS LINE SO REACT GETS THE %!
//        );
//    }
//
//    private Double getSafeDouble(String sql, Object... params) {
//        try {
//            Double value = jdbcTemplate.queryForObject(sql, Double.class, params);
//            return value != null ? value : 0.0;
//        } catch (EmptyResultDataAccessException | NullPointerException e) {
//            return 0.0;
//        }
//    }
//}
