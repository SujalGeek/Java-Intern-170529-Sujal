package com.example.studentsmartmanagement.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.studentsmartmanagement.entity.Submission;
import com.example.studentsmartmanagement.repository.SubmissionRepository;
import com.example.studentsmartmanagement.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService{
	
	private final SubmissionRepository submissionRepository;
	
	private final String PYTHON_PREDICT_URL = "http://localhost:5000/predict-score";
	
	@Override
	public Map<String, Object> getStudentRiskAnalysis(Long studentId) {
		
		// fetch the student's history from the database
		List<Submission> history = submissionRepository.findByStudent_StudentId(studentId);
		
		// 2. Extract Scores (only from GRADED exams)
        // We filter for "GRADED" to avoid including pending exams with 0 marks
		List<Double> scores = history.stream()
				.filter(s->"GRADED".equals(s.getStatus()))
				.map(Submission::getGradeObtained)
				.collect(Collectors.toList());
		
		
		// 3. Prepare Payload for Python
		RestTemplate restTemplate = new RestTemplate();
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("scores", scores);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map<String,Object>> requestEntity = new HttpEntity<>(requestBody,headers);
		
		try {
			Map<String, Object> pythonResponse = restTemplate.postForObject(PYTHON_PREDICT_URL, requestEntity, Map.class);
			
			if(pythonResponse != null)
			{
				pythonResponse.put("exam_analyzed", scores.size());
				pythonResponse.put("student_id", studentId);
			}
			
			return pythonResponse;
		}catch(Exception e) {
			
			Map<String, Object> err = new HashMap<>();
			err.put("success", false);
			err.put("message", "AI Analytics Service Unavailable: "+ e.getMessage());
			return err;
		}
	}

}
