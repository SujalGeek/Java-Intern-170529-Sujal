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

        BigDecimal newScore = calculateFinalScore(performance);
        performance.setFinalScore(newScore); 
        performance.setFinalGrade(GradeUtil.calculateGrade(newScore.doubleValue()));
        
        performance.setUpdatedAt(java.time.LocalDateTime.now());
        
        applyScoreCalculations(performance);
        
        if (dto.getUpdatedBy() == null || dto.getUpdatedBy() == 1L) {
            applyPrediction(performance);
       }
        
        Map<String, Double> bloomData = applyBloomAnalytics(performance);
        applyAcademicStatus(performance);
        applyDiagnostic(performance, bloomData);
        

//        applyPrediction(performance);

//        applyAcademicStatus(performance);

//        applyDiagnostic(performance, bloomData);
                
        Performance saved = performanceRepository.saveAndFlush(performance);
        log.info(">>> [CRITICAL] DB Write Triggered for Student: {} | New Time: {}", 
                saved.getStudentId(), saved.getUpdatedAt());

//        return performanceRepository.save(performance);
        return saved;
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
//    	double midtermPercentage = (safe(performance.getMidtermScore()) / 20.0) * 100;
    	

    	double midterm = safe(performance.getMidtermScore());
        double quiz = safe(performance.getQuizAverage());
        double assignment = safe(performance.getAssignmentAverage());
        double attendance = safe(performance.getAttendancePercentage());
        
        double weightedScore = (attendance * 0.10) + (quiz * 0.20) + (assignment * 0.20) + (midterm * 0.50);

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
        	
        	double rawScore = ((Number) row.get("avg_score")).doubleValue();
            double percentageScore = rawScore * 10.0;
            
            percentageScore = Math.min(percentageScore, 100.0);
            
            result.put(
                    (String) row.get("bloom_level"),
                    percentageScore
//                    ((Number) row.get("avg_score")).doubleValue()
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
    	System.out.println(">>> [AI] Preparing Prediction for Student: " + performance.getStudentId());
    	System.out.println(">>> [AI] Input Features -> M: " + performance.getMidtermScore() + 
                ", Q: " + performance.getQuizAverage() + 
                ", A: " + performance.getAssignmentAverage());
    	log.info("Sending data to Prediction Service for Student: {}", performance.getStudentId());
        Map<String, Object> prediction = callPredictionService(performance);
        
        System.out.println(">>> [AI] RAW Prediction Response: " + prediction);
        
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
    @Transactional
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
                .predictionConfidence(performance.getPredictionConfidence())
                .bloomAnalysis(performance.getBloomAnalysis())
                .build();
    }

