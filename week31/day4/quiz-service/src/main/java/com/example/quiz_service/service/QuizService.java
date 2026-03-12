package com.example.quiz_service.service;

import java.math.BigDecimal; 
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import com.example.quiz_service.config.RestTemplateConfig;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.quiz_service.entity.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final RestTemplateConfig restTemplateConfig;

	private final QuizRepository quizRepository;
	
	private final QuizQuestionRepository quizQuestionRepository;
	
	private final QuizAttemptRepository quizAttemptRepository;
	
	private final EnrollmentRepository enrollmentRepository;
	
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	@Autowired // This overrides Lombok for this specific field
    @Qualifier("externalRestTemplate")
    private RestTemplate restTemplate; // Remove 'final' here!
	
    @Value("${nlp.service.url}")
    private String nlpServiceUrl;

    

	public String nlpServiceUrl() {
		return nlpServiceUrl + "/generate-quiz";
	}
	
//	private static final String NLP_SERVICE_URL = "http://localhost:5001/generate-quiz";
	
	@Transactional
	public Quiz generateQuiz(Long courseId, String description) {
	    // --- 1. PREPARE THE HANDSHAKE PAYLOAD ---
	    Map<String, Object> request = new HashMap<>();
	    request.put("course_id", courseId);
	    request.put("description", description);
	    
	    System.out.println(" Initializing AI Synchronizer for Course: " + courseId);

	    try {
	        // --- 2. EXECUTE EXTERNAL CALL ---
	        // 🔥 Make sure your RestTemplate uses @Qualifier("externalRestTemplate")
	        ResponseEntity<Map> response = restTemplate.postForEntity(
	            nlpServiceUrl(), 
	            request, 
	            Map.class
	        );

	        // This is the 'root' JSON as seen in your Python console logs
	        Map<String, Object> quizData = response.getBody();
	        
	        // --- 3. DEFENSIVE VALIDATION (Flat Structure Handshake) ---
	        // Your log shows 'generated_questions' is the primary key at the root
	        if (quizData == null || !quizData.containsKey("generated_questions")) {
	            System.err.println("CRITICAL: Python NLP response missing 'generated_questions'. Payload: " + quizData);
	            throw new RuntimeException("NLP Service failed to generate valid quiz data.");
	        }

	        // --- 4. PERSIST THE QUIZ MASTER RECORD ---
	        // Pull 'question_count' from the Python response
	        Object rawCount = quizData.get("question_count");
	        Integer totalQuestions = (rawCount instanceof Integer) ? (Integer) rawCount : 10;

	        Quiz quiz = new Quiz();
	        quiz.setCourseId(courseId);
	        quiz.setTotalMarks(totalQuestions); // Assuming 1 mark per question for basic MCQs
	        quiz.setCreatedAt(LocalDateTime.now());
	        
	        final Quiz savedQuiz = quizRepository.save(quiz);
	        System.out.println(" Quiz Master Sequence persisted with ID: " + savedQuiz.getQuizId());

	        // --- 5. PERSIST THE SEMANTIC QUESTION BANK ---
	        // Extract the array using the exact key: 'generated_questions'
	     // --- 5. PERSIST THE SEMANTIC QUESTION BANK ---
	        List<Map<String, Object>> questions = (List<Map<String, Object>>) quizData.get("generated_questions"); 
	        
	        if (questions != null && !questions.isEmpty()) {
	            for (Map<String, Object> q : questions) {
	                QuizQuestion question = new QuizQuestion();
	                
	                question.setQuizId(savedQuiz.getQuizId());
	                question.setQuestionText((String) q.get("question"));
	                question.setCorrectAnswer((String) q.get("correct_answer"));
	                
	                Object qMarks = q.get("marks");
	                question.setMarks((qMarks instanceof Integer) ? (Integer) qMarks : 1);
	                
	                String bLevel = (String) q.get("bloom_level");
	                question.setBloomLevel(bLevel != null ? bLevel.toUpperCase() : "UNDERSTAND");
	                
	                // 🔥 NEW LOGIC: Save the options safely to the database!
	                List<String> optionsList = (List<String>) q.get("options");
	                if (optionsList != null) {
	                    question.setOptionsJson(objectMapper.writeValueAsString(optionsList));
	                }
	                
	                quizQuestionRepository.save(question);
	            }
	            System.out.println(" Success: Synchronized " + questions.size() + " questions into the Knowledge Base.");
	        }

	        return savedQuiz;

	    } catch (Exception e) {
	        System.err.println(" Quiz Orchestration Failure: " + e.getMessage());
	        throw new RuntimeException("Failed to synchronize with Hybrid NLP Engine: " + e.getMessage());
	    }
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
	
//	public QuizResponse getQuizById(Long quizId)
//	{
//		Quiz quiz = quizRepository.findById(quizId)
//				.orElseThrow( () -> new RuntimeException("Quiz not found"));
//		
//		List<QuizQuestion> questions = quizQuestionRepository.findByQuizId(quizId);
//		
//		QuizResponse response = new QuizResponse();
//		response.setQuizId(quiz.getQuizId());
//		response.setCourseId(quiz.getCourseId());
//		response.setTotalMarks(quiz.getTotalMarks());
//		
//		List<QuestionResponse> questionResponses = questions.stream().map(
//				q-> {
//					QuestionResponse qr = new QuestionResponse();
//					qr.setQuestionId(q.getQuestionId());
//					qr.setQuestionText(q.getQuestionText());
//					qr.setMarks(q.getMarks());
//					qr.setBloomLevel(q.getBloomLevel());
//			
//					return qr;
//				}).toList();
//	
//		response.setQuestions(questionResponses);
//		
//		return response;
//	}
	
	public QuizResponse getQuizById(Long quizId) {
		Quiz quiz = quizRepository.findById(quizId)
				.orElseThrow( () -> new RuntimeException("Quiz not found"));
		
		List<QuizQuestion> questions = quizQuestionRepository.findByQuizId(quizId);
		
		QuizResponse response = new QuizResponse();
		response.setQuizId(quiz.getQuizId());
		response.setCourseId(quiz.getCourseId());
		response.setTotalMarks(quiz.getTotalMarks());
		
		List<QuestionResponse> questionResponses = questions.stream().map(
				q -> {
					QuestionResponse qr = new QuestionResponse();
					qr.setQuestionId(q.getQuestionId());
					qr.setQuestionText(q.getQuestionText());
					qr.setMarks(q.getMarks());
					qr.setBloomLevel(q.getBloomLevel());
					
					// 🔥 NEW LOGIC: Read the options from the database and give them to React!
					List<String> optionsList = null;
					if (q.getOptionsJson() != null) {
					    try {
					        optionsList = objectMapper.readValue(
					            q.getOptionsJson(), 
					            new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {}
					        );
					    } catch (Exception e) {
					        System.err.println("Failed to parse options JSON");
					    }
					}
					qr.setOptions(optionsList);
			
					return qr;
				}).toList();
	
		response.setQuestions(questionResponses);
		
		return response;
	}

	// 🔥 NEW: Method to support the dashboard view
    public List<Map<String, Object>> getQuizzesByCourse(Long courseId) {
        // You might need to add findByCourseIdOrderByQuizIdDesc to QuizRepository
        List<Quiz> quizzes = quizRepository.findByCourseId(courseId);

        if (quizzes.isEmpty()) {
            throw new RuntimeException("No quizzes found for course: " + courseId);
        }

        return quizzes.stream().map(quiz -> {
            List<QuizQuestion> questions = quizQuestionRepository.findByQuizId(quiz.getQuizId());
            
            Map<String, Object> map = new HashMap<>();
            map.put("quizId", quiz.getQuizId());
            map.put("courseId", quiz.getCourseId());
            map.put("totalMarks", quiz.getTotalMarks());
            map.put("totalQuestions", questions.size()); // React needs this for the UI
            return map;
        }).toList();
    }
	
    @Transactional
    public void publishQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz not found"));
        quiz.setIsPublished(true);
        quizRepository.save(quiz);
        System.out.println("✅ Quiz " + quizId + " has been approved and published by the Teacher.");
    }
}
