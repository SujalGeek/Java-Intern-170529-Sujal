package com.example.performance_service.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.performance_service.dto.PerformanceDTO;
import com.example.performance_service.entity.Performance;
import com.example.performance_service.repository.PerformanceRepository;
import com.example.performance_service.util.GradeUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceService {

	private final PerformanceRepository performanceRepository;
	private final JdbcTemplate jdbcTemplate;
	private final RestTemplate restTemplate;
	
	private static final String PREDICT_URL = "http://localhost:8083/api/predict/complete";
	
	
	@Transactional
	public Performance upsertPerformance(PerformanceDTO dto)
	{
//		Optional<Performance> existing = performanceRepository.findByStudentIdAndCourseId(
//				dto.getStudentId(), dto.getCourseId());
//		
//		Performance performance = existing.orElse(new Performance());
//		
//		performance.setStudentId(dto.getStudentId());
//		performance.setCourseId(dto.getCourseId());
//		
//		if(dto.getAssignmentAverage() != null)
//		{
//			performance.setAssignmentAverage(dto.getAssignmentAverage());
//		}
//		if(dto.getMidtermScore() != null)
//		{
//			performance.setMidtermScore(dto.getMidtermScore());
//		}
//		if(dto.getParticipationScore() != null)
//		{
//			performance.setParticipationScore(dto.getParticipationScore());
//		}
//		
//		if(dto.getStudyHoursPerWeek() != null)
//		{
//			performance.setStudyHoursPerWeek(dto.getStudyHoursPerWeek());
//		}
//		
//		if(dto.getPreviousGpa() != null)
//		{
//			performance.setPreviousGpa(dto.getPreviousGpa());
//		}
//		if(dto.getUpdatedBy() != null)
//		{
//			performance.setUpdatedBy(dto.getUpdatedBy());
//		}
//		
//		BigDecimal finalScore = calculateFinalScore(performance);
//		performance.setFinalScore(finalScore);
//		
//		String grade = GradeUtil.calculateGrade(finalScore.doubleValue());
//		performance.setFinalGrade(grade);
//		
//		return performanceRepository.save(performance);
		
	Performance performance = performanceRepository
			.findByStudentIdAndCourseId(dto.getStudentId(), dto.getCourseId())
			.orElse(new Performance());
	
	updateScores(performance,dto);
	
	BigDecimal finalScore = calculateFinalScore(performance);
	
	performance.setFinalScore(finalScore);
	performance.setFinalGrade(
			GradeUtil.calculateGrade(
					finalScore.doubleValue()));
	
	Map<String, Double> bloomData = computeBloomAnalytics(
			dto.getStudentId(),
			dto.getCourseId());
	
	performance.setBloomAnalysis(bloomData.toString());
	
	Map<String, Object> prediction = callPredictionService(performance);
	
	Number confidence = (Number) prediction.get("prediction");
	performance.setPredictedGrade((String) prediction.get("predictedGrade"));
	performance.setRiskLevel((String) prediction.get("riskLevel"));
	performance.setPredictionConfidence(BigDecimal.valueOf(confidence.doubleValue()));
	
	String diagonstic = generateDiagonstic(performance,bloomData);
	performance.setDiagnosticFeedback(diagonstic);

	return performanceRepository.save(performance);
	}
	
	private String generateDiagonstic(Performance performance, Map<String, Double> bloom) {
		
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

	        return sb.toString();
	    }



	private Map<String, Object> callPredictionService(Performance performance) {

		Map<String, Object> request = new HashMap<>();
		
		request.put("attendancePercentage", safe(performance.getAttendancePercentage()));
		request.put("quizAverage", safe(performance.getQuizAverage()));
		request.put("assignmentAverage", safe(performance.getAssignmentAverage()));
		request.put("midTermScore", safe(performance.getMidtermScore()));
		request.put("participationScore",
                performance.getParticipationScore());
        request.put("studyHoursPerWeek",
                safe(performance.getStudyHoursPerWeek()));
        request.put("previousGpa",
                safe(normalizeGpa(performance.getPreviousGpa())));
		
        System.out.println("==== PREDICTION REQUEST PAYLOAD ====");
        System.out.println(request);
        
		return restTemplate.postForObject(PREDICT_URL, request, Map.class);
	}



	private BigDecimal normalizeGpa(BigDecimal previousGpa) {

	    if (previousGpa == null) {
	        return BigDecimal.ZERO;
	    }

	    return previousGpa
	            .divide(BigDecimal.valueOf(10.0), 4, RoundingMode.HALF_UP)
	            .multiply(BigDecimal.valueOf(4.0));
	}
	
	private Map<String, Double> computeBloomAnalytics(Long studentId, Long courseId) {

		String sql = """
				select q.bloom_level, avg(a.score) as avg_score from assignment_answer a
				join assignment_question q on a.question_id = q.question_id
				join assignment_attempt at
				on at.attempt_id = a.attempt_id
				join assignment ass
				on ass.assignment_id = at.assignment_id
				where at.student_id = ?
				and ass.course_id = ? 
				group by q.bloom_level
				""";
		
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql,studentId,courseId);
		Map<String, Double> result = new HashMap<>(); 
		
		for(Map<String, Object> row : rows)
		{
			result.put((String) row.get("bloom_level"),((Number) row.get("avg_score")).doubleValue());
		}
		return result;
	}


	private void updateScores(Performance performance, PerformanceDTO dto) {
		performance.setStudentId(dto.getStudentId());
		performance.setCourseId(dto.getCourseId());
		
		if(dto.getAttendancePercentage() != null)
		{
			performance.setAttendancePercentage(dto.getAttendancePercentage());
		}
		
		if(dto.getQuizAverage() != null)
		{
			performance.setQuizAverage(dto.getQuizAverage());
		}
		
		if(dto.getAssignmentAverage() != null)
		{
			performance.setAssignmentAverage(dto.getAssignmentAverage());
		}
		
		if(dto.getMidtermScore() != null)
		{
			performance.setMidtermScore(dto.getMidtermScore());
		}
		
		if(dto.getParticipationScore() != null)
		{
			performance.setParticipationScore(dto.getParticipationScore());
		}
		
		if(dto.getStudyHoursPerWeek() != null)
		{
			performance.setStudyHoursPerWeek(dto.getStudyHoursPerWeek());
		}
		
		if(dto.getPreviousGpa() != null)
		{
			performance.setPreviousGpa(dto.getPreviousGpa());
		}
		
		if(dto.getUpdatedBy() != null)
		{
			performance.setUpdatedBy(dto.getUpdatedBy());
		}
		
	}


	private BigDecimal calculateFinalScore(Performance performance) {
	
		double attendance = safe(performance.getAttendancePercentage());
		double quiz = safe(performance.getQuizAverage());
		double assignment = safe(performance.getAssignmentAverage());
		double midterm = safe(performance.getMidtermScore());
		
		
		double weightedScore = 
				(attendance * 0.10) +
				(quiz * 0.20) +
				(assignment * 0.20) +
				(midterm * 0.50);
		
		return BigDecimal.valueOf(weightedScore)
				.setScale(2,RoundingMode.HALF_UP);
	}

	private double safe(BigDecimal value) {
		return value != null ? value.doubleValue() : 0.0;
	}
	private double safe(Integer value) {
	    return value != null ? value.doubleValue() : 0.0;
	}
	
}
