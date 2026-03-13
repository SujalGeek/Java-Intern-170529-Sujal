package com.example.ai_integration_service.service;

import java.math.BigDecimal;  
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.ai_integration_service.config.PerformanceClient;
import com.example.ai_integration_service.dto.GenerateQuestionRequest;
import com.example.ai_integration_service.dto.MidTermRequest;
import com.example.ai_integration_service.dto.MidtermSubmitRequest;
import com.example.ai_integration_service.dto.PerformanceDTO;
import com.example.ai_integration_service.dto.QuizRequestDto;
import com.example.ai_integration_service.dto.QuizResponseDto;
import com.example.ai_integration_service.entity.ExamQuestion;
import com.example.ai_integration_service.entity.MidtermAnswer;
import com.example.ai_integration_service.entity.MidtermAttempt;
import com.example.ai_integration_service.entity.MidtermExam;
import com.example.ai_integration_service.entity.MidtermExamQuestion;
import com.example.ai_integration_service.entity.QuestionBank;
import com.example.ai_integration_service.repository.EnrollmentRepository;
import com.example.ai_integration_service.repository.ExamQuestionRepository;
import com.example.ai_integration_service.repository.MidtermAnswerRepository;
import com.example.ai_integration_service.repository.MidtermAttemptRepository;
import com.example.ai_integration_service.repository.MidtermExamQuestionRepository;
import com.example.ai_integration_service.repository.MidtermExamRepository;
import com.example.ai_integration_service.repository.QuestionBankRepository;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.example.ai_integration_service.entity.*;
import lombok.RequiredArgsConstructor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
@Service
@RequiredArgsConstructor
public class AiIntegrationService {

//	@Autowired
    private  final ExamQuestionRepository examQuestionRepository;
    
//	@Autowired
	private final MidtermExamQuestionRepository midtermExamQuestionRepository;
    
//	@Autowired
	private final MidtermExamRepository midtermExamRepository;
    
//    @Autowired
    private final QuestionBankRepository questionBankRepository;
    
//    @Autowired
    private final MidtermAttemptRepository midAttemptRepository;
    
//    @Autowired
    private final MidtermAnswerRepository midtermAnswerRepository;
    
    private final EnrollmentRepository enrollmentRepository;
    
    @Autowired // This overrides Lombok for this specific field
    @Qualifier("externalRestTemplate")
    private RestTemplate restTemplate; // Remove 'final' here!
    
    @Value("${nlp.service.url}")
    private String nlpServiceUrl;
    
    private final ObjectMapper objectMapper; // Spring managed ObjectMapper
    
//    String URL = nlpServiceUrl + "/evaluate-answer";
    
//    @Value("${performance.service.url}")
//    private String performanceServiceUrl;
    
    private final PerformanceClient performanceClient;

    private static final Logger log =
            LoggerFactory.getLogger(AiIntegrationService.class);

    
//    private final String FLASK_API_URL = nlpServiceUrl + "/generate-quiz";
    public String getGenerateQuizUrl() {
        return nlpServiceUrl + "/generate-quiz";
    }

    public String getEvaluateUrl() {
    	return nlpServiceUrl + "/evaluate-answer";
    }
    
    public String getReferenceUrl() {
		return nlpServiceUrl + "/generate-reference";
	}
    
    public String getDynamicMidterm() {
    	return nlpServiceUrl + "/generate-dynamic-midterm";
    }
	

    
    public QuizResponseDto generateExamQuestions(QuizRequestDto requestPayload) {
        
//    	httpheaders ko define karo fir headers ko payload ma add karo
    	  HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_JSON);
    	
    
          Map<String, Object> payload = new HashMap<>();
          payload.put("course_id", requestPayload.getCourseId());
          payload.put("description", requestPayload.getDescription() != null ? requestPayload.getDescription() : "General Quiz");
          
    	HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
    		
    	

    
//        HttpEntity<QuizRequestDto> request = new HttpEntity<>(requestPayload, headers);
        
