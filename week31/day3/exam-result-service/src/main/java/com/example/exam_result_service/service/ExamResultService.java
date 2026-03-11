package com.example.exam_result_service.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.exam_result_service.config.AiEvaluationClient;
import com.example.exam_result_service.dto.ExamResultResponseDTO;
import com.example.exam_result_service.dto.ExamResultSimpleDTO;
import com.example.exam_result_service.dto.ExamSubmissionDTO;
import com.example.exam_result_service.entity.ExamQuestion;
import com.example.exam_result_service.entity.ExamResult;
import com.example.exam_result_service.exception.ExamResultException;
import com.example.exam_result_service.repository.ExamQuestionRepository;
import com.example.exam_result_service.repository.ExamResultRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamResultService {

//	@Autowired
	private final  ExamResultRepository examResultRepository;
	
//	@Autowired
	private final ExamQuestionRepository examQuestionRepository;
	
//	@Autowired // This overrides Lombok for this specific field
    @Qualifier("externalRestTemplate")
    private RestTemplate restTemplate; // Remove 'final' here!
    
//    @Value("${nlp.service.url}")
//    private String nlpServiceUrl;

	
	private final AiEvaluationClient aiEvaluationClient;

	
//	private final String NLP_EVALUATION_URL = "http://localhost:5001/evaluate-answer";
//	@Value("${ai.evaluation.url}")
//	private String aiEvaluationUrl;
	
	//private final String AI_EVALUATION_URL = "http://localhost:8084/api/v1/exams/evaluate";
	
	
	@Transactional
	public ExamResultResponseDTO saveResult(Long studentId,ExamSubmissionDTO dto)
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
//			    ResponseEntity<Map> response = restTemplate.postForEntity(
//			    		aiEvaluationUrl,
//			            request,
//			            Map.class
//			    );

			  Map<String, Object> response = aiEvaluationClient.evaluate(request);
			    
			    
			  if (response == null || !"success".equals(response.get("status"))) {
		            throw new ExamResultException("Evaluation failed or service returned invalid status");
		        }

			  BigDecimal score = new BigDecimal(response.get("score").toString());
		        String feedback = response.get("feedback") != null ? response.get("feedback").toString() : "";
		        

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
		                .studentId(studentId)
		                .courseId(dto.getCourseId())
		                .examQuestionId(question.getExamQuestionId())
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
		                .question(question.getQuestion())
		                .studentAnswer(saved.getStudentAnswer())
		                .referenceAnswer(question.getReferenceAnswer())
		                .score(saved.getScore())
		                .feedback(saved.getFeedback())
		                .bloomLevel(question.getBloomLevel())
		                .build();

		} catch (Exception e) {
            throw new ExamResultException("Error saving exam result: " + e.getMessage());
		}
	
	}
	
	public List<ExamResultSimpleDTO> getByStudent(Long studentId)
	{
		List<ExamResult> results = examResultRepository.findByStudentId(studentId);
		
		if(results.isEmpty())
		{
			throw new ExamResultException("No Exam Results found for student: "+studentId);
		}
		return results.stream()
	            .map(result -> ExamResultSimpleDTO.builder()
	                    .examResultId(result.getExamResultId())
	                    .studentId(result.getStudentId())
	                    .courseId(result.getCourseId())
	                    .examQuestionId(result.getExamQuestionId())
	                    .score(result.getScore())
	                    .feedback(result.getFeedback())
	                    .bloomLevel(result.getBloomLevel())
	                    .build())
	            .toList();
	}
	
	public List<ExamResultSimpleDTO> getByCourse(Long courseId)
	{
		List<ExamResult> results = examResultRepository.findByCourseId(courseId);
		
		if(results.isEmpty())
		{
			throw new ExamResultException("No exam results found for courses: "+ courseId);
		}
		return results.stream()
	            .map(result -> ExamResultSimpleDTO.builder()
	                    .examResultId(result.getExamResultId())
	                    .studentId(result.getStudentId())
	                    .courseId(result.getCourseId())
	                    .examQuestionId(result.getExamQuestionId())
	                    .score(result.getScore())
	                    .feedback(result.getFeedback())
	                    .bloomLevel(result.getBloomLevel())
	                    .build())
	            .toList();
	}
	
	public List<ExamResultSimpleDTO> getByStudentAndCourse(Long studentId,Long courseId)
	{
		List<ExamResult> results = examResultRepository.findByStudentIdAndCourseId(studentId, courseId);
		
		if(results.isEmpty())
		{
			throw new ExamResultException("No results found for student and course");
		}
		return results.stream()
	            .map(result -> ExamResultSimpleDTO.builder()
	                    .examResultId(result.getExamResultId())
	                    .studentId(result.getStudentId())
	                    .courseId(result.getCourseId())
	                    .examQuestionId(result.getExamQuestionId())
	                    .score(result.getScore())
	                    .feedback(result.getFeedback())
	                    .bloomLevel(result.getBloomLevel())
	                    .build())
	            .toList();
		
	}
	
	
}
