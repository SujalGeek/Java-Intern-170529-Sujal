package com.example.exam_result_service.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.exam_result_service.dto.ExamResultResponseDTO;
import com.example.exam_result_service.dto.ExamSubmissionDTO;
import com.example.exam_result_service.entity.ExamQuestion;
import com.example.exam_result_service.entity.ExamResult;
import com.example.exam_result_service.exception.ExamResultException;
import com.example.exam_result_service.repository.ExamQuestionRepository;
import com.example.exam_result_service.repository.ExamResultRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
//@RequiredArgsConstructor
public class ExamResultService {

	@Autowired
	private ExamResultRepository examResultRepository;
	
	@Autowired
	private ExamQuestionRepository examQuestionRepository;
	
	@Autowired
	private RestTemplate restTemplate;
	
//	private final String NLP_EVALUATION_URL = "http://localhost:5001/evaluate-answer";
	
	private final String AI_EVALUATION_URL = "http://localhost:8084/api/v1/exams/evaluate";
	
	
	@Transactional
	public ExamResultResponseDTO saveResult(ExamSubmissionDTO dto)
	{	
		try {
			/* Map<String, Object> request = new HashMap<>();
			request.put("question", dto.getQuestion());
			request.put("student_answer", dto.getStudentAnswer());
			request.put("bloom_level", dto.getBloomLevel());
			
			Map<String, Object> response = restTemplate.postForObject(
					AI_EVALUATION_URL, request
					, Map.class);
			if(response == null)
			{
				throw new ExamResultException("Failed to evaluate answer from NLP service");
			}
			
//			String reference_answer = (String) response.get("reference_answer");
//			String feedback = (String) response.get("feedback");
//			
//			BigDecimal score = new BigDecimal(response.get("score").toString());
			
			if (!"success".equals(response.get("status"))) {
			    throw new ExamResultException("AI evaluation failed: " + response);
			}
			Object scoreObj = response.get("score");
			if (scoreObj == null) {
			    throw new ExamResultException("Score missing in AI response");
			}

			BigDecimal score = new BigDecimal(scoreObj.toString());

			String reference_answer = (String) response.get("reference_answer");
			String feedback = (String) response.get("feedback");
			
			
			ExamResult result = ExamResult.builder()
					.studentId(dto.getStudentId())
					.courseId(dto.getCourseId())
					.question(dto.getQuestion())
					.studentAnswer(dto.getStudentAnswer())
					.referenceAnswer(reference_answer)
					.score(score)
					.feedback(feedback)
					.bloomLevel(dto.getBloomLevel())
					.build();
			
			ExamResult saved = examResultRepository.save(result);
			
			return ExamResultResponseDTO.builder()
					.examResultId(saved.getExamResultId())
					.studentId(saved.getStudentId())
					.courseId(saved.getCourseId())
					.question(saved.getQuestion())
					.studentAnswer(saved.getStudentAnswer())
					.referenceAnswer(saved.getReferenceAnswer())
					.score(saved.getScore())
					.feedback(saved.getFeedback())
					.bloomLevel(saved.getBloomLevel())
					.build(); */
			
			    // 1️⃣ Fetch question
			    ExamQuestion question = examQuestionRepository.findById(dto.getExamQuestionId())
			            .orElseThrow(() -> new ExamResultException("Question not found"));

			    // 2️⃣ Prepare evaluation request
			    Map<String, Object> request = new HashMap<>();
			    request.put("student_answer", dto.getStudentAnswer());
			    request.put("reference_answer", question.getReferenceAnswer());
			    request.put("bloom_level", question.getBloomLevel());

			    // 3️⃣ Call AI Integration
			    ResponseEntity<Map> response = restTemplate.postForEntity(
			            AI_EVALUATION_URL,
			            request,
			            Map.class
			    );

			    Map<String, Object> body = response.getBody();

			    if (body == null || !"success".equals(body.get("status"))) {
			        throw new ExamResultException("Evaluation failed");
			    }

			    BigDecimal score = new BigDecimal(body.get("score").toString());
			    String feedback = body.get("feedback").toString();

			    // 4️ Save result
//			    ExamResult result = ExamResult.builder()
//			            .studentId(dto.getStudentId())
//			            .courseId(dto.getCourseId())
//			            .examQuestionId(question.getExamQuestionId())
//			            .studentAnswer(dto.getStudentAnswer())
//			            .score(score)
//			            .feedback(feedback)
//			            .build();
			    
			    ExamResult result = ExamResult.builder()
			            .studentId(dto.getStudentId())
			            .courseId(dto.getCourseId())
			            .examQuestionId(question.getExamQuestionId())
//			            .question(question)
//			            .referenceAnswer(question.getReferenceAnswer())
			            .bloomLevel(question.getBloomLevel())
			            .studentAnswer(dto.getStudentAnswer())
			            .score(score)
			            .feedback(feedback)
			            .build();

			    ExamResult saved = examResultRepository.save(result);

			    return ExamResultResponseDTO.builder()
			            .examResultId(saved.getExamResultId())
			            .studentId(saved.getStudentId())
			            .courseId(saved.getCourseId())
			            .question(question.getQuestion())                 // from exam_question
			            .studentAnswer(saved.getStudentAnswer())          // from exam_result
			            .referenceAnswer(question.getReferenceAnswer())   // from exam_question
			            .score(saved.getScore())
			            .feedback(saved.getFeedback())
			            .bloomLevel(question.getBloomLevel())             // from exam_question
			            .build();

		} catch (Exception e) {
            throw new ExamResultException("Error saving exam result: " + e.getMessage());
		}
	
	}
	
	public List<ExamResult> getByStudent(Long studentId)
	{
		List<ExamResult> results = examResultRepository.findByStudentId(studentId);
		
		if(results.isEmpty())
		{
			throw new ExamResultException("No Exam Results found for student: "+studentId);
		}
		return results;
	}
	
	public List<ExamResult> getByCourse(Long courseId)
	{
		List<ExamResult> results = examResultRepository.findByCourseId(courseId);
		
		if(results.isEmpty())
		{
			throw new ExamResultException("No exam results found for courses: "+ courseId);
		}
		return results;
	}
	
	public List<ExamResult> getByStudentAndCourse(Long studentId,Long courseId)
	{
		List<ExamResult> results = examResultRepository.findByStudentIdAndCourseId(studentId, courseId);
		
		if(results.isEmpty())
		{
			throw new ExamResultException("No results found for student and course");
		}
		return results;
		
	}
	
	
	
}
