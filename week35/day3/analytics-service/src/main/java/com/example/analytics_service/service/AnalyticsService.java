package com.example.analytics_service.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import com.example.analytics_service.dto.AnalyticsDTO;
import com.example.analytics_service.exception.AnalyticsException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

	private final JdbcTemplate jdbcTemplate;
    	
	public AnalyticsDTO getOverallAnalytics() {
		try {
			AnalyticsDTO dto = new AnalyticsDTO();
			
			
			dto.setTotalStudents(
					getSafeLong("select count(*) from students")
					);
			
//			dto.setTotalStudents(
//					jdbcTemplate.queryForObject(
//							"select count(*) from student_analytics_user.students",
//							Long.class)
//					);
			
		
            dto.setTotalCourses(
            		getSafeLong("select count(*) from courses where is_active = 1")
            		);
            
			dto.setTotalEnrollments(
					getSafeLong("select count(*) from enrollments where is_active = 1")
					);
			
			dto.setAverageAttendance(
					getSafeBigDecimal("select avg(attendance_percentage) from performance")
					);
			
			
			dto.setAverageAssignmentScore(
					getSafeBigDecimal("select avg(score) from exam_result")
					);
			
			dto.setAverageFinalScore(
					getSafeBigDecimal("select avg(final_score) from performance")
					);
			
			dto.setGradeDistribution(getGradeDistribution());
			dto.setRiskDistribution(getRiskDistribution());
			
			
			
			return dto;
		} catch (Exception e) {
            throw new AnalyticsException("Error generating overall analytics: " + e.getMessage());
		}
	}
	
	
	public Map<String, Object> getCourseAnalytics(Long courseId)
	{
		Map<String, Object> result = new HashMap<>();
		
		// 🔥 FIX 1: Renamed from "enrolledStudents" to "totalStudents" to match React
		result.put("totalStudents", 
					getSafeLong("select count(*) from enrollments where course_id = ? and enrollment_status = 'ACTIVE'",
							courseId)
				);
		
	
		result.put("averageAttendance", 
				getSafeBigDecimal("select avg(attendance_percentage) from performance where course_id = ?",
						courseId)
				);
		
		// 🔥 FIX 2: Renamed from "averageFinalScore" to "averageScore" to match React
		result.put("averageScore", 
				getSafeBigDecimal("select avg(final_score) from performance where course_id = ?",
						courseId)
				);
		
		// Kept your original exam score logic intact just in case
		result.put("averageExamScore",
                getSafeBigDecimal(
                        "SELECT AVG(score) FROM exam_result WHERE course_id = ?",
                        courseId));

		// 🔥 FIX 3: Added "highRiskCount" so the red UI card isn't zero
		result.put("highRiskCount",
				getSafeLong("select count(*) from performance where course_id = ? and risk_level = 'HIGH'", 
						courseId)
				);

        result.put("riskDistribution",
                getRiskDistributionByCourse(courseId));
		
		return result;
	}
	
	private Map<String, Long> getRiskDistribution() {
		Map<String, Long> distribution = new HashMap<>();
		
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(
				"select risk_level, count(*) as count from performance group by risk_level"
				);
		
		for(Map<String, Object> row : rows)
		{
			distribution.put(
				(String) row.get("risk_level")
					, ((Number) row.get("count")).longValue());
		}
		
		return distribution;
	}
	

	  private Map<String, Long> getRiskDistributionByCourse(Long courseId) {

	        Map<String, Long> distribution = new HashMap<>();

	        List<Map<String, Object>> rows = jdbcTemplate.queryForList(
	                "SELECT risk_level, COUNT(*) as count FROM performance WHERE course_id = ? GROUP BY risk_level",
	                courseId);

	        for (Map<String, Object> row : rows) {
	            distribution.put(
	                    (String) row.get("risk_level"),
	                    ((Number) row.get("count")).longValue());
	        }

	        return distribution;
	    }

	public Map<String, Object> getStudentAnalytics(Long studentId)
	{
		Map<String, Object> result = new HashMap<>();
		
		result.put("activeEnrollments", 
				getSafeLong("select count(*) from enrollments where student_id = ? and enrollment_status = 'ACTIVE'",
						studentId)
				);
		
		result.put("averageAttendance", 
				getSafeBigDecimal("select avg(attendance_percentage) from performance where student_id = ?",
						studentId)
				);
		
		result.put("averageFinalScore", 
				getSafeBigDecimal("select avg(final_score) from performance where student_id = ?",
						studentId)
				);
		
		result.put("averageExamScore",
                getSafeBigDecimal(
                        "SELECT AVG(score) FROM exam_result WHERE student_id = ?",
                        studentId));

        result.put("latestRiskStatus",
                getLatestRiskStatus(studentId));
		
		return result;
	}
	
	
	private Long getSafeLong(String sql,Object ...params) {
		try {
			Long value = jdbcTemplate.queryForObject(sql, Long.class,params);
			return value != null ? value : 0L;
		} catch (EmptyResultDataAccessException e) {
			return 0L;
		}
	}


	private BigDecimal getSafeBigDecimal(String sql, Object... params) {
        try {
            BigDecimal value = jdbcTemplate.queryForObject(sql, BigDecimal.class, params);
            return value != null ? value : BigDecimal.ZERO;
        } catch (EmptyResultDataAccessException e) {
            return BigDecimal.ZERO;
        }
    }
	
	
	private Map<String, Long> getGradeDistribution(){
		Map<String, Long> distributions = new HashMap<>();
		
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(
			    "SELECT predicted_grade, COUNT(*) as count FROM performance GROUP BY predicted_grade"
			);

		
		for(Map<String, Object> row : rows)
		{
			distributions.put((String)row.get("predicted_grade"),
					((Number) row.get("count")).longValue()
					);
		}
		
		return distributions;
	}
	
	private Map<String, Object> getLatestRiskStatus(Long studentId)
	{
		try {
			return jdbcTemplate.queryForMap(
					"select predicted_grade, risk_level, prediction_confidence " +
					"from performance where student_id = ? "+
					"order by updated_at desc limit 1 ",
					studentId);
			
		} catch (Exception e) {
			return Map.of("message","No performance data unavailable");
		}
		
	}
	
	
}