//    private void updateScores(Performance performance, PerformanceDTO dto) {
//
//        performance.setStudentId(dto.getStudentId());
//        performance.setCourseId(dto.getCourseId());
//
//        log.info("📡 Requesting real-time Quiz Average from Exam Service for Student: {}", dto.getStudentId());
//        try {
//            Double realQuizAvg = examDataClient.getQuizAverage(dto.getStudentId(), dto.getCourseId());
//            log.info("📥 Received Quiz Average: {} for Student: {}", realQuizAvg, dto.getStudentId());
//            performance.setQuizAverage(realQuizAvg != null ? BigDecimal.valueOf(realQuizAvg) : BigDecimal.ZERO);
//        } catch (Exception e) {
//            log.error("❌ Failed to fetch real-time quiz average via Feign: {}", e.getMessage());
//            if (dto.getQuizAverage() != null) {
//                log.info("🔄 Falling back to DTO provided Quiz Average: {}", dto.getQuizAverage());
//                performance.setQuizAverage(dto.getQuizAverage());
//            }
//        }
//        if (dto.getQuizAverage() != null) performance.setQuizAverage(dto.getQuizAverage());
//        if (dto.getAssignmentAverage() != null) performance.setAssignmentAverage(dto.getAssignmentAverage());
//        if (dto.getMidtermScore() != null) performance.setMidtermScore(dto.getMidtermScore());
////        }
//        
//        if (dto.getAttendancePercentage() != null) performance.setAttendancePercentage(dto.getAttendancePercentage());
////        if (dto.getAssignmentAverage() != null) performance.setAssignmentAverage(dto.getAssignmentAverage());
////        if (dto.getMidtermScore() != null) performance.setMidtermScore(dto.getMidtermScore());
//        if (dto.getParticipationScore() != null) performance.setParticipationScore(dto.getParticipationScore());
//        if (dto.getStudyHoursPerWeek() != null) performance.setStudyHoursPerWeek(dto.getStudyHoursPerWeek());
//        if (dto.getPreviousGpa() != null) performance.setPreviousGpa(dto.getPreviousGpa());
//        if (dto.getUpdatedBy() != null) {
//            performance.setUpdatedBy(dto.getUpdatedBy());
//        } else {
//            // 🔥 AGAR FRONTEND SE NAHI AA RHA TOH FORCE SET KARO TESTING KE LIYE
//            performance.setUpdatedBy(1L); 
//        }
//         
//    }
    private void updateScores(Performance performance, PerformanceDTO dto) {
        performance.setStudentId(dto.getStudentId());
        performance.setCourseId(dto.getCourseId());

        // 1. 🔥 LIVE MIDTERM RECALCULATION (The "3-to-2 marks" Fix)
        log.info(">>> Recalculating Midterm Score from DB for Student: {}", dto.getStudentId());
        try {
            String sql = "SELECT SUM(score) as obtained, SUM(marks) as total FROM exam_result WHERE student_id = ? AND course_id = ?";
            Map<String, Object> results = jdbcTemplate.queryForMap(sql, dto.getStudentId(), dto.getCourseId());
            
            if (results.get("obtained") != null) {
                double obtained = ((Number) results.get("obtained")).doubleValue();
                double total = ((Number) results.get("total")).doubleValue();
                double newMidtermPercent = (obtained / total) * 100;
                
                performance.setMidtermScore(BigDecimal.valueOf(newMidtermPercent).setScale(2, RoundingMode.HALF_UP));
                log.info(">>> New Midterm Score Calculated: {}", newMidtermPercent);
            }
        } catch (Exception e) {
            log.error(">>> DB Recalculation failed, falling back to DTO: {}", e.getMessage());
            if (dto.getMidtermScore() != null) performance.setMidtermScore(dto.getMidtermScore());
        }

        // 2. 📡 LIVE QUIZ FETCH (The "examClient" you were missing)
        log.info("📡 Requesting real-time Quiz Average via Feign for Student: {}", dto.getStudentId());
        try {
            Double realQuizAvg = examDataClient.getQuizAverage(dto.getStudentId(), dto.getCourseId());
            if (realQuizAvg != null) {
                performance.setQuizAverage(BigDecimal.valueOf(realQuizAvg).setScale(2, RoundingMode.HALF_UP));
                log.info("📥 Received Quiz Average: {}", realQuizAvg);
            }
        } catch (Exception e) {
            log.error("❌ Feign fetch failed: {}", e.getMessage());
            // Fallback to DTO if Feign fails
            if (dto.getQuizAverage() != null) performance.setQuizAverage(dto.getQuizAverage());
        }

        // 3. MAP REMAINING FIELDS FROM DTO
        if (dto.getAssignmentAverage() != null) performance.setAssignmentAverage(dto.getAssignmentAverage());
        if (dto.getAttendancePercentage() != null) performance.setAttendancePercentage(dto.getAttendancePercentage());
        if (dto.getParticipationScore() != null) performance.setParticipationScore(dto.getParticipationScore());
        if (dto.getStudyHoursPerWeek() != null) performance.setStudyHoursPerWeek(dto.getStudyHoursPerWeek());
        if (dto.getPreviousGpa() != null) performance.setPreviousGpa(dto.getPreviousGpa());
        
        // Safety for updatedBy
        performance.setUpdatedBy(dto.getUpdatedBy() != null ? dto.getUpdatedBy() : 1L);
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
    
    @Transactional
    public void syncManualOverride(Long studentId, Long courseId) {
        // 1. Puraane saare scores nikaalo is student ke liye is course mein
        String sql = "SELECT SUM(score) as total_obtained, SUM(marks) as total_possible FROM exam_result WHERE student_id = ? AND course_id = ?";
        
        Map<String, Object> totals = jdbcTemplate.queryForMap(sql, studentId, courseId);
        
        double obtained = ((Number) totals.get("total_obtained")).doubleValue();
        double possible = ((Number) totals.get("total_possible")).doubleValue();
        
        // 2. Naya Midterm Percentage calculate karo
        double newMidtermPercent = (obtained / possible) * 100;

        // 3. Performance record load karo aur update karo
        Performance perf = performanceRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new RuntimeException("Performance record not found"));
        
        perf.setMidtermScore(BigDecimal.valueOf(newMidtermPercent).setScale(2, RoundingMode.HALF_UP));
        
        // 4. Pura final score recalculate karo (Weighted Average)
        applyScoreCalculations(perf); 
        
        // 5. Save and Force Flush
        performanceRepository.saveAndFlush(perf);
        
        log.info(">>> [BRIDGE FIXED] Manual Override Synced! New Midterm %: {}", newMidtermPercent);
    }
}