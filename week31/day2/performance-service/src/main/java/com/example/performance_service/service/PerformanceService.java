package com.example.performance_service.service;

import java.math.BigDecimal; 
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.performance_service.config.ExamDataClient;
import com.example.performance_service.config.PredictClient;
import com.example.performance_service.dto.PerformanceDTO;
import com.example.performance_service.dto.PerformanceResponseDTO;
import com.example.performance_service.dto.PredictionRequest;
import com.example.performance_service.entity.Performance;
import com.example.performance_service.repository.EnrollmentRepository;
import com.example.performance_service.repository.PerformanceRepository;
import com.example.performance_service.util.GradeUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final JdbcTemplate jdbcTemplate;
    private final EnrollmentRepository enrollmentRepository;
//    private final RestTemplate restTemplate;
    
    @Autowired // This overrides Lombok for this specific field
    @Qualifier("externalRestTemplate")
    private RestTemplate restTemplate; // Remove 'final' here!
    
    private final ObjectMapper objectMapper;
    
    @Value("${predict.url}")
    private String predictUrl;
    
    private final PredictClient predictClient;
    
    private final ExamDataClient examDataClient; // ADD THIS
    
//    private static final String PREDICT_URL = "http://localhost:8083/api/predict/complete";

    // ==============================
    // MAIN UPSERT METHOD
    // ==============================
    @Transactional
    public Performance upsertPerformance(PerformanceDTO dto) {

        Performance performance = loadOrCreate(dto);

        updateScores(performance, dto);

        applyScoreCalculations(performance);

        Map<String, Double> bloomData =
                applyBloomAnalytics(performance);

        applyPrediction(performance);

        applyAcademicStatus(performance);

        applyDiagnostic(performance, bloomData);
                

        return performanceRepository.save(performance);
    }

    // ==============================
    // LOAD OR CREATE
    // ==============================
    private Performance loadOrCreate(PerformanceDTO dto) {
        return performanceRepository
                .findByStudentIdAndCourseId(dto.getStudentId(), dto.getCourseId())
                .orElse(new Performance());
    }

    // ==============================
    // SCORE CALCULATIONS
    // ==============================
    private void applyScoreCalculations(Performance performance) {

        BigDecimal finalScore = calculateFinalScore(performance);

        performance.setFinalScore(finalScore);
        performance.setFinalGrade(
                GradeUtil.calculateGrade(finalScore.doubleValue())
        );
    }

    private BigDecimal calculateFinalScore(Performance performance) {
    	double midtermPercentage = (safe(performance.getMidtermScore()) / 20.0) * 100;
    	

    	double weightedScore = 
    		    (safe(performance.getAttendancePercentage()) * 0.10) +
    		    (safe(performance.getQuizAverage()) * 0.20) +
    		    (safe(performance.getAssignmentAverage()) * 0.20) +
    		    (midtermPercentage * 0.50); // 🔥 Now this is accurate!

        return BigDecimal.valueOf(weightedScore)
                .setScale(2, RoundingMode.HALF_UP);
    }

    // ==============================
    // BLOOM ANALYTICS
    // ==============================
    private Map<String, Double> applyBloomAnalytics(Performance performance) {

        Map<String, Double> bloomData =
                computeBloomAnalytics(
                        performance.getStudentId(),
                        performance.getCourseId()
                );

        try {
            performance.setBloomAnalysis(objectMapper.writeValueAsString(bloomData));
        } catch (Exception e) {
            log.error("JSON Mapping Error");
        }
        return bloomData;
    }

    private Map<String, Double> computeBloomAnalytics(Long studentId, Long courseId) {

        String sql = """
                select q.bloom_level, avg(a.score) as avg_score
                from assignment_answer a
                join assignment_question q on a.question_id = q.question_id
                join assignment_attempt at on at.attempt_id = a.attempt_id
                join assignment ass on ass.assignment_id = at.assignment_id
                where at.student_id = ?
                and ass.course_id = ?
                group by q.bloom_level
                """;

        List<Map<String, Object>> rows =
                jdbcTemplate.queryForList(sql, studentId, courseId);

        Map<String, Double> result = new HashMap<>();

        for (Map<String, Object> row : rows) {
            result.put(
                    (String) row.get("bloom_level"),
                    ((Number) row.get("avg_score")).doubleValue()
            );
        }

        return result;
    }

    // ==============================
    // PREDICTION INTEGRATION
    // ==============================
    private void applyPrediction(Performance performance) {

//        Map<String, Object> prediction =
//                callPredictionService(performance);
//
//        performance.setPredictedGrade(
//                (String) prediction.get("predictedGrade")
//        );
//
//        performance.setRiskLevel(
//                (String) prediction.get("riskLevel")
//        );
//
//        Number confidenceValue =
//                (Number) prediction.get("confidence");
//
//        performance.setPredictionConfidence(
//                confidenceValue != null
//                        ? BigDecimal.valueOf(confidenceValue.doubleValue())
//                        : BigDecimal.ZERO
//        );
//    }
    	log.info("Sending data to Prediction Service for Student: {}", performance.getStudentId());
        Map<String, Object> prediction = callPredictionService(performance);
        
        performance.setPredictedGrade((String) prediction.get("predictedGrade"));
        performance.setRiskLevel((String) prediction.get("riskLevel"));
        
        Number confidenceValue = (Number) prediction.get("confidence");
        performance.setPredictionConfidence(confidenceValue != null ? 
                BigDecimal.valueOf(confidenceValue.doubleValue()) : BigDecimal.ZERO);
        
        log.info("🎯 ML Prediction Result -> Grade: {}, Risk: {}, Confidence: {}", 
                performance.getPredictedGrade(), performance.getRiskLevel(), performance.getPredictionConfidence());
    }

    private Map<String, Object> callPredictionService(Performance performance) {
        try {
            PredictionRequest request = new PredictionRequest(
                    safe(performance.getAttendancePercentage()),
                    safe(performance.getQuizAverage()),
                    safe(performance.getAssignmentAverage()),
                    safe(performance.getMidtermScore()),
                    performance.getParticipationScore() != null
                            ? performance.getParticipationScore()
                            : 5,
                    safe(performance.getStudyHoursPerWeek()),
                    safe(normalizeGpa(performance.getPreviousGpa()))
            );

            return predictClient.completePredict(request);

        } catch (Exception ex) {
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("predictedGrade", "UNKNOWN");
            fallback.put("riskLevel", "MEDIUM");
            fallback.put("confidence", 0.0);
            return fallback;
        }
    }