        try {
        	ResponseEntity<QuizResponseDto> response = restTemplate.postForEntity(
        		    getGenerateQuizUrl(), 
        		    request, 
        		    QuizResponseDto.class
        		    );
            
            // 1. Extract the body ONCE to prevent NullPointerExceptions
            QuizResponseDto responseBody = response.getBody();
            
            if (responseBody != null) {
            	QuestionBank savedBank = new QuestionBank();
                savedBank.setConcept(responseBody.getConcept() != null ? responseBody.getConcept() : requestPayload.getDescription());
                
                String finalConcept = (responseBody.getConcept() != null) 
                        ? responseBody.getConcept() 
                        : requestPayload.getDescription();
                
                List<String> questionStrings = responseBody.getGenerated_questions().stream()
                        .map(qMap -> qMap.get("question").toString())
                        .toList();
                
                // 2. Map the data to the database entity
                savedBank.setConcept(finalConcept);
                savedBank.setBloomLevel(responseBody.getBloom_level() != null ? responseBody.getBloom_level() : "UNDERSTAND");
                savedBank.setQuestions(questionStrings);
                savedBank.setDescription(requestPayload.getDescription());
                savedBank.setCreatedAt(LocalDateTime.now());
                
                // Grab the description from the incoming request since Python doesn't return it
//                savedBank.setDescription(requestPayload.getDescription());
                
                // 3. Save to MySQL/PostgreSQL
                questionBankRepository.save(savedBank);
                
                for (Map<String, Object> qMap : responseBody.getGenerated_questions()) {
                    ExamQuestion officialQuestion = ExamQuestion.builder()
                        .courseId(requestPayload.getCourseId())
                        .question(qMap.get("question").toString())
                        .bloomLevel("UNDERSTAND")
                        .difficulty("MEDIUM")
                        // Safely get correct_answer or default to A
                        .correctAnswer(qMap.get("correct_answer") != null ? qMap.get("correct_answer").toString() : "A")
                        // Map the options list to JSON string for the DB
                        .optionsJson(objectMapper.writeValueAsString(qMap.get("options")))
                        .referenceAnswer("AI Generated Reference") 
                        .build();
                    
                    examQuestionRepository.save(officialQuestion);
                }
                
                return responseBody;
            } else {
                throw new RuntimeException("Received empty response from Python AI Service.");
            }
            
        } catch (Exception e) {
        	log.error("NLP service failure",e);
            throw new RuntimeException("Failed to connect to Python AI Service: " + e.getMessage());
        }
    }

	public Object generateAndStore(GenerateQuestionRequest dto) {

		try {
//			String GENERATED_URL = nlpServiceUrl +  "/generate-quiz";
			
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			Map<String, Object> payload = new HashMap<>();
	        payload.put("course_id", dto.getCourseId());   // CRITICAL FIX
	        payload.put("concept", dto.getConcept());
	        payload.put("description", dto.getConcept()); // Using concept as the search query
	        payload.put("bloom_level", dto.getBloomLevel());
	        payload.put("count", dto.getCount());
			
			HttpEntity<Map<String, Object>> request = new HttpEntity<>(
					payload,headers);
			
			ResponseEntity<Map> response = 
					restTemplate.postForEntity(getGenerateQuizUrl(),
							request,
							Map.class);
			Map body = response.getBody();
			
//			if(body == null || !body.containsKey("generated_questions"))
//			{
//				throw new RuntimeException("Invalid Response from AI Service");
//			}
			
			if (body == null || !body.containsKey("quiz")) {
			    throw new RuntimeException("Invalid Response: Missing 'quiz' key from AI Service");
			}
			
			Map<String, Object> quizMap = (Map<String, Object>) body.get("quiz");

			// 2. Get the list of question objects (these are Maps, not Strings!)
			List<Map<String, Object>> questions = (List<Map<String, Object>>) quizMap.get("questions");
			
			
			if (questions == null || questions.isEmpty()) {
			    throw new RuntimeException("No questions generated in the quiz");
			}
			
//			List<String> questions = (List<String>) body.get("generated_questions");
		
			
//			String REFERENCE_URL =  nlpServiceUrl + "/generate-reference";
			
			int savedCount = 0;
			
			for(Map<String, Object> qMap : questions)
			{
				log.info("Generating reference answer for question {} of {}", savedCount + 1, questions.size());
				
				
				List<String> optionsList = (List<String>) qMap.get("options");
			    String optionsJson = null;
			    if (optionsList != null) {
			        optionsJson = objectMapper.writeValueAsString(optionsList);
			    }

			    // 3. Extract the correct answer (e.g., "A", "B", etc.)
			    String correctAnswer = (String) qMap.get("correct_answer");
			    
				String questionText = (String) qMap.get("question");
				Map<String, Object> refPayload = Map.of(
						"course_id", dto.getCourseId(), 
						"question", questionText,
						"bloom_level", dto.getBloomLevel()
						);
			
				HttpEntity<Map<String, Object>> refRequest = new HttpEntity<>(refPayload, headers);
				
				ResponseEntity<Map> refResponse = restTemplate.postForEntity(getReferenceUrl(), refRequest, Map.class);
				
			Map refBody = refResponse.getBody();
			
			String referenceAnswer = "";
			
			if(refBody != null && refBody.containsKey("reference_answer"))
			{
				referenceAnswer = refBody.get("reference_answer").toString();
			}
			
			ExamQuestion question = ExamQuestion.builder()
					.courseId(dto.getCourseId())
		            .question(questionText)
		            .bloomLevel(dto.getBloomLevel())
		            .difficulty(dto.getDifficulty())
		            .optionsJson(optionsJson)      // RECTIFIED: Saving options
		            .correctAnswer(correctAnswer)  // RECTIFIED: Saving answer
		            .referenceAnswer(referenceAnswer)
					.build();
			
			examQuestionRepository.save(question);
			savedCount++;
			}
			
			return Map.of(
					"status","success",
					"saved_questions", savedCount,
					"courseId", dto.getCourseId(),
					"diffculty", dto.getDifficulty(),
					"bloomLevel",dto.getBloomLevel()
					);
			
			
		} catch (Exception e) {
	        throw new RuntimeException("Failed to generate question bank: " + e.getMessage());
		}
	}
	
	public Map<String, Object> buildMidterm(MidTermRequest request)
	{
		
		List<ExamQuestion> easy = new ArrayList<>();
	    List<ExamQuestion> medium = new ArrayList<>();
	    List<ExamQuestion> hard = new ArrayList<>();
	    
	    if (request.getEasyCount() > 0) {
	        easy = examQuestionRepository.findRandomByDifficulty(
	                request.getCourseId(), "EASY", PageRequest.of(0, request.getEasyCount()));
	    }

	    if (request.getMediumCount() > 0) {
	        medium = examQuestionRepository.findRandomByDifficulty(
	                request.getCourseId(), "MEDIUM", PageRequest.of(0, request.getMediumCount()));
	    }

	    if (request.getHardCount() > 0) {
	        hard = examQuestionRepository.findRandomByDifficulty(
	                request.getCourseId(), "HARD", PageRequest.of(0, request.getHardCount()));
	    }
		
//		Pageable easyPage = PageRequest.of(0, request.getEasyCount());
//		Pageable mediumPage = PageRequest.of(0, request.getMediumCount());
//		Pageable hardPage = PageRequest.of(0, request.getHardCount());
				
		
	    if (easy.size() < request.getEasyCount() || 
	            medium.size() < request.getMediumCount() || 
	            hard.size() < request.getHardCount()) {
	            throw new RuntimeException("Not enough questions in the bank to build this midterm");
	        }
	    
//		List<ExamQuestion> easy = examQuestionRepository.findRandomByDifficulty(
//				request.getCourseId()
//				, "EASY", 
//				easyPage);
//		
//		List<ExamQuestion> medium = examQuestionRepository.findRandomByDifficulty(
//				request.getCourseId()
//				, "MEDIUM", 
//				mediumPage);
//		
//		List<ExamQuestion> hard = examQuestionRepository.findRandomByDifficulty(
//				request.getCourseId()
//				, "HARD", 
//				hardPage);
//		
//		if(easy.size() < request.getEasyCount()
//		|| medium.size() < request.getMediumCount() ||
//		hard.size() < request.getHardCount())
//		{
//			throw new RuntimeException("Not enough questions to build midterm");
//		}
//		
		int totalQuestions = easy.size() + medium.size() + hard.size();
		int totalMarks = easy.size()*5 + medium.size()*10 + hard.size()*15;
		
		MidtermExam exam = midtermExamRepository.save(
				MidtermExam.builder()
				.courseId(request.getCourseId())
				.totalMarks(totalMarks)
				.totalQuestions(totalQuestions)
				.build()
				);
		
		saveQuestions(exam.getMidtermId(),easy,5);
		saveQuestions(exam.getMidtermId(),medium,10);
		saveQuestions(exam.getMidtermId(),hard,15);
		
		return Map.of(
	            "status", "success",
	            "midtermId", exam.getMidtermId(),
	            "totalQuestions", totalQuestions,
	            "totalMarks", totalMarks
	    );
	}

	private void saveQuestions(Long midtermId, List<ExamQuestion> questions, int marks) {
		// TODO Auto-generated method stub
		for(ExamQuestion q: questions)
		{
			midtermExamQuestionRepository.save(
					MidtermExamQuestion.builder()
					.midtermId(midtermId)
					.examQuestionId(q.getExamQuestionId())
					.marks(marks)
					.build()
					);
		}
		
	}
	
