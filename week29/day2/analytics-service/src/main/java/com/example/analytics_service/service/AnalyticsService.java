package com.example.analytics_service.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
					jdbcTemplate.queryForObject(
							"select count(*) from student_analytics_user.students",
							Long.class)
					);
			
			dto.setTotalCourses(
                    jdbcTemplate.queryForObject(
                            "SELECT COUNT(*) FROM courses WHERE is_active = 1",
                            Long.class
                    )
            );
			dto.setTotalEnrollments(
					
					jdbcTemplate.queryForObject(
							"SELECT COUNT(*) FROM enrollments WHERE enrollment_status = 'ACTIVE'"
							, Long.class)
					);
			
			
			dto.setAverageAttendance(
					getSafeBigDecimal("SELECT AVG(attendance_percentage) FROM performance")
					);

			dto.setAverageExamScore(
					getSafeBigDecimal("select avg(score) from exam_result")
					);
			
			dto.setAverageAssignmentScore(
					getSafeBigDecimal("SELECT AVG(grade_obtained) FROM submissions WHERE status = 'GRADED'")
					);
			
			dto.setRiskDistribution(getRiskDistribution());
			dto.setGradeDistribution(getGradeDistribution());
			
			return dto;
		} catch (Exception e) {
            throw new AnalyticsException("Error generating overall analytics: " + e.getMessage());
		}
	}
	
	
	public Map<String, Object> getCourseAnalytics(Long courseId)
	{
		Map<String, Object> result = new HashMap<>();
		
		result.put("enrolledStudents", 
				jdbcTemplate.queryForObject(
						"select count(*) from enrollments where course_id = ? and enrollment_status = 'ACTIVE'"
						, Long.class ,
						courseId)
				);
	
		result.put("averageAttendance", 
				getSafeBigDecimal(
						"select avg(attendance_percentage) from performance where course_id = ?" 
						, courseId)
				);
		
		result.put("averageAssignmentScore", 
				getSafeBigDecimal(
					"select avg(s.grade_obtained) "+
					"from submissions s " +
					"join assignments a on s.assignment_id = a.assignment_id "+
					"where a.course_id = ? and s.status = 'GRADED'")
				);
		
		result.put("averageExamScore", 
				getSafeBigDecimal("select avg(score) from exam_result where course_id = ?",
						courseId
						)
				);
		
		result.put("latestPrediction", 
				jdbcTemplate.queryForMap(
						   "SELECT predicted_grade, risk_level, confidence_score " +
							        "FROM predictions WHERE course_id = ? " +
							        "ORDER BY predication_date DESC LIMIT 1",courseId)
				);
		
		return result;
	}
	
	public Map<String, Object> getStudentAnalytics(Long studentId)
	{
		Map<String, Object> result = new HashMap<>();
		
		result.put("enrolledStudents", 
				jdbcTemplate.queryForObject(
						"select count(*) from enrollments where student_id = ? and enrollment_status = 'ACTIVE'",
						Long.class , studentId)
				);
		
		result.put("averageAttendance", 
				getSafeBigDecimal("select avg(attendance_percentage) from performance where student_id = ?",
						studentId
						)
				);
		
		result.put("averageExamScore", 
				getSafeBigDecimal("select avg(score) from exam_result where student_id = ?",
						studentId)
				);
		
		result.put("averageAssignmentScore",
				getSafeBigDecimal("select avg(grade_obtained) from submissions where student_id = ? and status = 'GRADED'",
				studentId)
				);
		
		result.put("latestPrediction", 
				jdbcTemplate.queryForMap("select predicted_grade, risk_level, confidence_score " +
						"from predictions where student_id = ? "+
						"order by predication_date desc limit 1",
						studentId)
				);
		
		return result;
	}
	
	private BigDecimal getSafeBigDecimal(String sql, Object ...params)
	{
	BigDecimal value = jdbcTemplate.queryForObject(sql, BigDecimal.class,params);
	
	return value == null ? BigDecimal.ZERO : value;
		
	}
	
	
	private Map<String, Long> getRiskDistribution(){
		Map<String, Long> distributions = new HashMap<>();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(
				"select risk_level, count(*) as count from predictions group by risk_level");

		for(Map<String, Object> row: rows)
		{
			distributions.put((String) row.get("risk_level"), ((Number) row.get("count")).longValue());
		}
		
		return distributions;
	}
	
	private Map<String, Long> getGradeDistribution(){
		Map<String, Long> distributions = new HashMap<>();
		
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(
			    "SELECT predicted_grade, COUNT(*) as count FROM predictions GROUP BY predicted_grade"
			);

		
		for(Map<String, Object> row : rows)
		{
			distributions.put((String)row.get("predicted_grade"),
					((Number) row.get("count")).longValue()
					);
		}
		
		return distributions;
	}
	
	
}
