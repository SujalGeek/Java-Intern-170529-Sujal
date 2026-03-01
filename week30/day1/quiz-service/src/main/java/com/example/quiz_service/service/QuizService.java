package com.example.quiz_service.service;

import java.math.BigDecimal; 
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.quiz_service.dto.QuestionResponse;
import com.example.quiz_service.dto.QuizResponse;
import com.example.quiz_service.dto.SubmitQuizRequest;
import com.example.quiz_service.entity.Quiz;
import com.example.quiz_service.entity.QuizAttempt;
import com.example.quiz_service.entity.QuizQuestion;
import com.example.quiz_service.repository.EnrollmentRepository;
import com.example.quiz_service.repository.QuizAttemptRepository;
import com.example.quiz_service.repository.QuizQuestionRepository;
import com.example.quiz_service.repository.QuizRepository;
import com.example.quiz_service.entity.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizService {

	private final QuizRepository quizRepository;
	
	private final QuizQuestionRepository quizQuestionRepository;
	
	private final QuizAttemptRepository quizAttemptRepository;
	
	private final EnrollmentRepository enrollmentRepository;
	
	private final RestTemplate restTemplate;
	
	@Value("${nlp.service.url}")
	private String nlpServiceUrl;
	
	public String nlpServiceUrl() {
		return nlpServiceUrl + "/generate-quiz";
	}
	
//	private static final String NLP_SERVICE_URL = "http://localhost:5001/generate-quiz";
	
	public Quiz generateQuiz(Long courseId,String description)
	{
		Map<String, String> request = new HashMap<>();
		request.put("description", description);
		
		ResponseEntity<Map> response = restTemplate.postForEntity(nlpServiceUrl(),
				request, Map.class);
		
		Map quizData = (Map) response.getBody().get("quiz");
				
		Integer totalMarks = (Integer) quizData.get("total_marks");
		
		Quiz quiz = new Quiz();
		quiz.setCourseId(courseId);
		quiz.setTotalMarks(totalMarks);
		
		quiz = quizRepository.save(quiz);
		
		List<Map<String, Object>> questions = 
				(List<Map<String,Object>>)
				quizData.get("questions"); 
		
		for(Map<String, Object> q : questions)
		{
			QuizQuestion question = new QuizQuestion();
			
			question.setQuizId(quiz.getQuizId());
			question.setQuestionText((String) q.get("question"));
			question.setCorrectAnswer((String) q.get("correct_answer"));
			question.setMarks((Integer) q.get("marks"));
			question.setBloomLevel((String) q.get("bloom_level"));
			
			quizQuestionRepository.save(question);
			
		}
		
		return quiz;
	}
	
	public QuizAttempt submitQuiz(Long studentId, SubmitQuizRequest request) {

	    Quiz quiz = quizRepository.findById(request.getQuizId())
	            .orElseThrow(() -> new RuntimeException("Quiz not found"));

	    Long courseId = quiz.getCourseId();

	    // 🔥 Enrollment Validation
	    boolean enrolled = enrollmentRepository
	            .existsByStudentIdAndCourseIdAndStatus(
	                    studentId,
	                    courseId,
	                    Enrollment.Status.ACTIVE
	            );

	    if (!enrolled) {
	        throw new RuntimeException("Student not enrolled in this course");
	    }

	    // Prevent re-attempt
	    if (quizAttemptRepository.existsByQuizIdAndStudentId(
	            request.getQuizId(), studentId)) {
	        throw new RuntimeException("Quiz already submitted");
	    }

	    List<QuizQuestion> questions =
	            quizQuestionRepository.findByQuizId(request.getQuizId());

	    if (questions.isEmpty()) {
	        throw new RuntimeException("Quiz has no questions");
	    }

	    BigDecimal totalScore = BigDecimal.ZERO;

	    for (QuizQuestion question : questions) {

	        String studentAnswer =
	                request.getAnswers().get(question.getQuestionId());

	        if (studentAnswer != null &&
	                question.getCorrectAnswer()
	                        .equalsIgnoreCase(studentAnswer)) {

	            totalScore = totalScore.add(
	                    BigDecimal.valueOf(question.getMarks()));
	        }
	    }

	    BigDecimal totalMarks =
	            questions.stream()
	                    .map(q -> BigDecimal.valueOf(q.getMarks()))
	                    .reduce(BigDecimal.ZERO, BigDecimal::add);

	    BigDecimal percentage =
	            totalScore.divide(totalMarks, 2, RoundingMode.HALF_UP)
	                    .multiply(BigDecimal.valueOf(100));

	    String grade = calculateGrade(percentage);

	    QuizAttempt attempt = new QuizAttempt();
	    attempt.setQuizId(request.getQuizId());
	    attempt.setStudentId(studentId);
	    attempt.setTotalScore(totalScore);
	    attempt.setPercentage(percentage);
	    attempt.setGrade(grade);

	    return quizAttemptRepository.save(attempt);
	}
	private String calculateGrade(BigDecimal percentage) {
		  if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) return "A+";
		    if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) return "A";
		    if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) return "B";
		    if (percentage.compareTo(BigDecimal.valueOf(60)) >= 0) return "C";
		    if (percentage.compareTo(BigDecimal.valueOf(50)) >= 0) return "D";
		    return "F";
	}
	
	public QuizResponse getQuizById(Long quizId)
	{
		Quiz quiz = quizRepository.findById(quizId)
				.orElseThrow( () -> new RuntimeException("Quiz not found"));
		
		List<QuizQuestion> questions = quizQuestionRepository.findByQuizId(quizId);
		
		QuizResponse response = new QuizResponse();
		response.setQuizId(quiz.getQuizId());
		response.setCourseId(quiz.getCourseId());
		response.setTotalMarks(quiz.getTotalMarks());
		
		List<QuestionResponse> questionResponses = questions.stream().map(
				q-> {
					QuestionResponse qr = new QuestionResponse();
					qr.setQuestionId(q.getQuestionId());
					qr.setQuestionText(q.getQuestionText());
					qr.setMarks(q.getMarks());
					qr.setBloomLevel(q.getBloomLevel());
			
					return qr;
				}).toList();
	
		response.setQuestions(questionResponses);
		
		return response;
	}
	
}