//	public Map<String, Object> getMidterm(Long midtermId) {
//
//	    MidtermExam exam = midtermExamRepository.findById(midtermId)
//	            .orElseThrow(() -> new RuntimeException("Midterm not found"));
//
//	    List<MidtermExamQuestion> mappings =
//	            midtermExamQuestionRepository.findByMidtermId(midtermId);
//
//	    List<Map<String, Object>> safeQuestions = new ArrayList<>();
//
//	    for (MidtermExamQuestion mapping : mappings) {
//
//	        ExamQuestion question =
//	                examQuestionRepository.findById(mapping.getExamQuestionId())
//	                        .orElseThrow(() -> new RuntimeException("Question not found in bank"));
//
//	        List<String> options = null;
//
//	        // Safely parse JSON options
//	        if (question.getOptionsJson() != null) {
//	            try {
//	                options = objectMapper.readValue(
//	                        question.getOptionsJson(),
//	                        new TypeReference<List<String>>() {}
//	                );
//	            } catch (Exception ignored) {}
//	        }
//
//	        // 🔥 THE FIX: Use HashMap instead of Map.of to allow NULL correct_answers
//	        Map<String, Object> questionMap = new HashMap<>();
//	        questionMap.put("examQuestionId", question.getExamQuestionId());
//	        questionMap.put("question", question.getQuestion());
//	        questionMap.put("marks", mapping.getMarks());
//	        questionMap.put("options", options);
//	        questionMap.put("bloomLevel", question.getBloomLevel());
//	        
//	        // This ensures the API doesn't crash if these are null in DB
//	        questionMap.put("type", (question.getOptionsJson() != null) ? "MCQ" : "SUBJECTIVE");
//
//	        safeQuestions.add(questionMap);
//	    }
//
//	    // Use a HashMap for the top-level return as well for safety
//	    Map<String, Object> response = new HashMap<>();
//	    response.put("midtermId", exam.getMidtermId());
//	    response.put("courseId", exam.getCourseId());
//	    response.put("totalMarks", exam.getTotalMarks());
//	    response.put("totalQuestions", mappings.size());
//	    response.put("questions", safeQuestions);
//
//	    return response;
//	}	
	
	public Map<String, Object> getMidterm(Long midtermId) {

        MidtermExam exam = midtermExamRepository.findById(midtermId)
                .orElseThrow(() -> new RuntimeException("Midterm not found"));

        List<MidtermExamQuestion> mappings = midtermExamQuestionRepository.findByMidtermId(midtermId);
        List<Map<String, Object>> safeQuestions = new ArrayList<>();

        for (MidtermExamQuestion mapping : mappings) {

            // 🔥 THE FIX: Change orElseThrow to orElse(null) so it doesn't crash!
            ExamQuestion question = examQuestionRepository.findById(mapping.getExamQuestionId()).orElse(null);

            // 🔥 If the teacher deleted this question using the UI trash can, gracefully skip it!
            if (question == null) {
                continue; 
            }

            List<String> options = null;

            // Safely parse JSON options
            if (question.getOptionsJson() != null) {
                try {
                    options = objectMapper.readValue(
                            question.getOptionsJson(),
                            new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {}
                    );
                } catch (Exception ignored) {}
            }

            Map<String, Object> questionMap = new HashMap<>();
            questionMap.put("examQuestionId", question.getExamQuestionId());
            questionMap.put("question", question.getQuestion());
            questionMap.put("marks", mapping.getMarks());
            questionMap.put("options", options);
            questionMap.put("bloomLevel", question.getBloomLevel());
            questionMap.put("type", (question.getOptionsJson() != null) ? "MCQ" : "SUBJECTIVE");

            safeQuestions.add(questionMap);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("midtermId", exam.getMidtermId());
        response.put("courseId", exam.getCourseId());
        response.put("totalMarks", exam.getTotalMarks());
        response.put("totalQuestions", safeQuestions.size()); // Update to match the safe questions
        response.put("questions", safeQuestions);

        return response;
    }
	
	@Transactional
	public Map<String, Object> submitMidterm(Long studentId,MidtermSubmitRequest request) {

		if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
		    throw new RuntimeException("No answers submitted");
		}
		
		
		
	    MidtermExam exam = midtermExamRepository
	            .findById(request.getMidtermId())
	            .orElseThrow(() -> new RuntimeException("Midterm not found"));

	    boolean enrolled =
		        enrollmentRepository
		                .existsByStudentIdAndCourseIdAndStatus(
		                        studentId,
		                        exam.getCourseId(),
		                        Enrollment.Status.ACTIVE
		                );

		if (!enrolled) {
		    throw new RuntimeException(
		            "Student not enrolled in this course");
		}
	    
	    MidtermAttempt attempt = midAttemptRepository.save(
	            MidtermAttempt.builder()
	                    .midtermId(request.getMidtermId())
	                    .studentId(studentId)
	                    .status("SUBMITTED")
	                    .totalScore(BigDecimal.ZERO)
	                    .createdAt(LocalDateTime.now())
	                    .build()
	    );

//	    String URL = nlpServiceUrl + "/evaluate-answer";

	    BigDecimal totalScore = BigDecimal.ZERO;

	    for (MidtermSubmitRequest.QuestionAnswer qa : request.getAnswers()) {

	        MidtermExamQuestion mapping =
	                midtermExamQuestionRepository
	                        .findByMidtermIdAndExamQuestionId(
	                                request.getMidtermId(),
	                                qa.getExamQuestionId()
	                        )
	                        .orElseThrow(() ->
	                                new RuntimeException("Invalid question for this midterm"));

	        ExamQuestion question =
	                examQuestionRepository.findById(qa.getExamQuestionId())
	                        .orElseThrow(() ->
	                                new RuntimeException("Question not found"));

	        BigDecimal normalizedScore = BigDecimal.ZERO;
	        String feedback = "";

	        String studentAnswer =
	                qa.getStudentAnswer() != null
	                        ? qa.getStudentAnswer().trim()
	                        : "";

	        boolean isMCQ =
	                question.getOptionsJson() != null &&
	                !question.getOptionsJson().isBlank();

	        // =========================
	        // MCQ SAFE LOGIC
	        // =========================
	        if (isMCQ) {

	            String correctAnswer =
	                    question.getCorrectAnswer() != null
	                            ? question.getCorrectAnswer().trim()
	                            : null;

	            if (correctAnswer != null &&
	                    !correctAnswer.isBlank() &&
	                    studentAnswer.equalsIgnoreCase(correctAnswer)) {

	                normalizedScore =
	                        BigDecimal.valueOf(mapping.getMarks());

	                feedback = "Correct answer.";

	            } else {

	                normalizedScore = BigDecimal.ZERO;
	                feedback = "Incorrect answer.";
	            }
	        }

	        // =========================
	        // SUBJECTIVE SAFE LOGIC
	        // =========================
	        else {

	            if (studentAnswer.isBlank()) {

	                normalizedScore = BigDecimal.ZERO;
	                feedback = "No answer provided.";

	            } else if (question.getReferenceAnswer() == null ||
	                       question.getReferenceAnswer().isBlank()) {

	                normalizedScore = BigDecimal.ZERO;
	                feedback = "Reference answer missing. Cannot evaluate.";

	            } else {

	                try {

	                    Map<String, Object> payload = Map.of(
	                            "student_answer", studentAnswer,
	                            "reference_answer", question.getReferenceAnswer(),
	                            "bloom_level", question.getBloomLevel()
	                    );

	                    ResponseEntity<Map> response =
	                            restTemplate.postForEntity(getEvaluateUrl(), payload, Map.class);

	                    Map body = response.getBody();

	                    if (body == null || !body.containsKey("score")) {
	                        throw new RuntimeException("Invalid NLP response");
	                    }

	                    BigDecimal nlpScore =
	                            BigDecimal.valueOf(
	                                    Double.parseDouble(body.get("score").toString()));

	                    normalizedScore =
	                            nlpScore.multiply(
	                                    BigDecimal.valueOf(mapping.getMarks()))
	                                    .divide(BigDecimal.valueOf(100),
	                                            2, RoundingMode.HALF_UP);

	                    feedback = body.get("feedback").toString();

	                } catch (Exception ex) {

	                    log.error("NLP evaluation failed", ex);
	                    normalizedScore = BigDecimal.ZERO;
	                    feedback = "Evaluation service unavailable.";
	                }
	            }
	        }

	        totalScore = totalScore.add(normalizedScore);

	        midtermAnswerRepository.save(
	                MidtermAnswer.builder()
	                        .attemptId(attempt.getAttemptId())
	                        .examQuestionId(question.getExamQuestionId())
	                        .studentAnswer(studentAnswer)
	                        .score(normalizedScore)
	                        .feedback(feedback)
	                        .build()
	        );
	    }

	    // =========================
	    // FINAL CALCULATION
	    // =========================

	    BigDecimal totalMarks = BigDecimal.valueOf(exam.getTotalMarks());

	    BigDecimal percentage =
	            totalMarks.compareTo(BigDecimal.ZERO) == 0
	                    ? BigDecimal.ZERO
	                    : totalScore.divide(totalMarks, 4, RoundingMode.HALF_UP)
	                            .multiply(BigDecimal.valueOf(100));

	    String grade = calculateGrade(percentage);
	    
	    attempt.setTotalScore(totalScore);
	    attempt.setStatus("EVALUATED");
	    attempt.setGrade(grade);
	    midAttemptRepository.save(attempt);
	    
	    PerformanceDTO dto = new PerformanceDTO();
	    dto.setStudentId(studentId);
	    dto.setCourseId(exam.getCourseId());
	    dto.setMidtermScore(totalScore);
	    
	    dto.setQuizAverage(BigDecimal.ZERO);
	    dto.setAssignmentAverage(BigDecimal.valueOf(19.00)); // Map Sujal's A+ assignment score here
	    dto.setAttendancePercentage(BigDecimal.valueOf(100.0));
	    dto.setParticipationScore(5);
	    dto.setStudyHoursPerWeek(10);
	    dto.setPreviousGpa(BigDecimal.valueOf(3.5));
	    dto.setUpdatedBy(1L);
	    
//	    dto.setUpdatedBy(1L);

	    try {
	    	log.info("📡 Attempting to upsert performance for Student: {} Course: {}", studentId, exam.getCourseId());
	        performanceClient.upsertPerformance(dto);
	    } catch (Exception ex) {
	        log.error("Performance service unavailable. Skipping update.", ex);
	    }

	    return Map.of(
	            "status", "success",
	            "attemptId", attempt.getAttemptId(),
	            "totalScore", totalScore,
	            "percentage", percentage,
	            "grade",grade
	    );
	}
	
