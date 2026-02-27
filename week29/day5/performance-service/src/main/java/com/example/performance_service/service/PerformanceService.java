package com.example.performance_service.service;

import java.math.BigDecimal; 
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.performance_service.config.PredictClient;
import com.example.performance_service.dto.PerformanceDTO;
import com.example.performance_service.dto.PerformanceResponseDTO;
import com.example.performance_service.dto.PredictionRequest;
import com.example.performance_service.entity.Performance;
import com.example.performance_service.repository.PerformanceRepository;
import com.example.performance_service.util.GradeUtil;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final JdbcTemplate jdbcTemplate;
//    private final RestTemplate restTemplate;

    @Value("${predict.url}")
    private String predictUrl;
    
    private final PredictClient predictClient;
    
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

        double weightedScore =
                (safe(performance.getAttendancePercentage()) * 0.10) +
                (safe(performance.getQuizAverage()) * 0.20) +
                (safe(performance.getAssignmentAverage()) * 0.20) +
                (safe(performance.getMidtermScore()) * 0.50);

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

        performance.setBloomAnalysis(bloomData.toString());

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

        Map<String, Object> prediction =
                callPredictionService(performance);

        performance.setPredictedGrade(
                (String) prediction.get("predictedGrade")
        );

        performance.setRiskLevel(
                (String) prediction.get("riskLevel")
        );

        Number confidenceValue =
                (Number) prediction.get("confidence");

        performance.setPredictionConfidence(
                confidenceValue != null
                        ? BigDecimal.valueOf(confidenceValue.doubleValue())
                        : BigDecimal.ZERO
        );
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

        Performance performance =
                performanceRepository
                        .findByStudentIdAndCourseId(studentId, courseId)
                        .orElseThrow(() ->
                                new RuntimeException("Performance not found"));

        return PerformanceResponseDTO.builder()
                .performanceId(performance.getPerformanceId())
                .studentId(performance.getStudentId())
                .courseId(performance.getCourseId())
                .finalScore(performance.getFinalScore())
                .finalGrade(performance.getFinalGrade())
                .predictedGrade(performance.getPredictedGrade())
                .riskLevel(performance.getRiskLevel())
                .predictionConfidence(performance.getPredictionConfidence())
                .academicStatus(performance.getAcademicStatus())
                .diagnosticFeedback(performance.getDiagnosticFeedback())
                .bloomAnalysis(performance.getBloomAnalysis())
                .build();
    }

    private void updateScores(Performance performance, PerformanceDTO dto) {

        performance.setStudentId(dto.getStudentId());
        performance.setCourseId(dto.getCourseId());

        if (dto.getAttendancePercentage() != null)
            performance.setAttendancePercentage(dto.getAttendancePercentage());

        if (dto.getQuizAverage() != null)
            performance.setQuizAverage(dto.getQuizAverage());

        if (dto.getAssignmentAverage() != null)
            performance.setAssignmentAverage(dto.getAssignmentAverage());

        if (dto.getMidtermScore() != null)
            performance.setMidtermScore(dto.getMidtermScore());

        if (dto.getParticipationScore() != null)
            performance.setParticipationScore(dto.getParticipationScore());

        if (dto.getStudyHoursPerWeek() != null)
            performance.setStudyHoursPerWeek(dto.getStudyHoursPerWeek());

        if (dto.getPreviousGpa() != null)
            performance.setPreviousGpa(dto.getPreviousGpa());

        if (dto.getUpdatedBy() != null)
            performance.setUpdatedBy(dto.getUpdatedBy());
    }
}