//        request.put("attendancePercentage",
//                safe(performance.getAttendancePercentage()));
//
//        request.put("quizAverage",
//                safe(performance.getQuizAverage()));
//
//        request.put("assignmentAverage",
//                safe(performance.getAssignmentAverage()));
//
//        request.put("midTermScore",
//                safe(performance.getMidtermScore()));
//
//        request.put("participationScore",
//                performance.getParticipationScore() != null
//                        ? performance.getParticipationScore()
//                        : 5);
//
//        request.put("studyHoursPerWeek",
//                safe(performance.getStudyHoursPerWeek()));
//
//        request.put("previousGpa",
//                safe(normalizeGpa(performance.getPreviousGpa())));

        

        //return restTemplate.postForObject(predictUrl, request, Map.class);
    

    
    
    // ==============================
    // ACADEMIC STATUS
    // ==============================
    private void applyAcademicStatus(Performance performance) {

        double finalScore = performance.getFinalScore().doubleValue();
        double confidence = performance.getPredictionConfidence().doubleValue();

        if ("HIGH".equalsIgnoreCase(performance.getRiskLevel())) {
            performance.setAcademicStatus("AT_RISK");
            return;
        }

        if (finalScore < 60) {
            performance.setAcademicStatus("AT_RISK");
            return;
        }

        if (confidence > 0.90 && finalScore < 70) {
            performance.setAcademicStatus("AT_RISK");
            return;
        }

        if (finalScore >= 85) {
            performance.setAcademicStatus("EXCELLENT");
            return;
        }

        performance.setAcademicStatus("STABLE");
    }

    // ==============================
    // DIAGNOSTIC ENGINE
    // ==============================
    private void applyDiagnostic(
            Performance performance,
            Map<String, Double> bloom) {

        StringBuilder sb = new StringBuilder();

        if (safe(performance.getQuizAverage()) < 60)
            sb.append("Conceptual understanding needs improvement. ");

        if (safe(performance.getAssignmentAverage()) < 60)
            sb.append("Descriptive reasoning skills are weak. ");

        if (safe(performance.getMidtermScore()) < 60)
            sb.append("Exam preparation is insufficient. ");

        bloom.forEach((level, score) -> {
            if (score < 50) {
                sb.append("Weak performance in ")
                  .append(level)
                  .append(" cognitive level. ");
            }
        });

        if ("HIGH".equalsIgnoreCase(performance.getRiskLevel()))
            sb.append("High academic risk detected. Immediate attention required.");

        if (sb.length() == 0)
            sb.append("Overall academic performance is strong and consistent.");

        performance.setDiagnosticFeedback(sb.toString());
    }

    // ==============================
    // UTILITY METHODS
    // ==============================
    private BigDecimal normalizeGpa(BigDecimal previousGpa) {

        if (previousGpa == null) {
            return BigDecimal.ZERO;
        }

        return previousGpa
                .divide(BigDecimal.valueOf(10.0), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(4.0));
    }

    private double safe(BigDecimal value) {
        return value != null ? value.doubleValue() : 0.0;
    }

    private double safe(Integer value) {
        return value != null ? value.doubleValue() : 0.0;
    }

    // ==============================
    // READ API
    // ==============================
    @Transactional(readOnly = true)
    public PerformanceResponseDTO getPerformance(Long studentId, Long courseId) {
        // 🔥 SENIOR FIX: Don't throw RuntimeException, return a "Zero" state if first-time student
        Performance performance = performanceRepository
                .findByStudentIdAndCourseId(studentId, courseId)
                .orElseGet(() -> {
                    Performance p = new Performance();
                    p.setStudentId(studentId);
                    p.setCourseId(courseId);
                    p.setFinalScore(BigDecimal.ZERO);
                    p.setAcademicStatus("INITIALIZING");
                    p.setRiskLevel("LOW");
                    return p;
                });

        return PerformanceResponseDTO.builder()
                .performanceId(performance.getPerformanceId())
                .studentId(performance.getStudentId())
                .courseId(performance.getCourseId())
                .finalScore(performance.getFinalScore())
                .finalGrade(performance.getFinalGrade() != null ? performance.getFinalGrade() : "N/A")
                .predictedGrade(performance.getPredictedGrade())
                .riskLevel(performance.getRiskLevel())
                .academicStatus(performance.getAcademicStatus())
                .diagnosticFeedback(performance.getDiagnosticFeedback())
                .bloomAnalysis(performance.getBloomAnalysis())
                .build();
    }

    private void updateScores(Performance performance, PerformanceDTO dto) {

        performance.setStudentId(dto.getStudentId());
        performance.setCourseId(dto.getCourseId());

        log.info("📡 Requesting real-time Quiz Average from Exam Service for Student: {}", dto.getStudentId());
        try {
            Double realQuizAvg = examDataClient.getQuizAverage(dto.getStudentId(), dto.getCourseId());
            log.info("📥 Received Quiz Average: {} for Student: {}", realQuizAvg, dto.getStudentId());
            performance.setQuizAverage(realQuizAvg != null ? BigDecimal.valueOf(realQuizAvg) : BigDecimal.ZERO);
        } catch (Exception e) {
            log.error("❌ Failed to fetch real-time quiz average via Feign: {}", e.getMessage());
            if (dto.getQuizAverage() != null) {
                log.info("🔄 Falling back to DTO provided Quiz Average: {}", dto.getQuizAverage());
                performance.setQuizAverage(dto.getQuizAverage());
            }
        }
        
        if (dto.getAttendancePercentage() != null) performance.setAttendancePercentage(dto.getAttendancePercentage());
        if (dto.getAssignmentAverage() != null) performance.setAssignmentAverage(dto.getAssignmentAverage());
        if (dto.getMidtermScore() != null) performance.setMidtermScore(dto.getMidtermScore());
        if (dto.getParticipationScore() != null) performance.setParticipationScore(dto.getParticipationScore());
        if (dto.getStudyHoursPerWeek() != null) performance.setStudyHoursPerWeek(dto.getStudyHoursPerWeek());
        if (dto.getPreviousGpa() != null) performance.setPreviousGpa(dto.getPreviousGpa());
        if (dto.getUpdatedBy() != null) performance.setUpdatedBy(dto.getUpdatedBy());
        
    }
    
 // 🔥 ADD THIS TO PerformanceService.java
    public Map<String, Object> getStudentGlobalSummary(Long studentId) {
        List<Performance> allPerf = performanceRepository.findAllByStudentId(studentId);
        
        String sql = "SELECT semester FROM student_analytics_user.students WHERE user_id = ?";
        String currentSemester = jdbcTemplate.queryForObject(sql, String.class, studentId);
        
        
        double avgScore = allPerf.stream()
            .mapToDouble(p -> p.getFinalScore().doubleValue())
            .average().orElse(0.0);
            
        int completedTasks = allPerf.size(); // Simplified for demo

        Map<String, Object> summary = new HashMap<>();
        summary.put("gpa", avgScore / 25.0); // Converting 100% scale to 4.0 scale
        summary.put("totalTasks", completedTasks);
        summary.put("academicStanding", avgScore > 80 ? "Excellent" : "Stable");
        summary.put("semester", currentSemester != null ? currentSemester : "1");
        return summary;
    }
}