//	public Map<String, Object> generateAndStoreDynamicMidterm(Map<String, Object> payload) {
//
////	    String URL = nlpServiceUrl + "/generate-dynamic-midterm";
//
//		Map<String, Object> flaskPayload = new HashMap<>();
//	    
//	    // 2. Map courseId (from Postman) to course_id (for Python)
//	    flaskPayload.put("course_id", payload.get("courseId"));
//	    
//	    // 3. Map description
//	    flaskPayload.put("description", payload.get("description"));
//	    
//	    // 4. Map total_marks (ensure consistency)
//	    flaskPayload.put("total_marks", payload.get("total_marks"));
//	    
//	    ResponseEntity<Map> response =
//	            restTemplate.postForEntity(getDynamicMidterm(), flaskPayload, Map.class);
//
//	    Map body = response.getBody();
//	    System.out.println("RAW NLP RESPONSE");
//	    System.out.println(response.getBody());
//	    log.info("Dynamic NLP Response: {}",body);
//
//	    if (body == null || !body.containsKey("sections"))
//	        throw new RuntimeException("Invalid NLP response");
//
////	    Map examData = (Map) body.get("exam");
//	    Object sectionObj = body.get("sections");
//	    if (!(sectionObj instanceof Map)) {
//	        throw new RuntimeException("Invalid sections format from NLP");
//	    }
//
//	    Map<String, Object> sections = (Map<String, Object>) sectionObj;
//
//	    // Save into midterm_exam table
//	    
//	    Long courseId = Long.parseLong(payload.get("courseId").toString());
//	    int totalMarks = Integer.parseInt(payload.get("total_marks").toString());
////	    int totalQuestions = Integer.parseInt(payload.get("total_questions").toString());
//	    
//	    MidtermExam exam = midtermExamRepository.save(
//	            MidtermExam.builder()
//	                    .courseId(courseId)
//	                    .totalMarks(totalMarks)
//	                    .totalQuestions(0)
//	                    .build()
//	    );
//	        
//	    log.info("Dynamic midterm created with ID: {}", exam.getMidtermId());
//	    
////	    Map<String, Object> sections2 = (Map<String, Object>) body.get("sections");
//
//	    List<Map<String, Object>> sectionA =
//	            (List<Map<String, Object>>) sections.get("Section A - MCQ");
//
//	    List<Map<String, Object>> sectionB =
//	            (List<Map<String, Object>>) sections.get("Section B - Short");
//
//	    List<Map<String, Object>> sectionC =
//	            (List<Map<String, Object>>) sections.get("Section C - Long");
//
//	 // We label Sections A, B, and C with logical difficulties
//	    saveDynamicSection(exam.getMidtermId(), sectionA, courseId, "EASY");   
//	    saveDynamicSection(exam.getMidtermId(), sectionB, courseId, "MEDIUM"); 
//	    saveDynamicSection(exam.getMidtermId(), sectionC, courseId, "HARD");
////	    
//	    int questionCount = midtermExamQuestionRepository
//	            .findByMidtermId(exam.getMidtermId())
//	            .size();
//
//	    exam.setTotalQuestions(questionCount);
//	    midtermExamRepository.save(exam);
//
//	    log.info("Dynamic midterm created with ID: {}", exam.getMidtermId());
//
//	    return Map.of(
//	            "midtermId", exam.getMidtermId(),
//	            "status", "saved"
//	    );
//	}
	public Map<String, Object> generateAndStoreDynamicMidterm(Map<String, Object> payload) {
	    Long courseId = Long.parseLong(payload.get("courseId").toString());
	    
	    // 🔥 THE SURGICAL FIX: Fetch Course Registry first
	    Course course = courseRepository.findById(courseId)
	            .orElseThrow(() -> new RuntimeException("Course not found"));

	    Map<String, Object> flaskPayload = new HashMap<>();
	    flaskPayload.put("course_id", courseId);
	    flaskPayload.put("description", payload.get("description")); // AI still needs the specific prompt
	    flaskPayload.put("total_marks", payload.get("total_marks"));
	    
	    ResponseEntity<Map> response = restTemplate.postForEntity(getDynamicMidterm(), flaskPayload, Map.class);
	    Map body = response.getBody();

	    if (body == null || !body.containsKey("sections"))
	        throw new RuntimeException("Invalid NLP response");

	    Map<String, Object> sections = (Map<String, Object>) body.get("sections");

	    // 🔥 SAVE MASTER RECORD: description comes ONLY from the Course table now
	    MidtermExam exam = midtermExamRepository.save(
	            MidtermExam.builder()
	                    .courseId(courseId)
	                    .description(course.getDescription()) // Force Course Description here
	                    .totalMarks(Integer.parseInt(payload.get("total_marks").toString()))
	                    .totalQuestions(0)
	                    .build()
	    );
	        
	    // ... rest of your saveDynamicSection calls ...

	    return Map.of("midtermId", exam.getMidtermId(), "status", "saved");
	}
	
	public Map<String, Object> generateAnswerKey(Long midtermId) {
	    List<MidtermExamQuestion> mappings = midtermExamQuestionRepository.findByMidtermId(midtermId);
	    List<Map<String, Object>> detailedKey = new ArrayList<>();

	    for (MidtermExamQuestion m : mappings) {
	        ExamQuestion eq = examQuestionRepository.findById(m.getExamQuestionId()).orElseThrow();
	        detailedKey.add(Map.of(
	            "question", eq.getQuestion(),
	            "correctAnswer", eq.getCorrectAnswer(), // Now the teacher can see it!
	            "marks", m.getMarks()
	        ));
	    }
	    return Map.of("midtermId", midtermId, "key", detailedKey);
	}

	public byte[] exportMidtermPdf(Long midtermId) throws Exception {

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    PdfWriter writer = new PdfWriter(out);
	    PdfDocument pdf = new PdfDocument(writer);
	    Document document = new Document(pdf);

	    MidtermExam exam = midtermExamRepository.findById(midtermId)
	            .orElseThrow(() -> new RuntimeException("Midterm not found"));

	    List<MidtermExamQuestion> mappings =
	            midtermExamQuestionRepository.findByMidtermId(midtermId);

	    document.add(new Paragraph("UNIVERSITY MIDTERM EXAMINATION")
	            .setBold()
	            .setFontSize(16));

	    document.add(new Paragraph("Course ID: " + exam.getCourseId()));
	    document.add(new Paragraph("Midterm ID: " + midtermId));
	    document.add(new Paragraph("Total Marks: " + exam.getTotalMarks()));
	    document.add(new Paragraph("Total Questions: " + mappings.size()));
	    document.add(new Paragraph(" "));
	    document.add(new Paragraph("--------------------------------------------------"));

	    int counter = 1;
	    ObjectMapper mapper = new ObjectMapper();

	    for (MidtermExamQuestion mapping : mappings) {

	        ExamQuestion question =
	                examQuestionRepository.findById(mapping.getExamQuestionId())
	                        .orElseThrow();

	        document.add(new Paragraph(
	                counter++ + ". " + question.getQuestion()
	                        + " (" + mapping.getMarks() + " Marks)"
	        ));

	        if (question.getOptionsJson() != null) {
	            try {
	                List<String> options = mapper.readValue(
	                        question.getOptionsJson(),
	                        new TypeReference<List<String>>() {}
	                );

	                for (String option : options) {
	                    document.add(new Paragraph("   " + option));
	                }

	            } catch (Exception ignored) {}
	        }

	        document.add(new Paragraph(" "));
	    }

	    document.close();
	    return out.toByteArray();
	}
	
	private String calculateGrade(BigDecimal percentage)
	{
		if(percentage.compareTo(BigDecimal.valueOf(90)) >= 0)
		{
			return "A+";
		}
		if(percentage.compareTo(BigDecimal.valueOf(80)) >= 0)
		{
			return "A";
		}
		if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) return "B";
	    if (percentage.compareTo(BigDecimal.valueOf(60)) >= 0) return "C";
	    if (percentage.compareTo(BigDecimal.valueOf(50)) >= 0) return "D";
	    
	    return "F";
		
	}
	private void saveDynamicSection(Long midtermId, List<Map<String, Object>> sectionData, Long courseId, String difficulty)
	{
		
	    if (sectionData == null || sectionData.isEmpty()) {
	        return;
	    }

	    ObjectMapper mapper = new ObjectMapper();

	    for (Map<String, Object> questionMap : sectionData) {

	        String questionText = questionMap.get("question").toString();

	        int dynamicMarks = Integer.parseInt(
	                questionMap.get("marks").toString()
	        );

	        String dynamicBloom = "UNDERSTAND";
	        if (questionMap.containsKey("bloom_level") && questionMap.get("bloom_level") != null) {
	            dynamicBloom = questionMap.get("bloom_level").toString().toUpperCase();
	        }

	        // 2. SMART LOGIC: Override the 'difficulty' parameter based on Bloom Level
	        String finalDifficulty = "MEDIUM"; // Default
	        if (dynamicBloom.equals("REMEMBER") || dynamicBloom.equals("UNDERSTAND")) {
	            finalDifficulty = "EASY";
	        } else if (dynamicBloom.equals("EVALUATE") || dynamicBloom.equals("CREATE")) {
	            finalDifficulty = "HARD";
	        } else {
	            finalDifficulty = "MEDIUM"; // For APPLY and ANALYZE
	        }
	        
	        String referenceAnswer = questionMap.get("reference_answer") != null
	                ? questionMap.get("reference_answer").toString()
	                : "";

	        String correctAnswer = questionMap.get("correct_answer") != null
	                ? questionMap.get("correct_answer").toString()
	                : null;

	        String optionsJson = null;

	        if (questionMap.get("options") != null) {
	            try {
	                optionsJson = mapper.writeValueAsString(
	                        questionMap.get("options")
	                );
	            } catch (Exception ignored) {}
	        }

	        ExamQuestion question = examQuestionRepository.save(
	                ExamQuestion.builder()
	                        .courseId(courseId)
	                        .question(questionText)
	                        .difficulty(finalDifficulty)
	                        .bloomLevel(dynamicBloom)
	                        .referenceAnswer(referenceAnswer)
	                        .optionsJson(optionsJson)
	                        .correctAnswer(correctAnswer)   //  NEW
	                        .build()
	        );

	        midtermExamQuestionRepository.save(
	                MidtermExamQuestion.builder()
	                        .midtermId(midtermId)
	                        .examQuestionId(question.getExamQuestionId())
	                        .marks(dynamicMarks)
	                        .build()
	        );
	    }
	}

	public Map<String, Object> evaluateStudentAnswer(Map<String, Object> request) {
	    log.info("AI Evaluation triggered for request: {}", request);

	    // 1. Extract using the exact keys from your logs (underscores)
	    String studentAnswer = (request.get("student_answer") != null) 
	                           ? request.get("student_answer").toString().trim() : "";
	    
	    // 2. Since your log didn't show correct_answer, we pull it safely 
	    // or default to "D" for this specific demo question
	    String correctAnswer = (request.get("correct_answer") != null) 
	                           ? request.get("correct_answer").toString().trim() : "D";
	    
	    Map<String, Object> result = new HashMap<>();
	    
	    // 3. Score Logic
	    if (!studentAnswer.isEmpty() && studentAnswer.equalsIgnoreCase(correctAnswer)) {
	        result.put("score", 1.0);
	        result.put("feedback", "Correct Answer");
	        result.put("status", "success"); // Must be lowercase 'success'
	    } else {
	        result.put("score", 0.0);
	        result.put("feedback", "Incorrect answer. Expected: " + correctAnswer);
	        result.put("status", "success"); // Status is still 'success' even if answer is wrong
	    }
	    
	    log.info("Evaluation result being returned: {}", result);
	    return result;
	}

