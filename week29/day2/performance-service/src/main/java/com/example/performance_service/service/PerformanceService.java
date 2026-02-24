package com.example.performance_service.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.performance_service.dto.PerformanceDTO;
import com.example.performance_service.entity.Performance;
import com.example.performance_service.repository.PerformanceRepository;
import com.example.performance_service.util.GradeUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PerformanceService {

	private final PerformanceRepository performanceRepository;
	
	@Transactional
	public Performance upsertPerformance(PerformanceDTO dto)
	{
		Optional<Performance> existing = performanceRepository.findByStudentIdAndCourseId(
				dto.getStudentId(), dto.getCourseId());
		
		Performance performance = existing.orElse(new Performance());
		
		performance.setStudentId(dto.getStudentId());
		performance.setCourseId(dto.getCourseId());
		
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
		
		BigDecimal finalScore = calculateFinalScore(performance);
		performance.setFinalScore(finalScore);
		
		String grade = GradeUtil.calculateGrade(finalScore.doubleValue());
		performance.setFinalGrade(grade);
		
		return performanceRepository.save(performance);
		
				
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
	
}