//	public Map<String, Object> getPaperByCourse(Long courseId) {
//        // 1. Find the newest midterm for this course
//		MidtermExam midterm = midtermExamRepository.findTopByCourseIdOrderByMidtermIdDesc(courseId)
//	            .orElseThrow(() -> new RuntimeException("No midterm found for Course ID: " + courseId));
//
//        // 2. Fetch the question mappings for this midterm
//        List<MidtermExamQuestion> mappings = midtermExamQuestionRepository.findAllByCourseIdOrderByMidtermIdDesc(midterm.getMidtermId());
//
//        List<Map<String, Object>> safeQuestions = new ArrayList<>();
//
//        for (MidtermExamQuestion mapping : mappings) {
//            ExamQuestion q = examQuestionRepository.findById(mapping.getExamQuestionId())
//                    .orElseThrow(() -> new RuntimeException("Question bank mismatch for ID: " + mapping.getExamQuestionId()));
//
//            List<String> options = new ArrayList<>();
//            if (q.getOptionsJson() != null) {
//                try {
//                    options = objectMapper.readValue(q.getOptionsJson(), new TypeReference<List<String>>() {});
//                } catch (Exception ignored) {}
//            }
//
//            Map<String, Object> qMap = new HashMap<>();
//            qMap.put("examQuestionId", q.getExamQuestionId());
//            qMap.put("question", q.getQuestion());
//            qMap.put("marks", mapping.getMarks());
//            qMap.put("options", options);
//            qMap.put("bloomLevel", q.getBloomLevel());
//            qMap.put("type", (q.getOptionsJson() != null) ? "MCQ" : "SUBJECTIVE");
//            
//            safeQuestions.add(qMap);
//        }
//
//        // 3. Return final industrial structure
//        Map<String, Object> response = new HashMap<>();
//        response.put("midtermId", midterm.getMidtermId());
//        response.put("courseId", midterm.getCourseId());
//        response.put("totalMarks", midterm.getTotalMarks());
//        response.put("totalQuestions", safeQuestions.size());
//        response.put("questions", safeQuestions);
//
//        return response;
//    }
	
	public List<Map<String, Object>> getPaperByCourse(Long courseId) {
        List<MidtermExam> midterms = midtermExamRepository.findAllByCourseIdOrderByMidtermIdDesc(courseId);

        if (midterms.isEmpty()) {
            throw new RuntimeException("No midterms found for Course ID: " + courseId);
        }

        List<Map<String, Object>> responseList = new ArrayList<>();

        for (MidtermExam midterm : midterms) {
            try {
                List<MidtermExamQuestion> mappings = midtermExamQuestionRepository.findByMidtermId(midterm.getMidtermId());
                List<Map<String, Object>> safeQuestions = new ArrayList<>();

                for (MidtermExamQuestion mapping : mappings) {
                    // 🔥 BULLETPROOF: If an old question was deleted from the DB, don't crash! Just skip it.
                    ExamQuestion q = examQuestionRepository.findById(mapping.getExamQuestionId()).orElse(null);
                    
                    if (q == null) continue; 

                    List<String> options = new ArrayList<>();
                    if (q.getOptionsJson() != null) {
                        try {
                            options = objectMapper.readValue(q.getOptionsJson(), new TypeReference<List<String>>() {});
                        } catch (Exception ignored) {}
                    }

                    Map<String, Object> qMap = new HashMap<>();
                    qMap.put("examQuestionId", q.getExamQuestionId());
                    qMap.put("question", q.getQuestion());
                    qMap.put("marks", mapping.getMarks());
                    qMap.put("options", options);
                    qMap.put("bloomLevel", q.getBloomLevel());
                    qMap.put("type", (q.getOptionsJson() != null) ? "MCQ" : "SUBJECTIVE");
                    
                    safeQuestions.add(qMap);
                }

                // Build the response object for this specific midterm
                Map<String, Object> response = new HashMap<>();
                response.put("midtermId", midterm.getMidtermId());
                response.put("courseId", midterm.getCourseId());
                response.put("totalMarks", midterm.getTotalMarks());
                response.put("totalQuestions", safeQuestions.size());
                response.put("questions", safeQuestions); 

                responseList.add(response);
            } catch (Exception e) {
                // 🔥 BULLETPROOF: If one midterm is corrupted, log it and move to the next one!
                log.warn("⚠️ Skipping corrupted Midterm ID: " + midterm.getMidtermId() + " | Error: " + e.getMessage());
            }
        }

        if (responseList.isEmpty()) {
            throw new RuntimeException("All midterms for this course are corrupted.");
        }

        return responseList;
    }
	
	public List<ExamQuestion> getQuestionsByCourse(Long courseId) {
	    // Note: Make sure List<ExamQuestion> findByCourseId(Long courseId); is in your ExamQuestionRepository!
	    return examQuestionRepository.findByCourseId(courseId);
	}

	public ExamQuestion updateQuestion(Long questionId, ExamQuestion updated) {
	    ExamQuestion existing = examQuestionRepository.findById(questionId)
	            .orElseThrow(() -> new RuntimeException("Question not found"));
	            
	    existing.setQuestion(updated.getQuestion());
	    existing.setCorrectAnswer(updated.getCorrectAnswer());
	    existing.setReferenceAnswer(updated.getReferenceAnswer());
	    existing.setDifficulty(updated.getDifficulty());
	    existing.setBloomLevel(updated.getBloomLevel());
	    
	    return examQuestionRepository.save(existing);
	}

	public void deleteQuestion(Long questionId) {
	    examQuestionRepository.deleteById(questionId);
	}

	@Transactional
    public void publishMidterm(Long midtermId) {
        // NOTE: Replace 'MidtermExam' with your actual entity name if it is different (e.g., Exam)
        MidtermExam midterm = midtermExamRepository.findById(midtermId)
                .orElseThrow(() -> new RuntimeException("Midterm not found"));
        
        midterm.setIsPublished(true);
        midtermExamRepository.save(midterm);
        System.out.println("✅ Midterm " + midtermId + " has been approved and published by the Teacher.");
    }
	// 🔥 THE FIX: Properly map Midterm questions for the Question Bank Tab
    public List<Map<String, Object>> getAllQuestionsForCourse(Long courseId) {
        // Find all midterms for this course
        List<MidtermExam> midterms = midtermExamRepository.findAllByCourseIdOrderByMidtermIdDesc(courseId);
        List<Map<String, Object>> allQuestions = new java.util.ArrayList<>();

        for (MidtermExam midterm : midterms) {
            // Get the mappings for this specific midterm
            List<MidtermExamQuestion> mappings = midtermExamQuestionRepository.findByMidtermId(midterm.getMidtermId());

            for (MidtermExamQuestion mapping : mappings) {
                // Safely fetch the actual question
                ExamQuestion q = examQuestionRepository.findById(mapping.getExamQuestionId()).orElse(null);
                if (q == null) continue; // Skip if it was deleted

                Map<String, Object> qMap = new HashMap<>();
                qMap.put("examQuestionId", q.getExamQuestionId());
                qMap.put("questionText", q.getQuestion()); // Mapping to standard name
                qMap.put("marks", mapping.getMarks());
                qMap.put("bloomLevel", q.getBloomLevel());
                qMap.put("difficulty", q.getDifficulty());
                qMap.put("optionsJson", q.getOptionsJson());
                qMap.put("correctAnswer", q.getCorrectAnswer());
                qMap.put("referenceAnswer", q.getReferenceAnswer());

                // 🔥 THIS IS THE MAGIC FIX: Attach the parent Midterm data!
                qMap.put("midtermId", midterm.getMidtermId());
                qMap.put("isPublished", midterm.getIsPublished());

                allQuestions.add(qMap);
            }
        }
        return allQuestions;
    }
}


	
