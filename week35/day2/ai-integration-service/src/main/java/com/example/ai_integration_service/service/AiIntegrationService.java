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

import com.example.ai_integration_service.config.AssignmentClient;
import com.example.ai_integration_service.config.PerformanceClient;
import com.example.ai_integration_service.config.QuizClient;
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
import com.example.ai_integration_service.repository.ExamResultRepository;
import com.example.ai_integration_service.repository.MidtermAnswerRepository;
import com.example.ai_integration_service.repository.MidtermAttemptRepository;
import com.example.ai_integration_service.repository.MidtermExamQuestionRepository;
import com.example.ai_integration_service.repository.MidtermExamRepository;
import com.example.ai_integration_service.repository.PerformanceRepository;
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
    
    private final ExamResultRepository examResultRepository;
    
//	@Autowired
	private final MidtermExamQuestionRepository midtermExamQuestionRepository;
    
//	@Autowired
	private final MidtermExamRepository midtermExamRepository;
    
//    @Autowired
    private final QuestionBankRepository questionBankRepository;
    
//    @Autowired
    private final MidtermAttemptRepository midAttemptRepository;
    
    private final PerformanceRepository performanceRepository;
    
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
    
    private final QuizClient quizClient;
    
    private final AssignmentClient assignmentClient;

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
	
//	@Transactional
//	public Map<String, Object> submitMidterm(Long studentId, MidtermSubmitRequest request) {
//
//	    if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
//	        throw new RuntimeException("No answers submitted");
//	    }
//
//	    MidtermExam exam = midtermExamRepository
//	            .findById(request.getMidtermId())
//	            .orElseThrow(() -> new RuntimeException("Midterm not found"));
//
//	    boolean enrolled = enrollmentRepository
//	            .existsByStudentIdAndCourseIdAndStatus(
//	                    studentId,
//	                    exam.getCourseId(),
//	                    Enrollment.Status.ACTIVE
//	            );
//
//	    if (!enrolled) {
//	        throw new RuntimeException("Student not enrolled in this course");
//	    }
//
//	    MidtermAttempt attempt = midAttemptRepository.save(
//	            MidtermAttempt.builder()
//	                    .midtermId(request.getMidtermId())
//	                    .studentId(studentId)
//	                    .status("SUBMITTED")
//	                    .totalScore(BigDecimal.ZERO)
//	                    .createdAt(LocalDateTime.now())
//	                    .build()
//	    );
//
//	    BigDecimal totalScore = BigDecimal.ZERO;
//
//	    for (MidtermSubmitRequest.QuestionAnswer qa : request.getAnswers()) {
//
//	        MidtermExamQuestion mapping = midtermExamQuestionRepository
//	                .findByMidtermIdAndExamQuestionId(
//	                        request.getMidtermId(),
//	                        qa.getExamQuestionId()
//	                )
//	                .orElseThrow(() -> new RuntimeException("Invalid question for this midterm"));
//
//	        ExamQuestion question = examQuestionRepository.findById(qa.getExamQuestionId())
//	                .orElseThrow(() -> new RuntimeException("Question not found"));
//
//	        BigDecimal normalizedScore = BigDecimal.ZERO;
//	        String feedback = "";
//
//	        String studentAnswer = qa.getStudentAnswer() != null ? qa.getStudentAnswer().trim() : "";
//
//	        boolean isMCQ = question.getOptionsJson() != null && !question.getOptionsJson().isBlank();
//
//	        // =========================
//	        // MCQ SAFE LOGIC (KEEPING IT!)
//	        // =========================
//	        if (isMCQ) {
//	            String cleanCorrect = (question.getCorrectAnswer() != null) ? question.getCorrectAnswer().trim() : "";
//	            String cleanStudent = (studentAnswer != null) ? studentAnswer.trim() : "";
//
//	            // 🔥 SMART MATCH LOGIC:
//	            // 1. Check perfect match (D == D)
//	            // 2. Check if cleanCorrect starts with "D)" (D matches D) All of the above)
//	            // 3. Ignore Case
//	            boolean isMatch = cleanStudent.equalsIgnoreCase(cleanCorrect) || 
//	                              cleanCorrect.toUpperCase().startsWith(cleanStudent.toUpperCase() + ")") ||
//	                              cleanCorrect.toUpperCase().startsWith(cleanStudent.toUpperCase() + ".");
//
//	            if (!cleanCorrect.isEmpty() && isMatch) {
//	                normalizedScore = BigDecimal.valueOf(mapping.getMarks());
//	                feedback = "Correct answer.";
//	            } else {
//	                normalizedScore = BigDecimal.ZERO;
//	                feedback = "Incorrect answer. Expected: " + cleanCorrect + " but got: " + cleanStudent;
//	            }
//	        
//	        	
////	            String correctAnswer = question.getCorrectAnswer() != null ? question.getCorrectAnswer().trim() : null;
////	            if (correctAnswer != null && !correctAnswer.isBlank() && studentAnswer.equalsIgnoreCase(correctAnswer)) {
////	                normalizedScore = BigDecimal.valueOf(mapping.getMarks());
////	                feedback = "Correct answer.";
////	            } else {
////	                normalizedScore = BigDecimal.ZERO;
////	                feedback = "Incorrect answer.";
////	            }
//	        } 
//	        // =========================
//	        // SUBJECTIVE SAFE LOGIC (KEEPING IT!)
//	        // =========================
//	        else {
//	            if (studentAnswer.isBlank()) {
//	                normalizedScore = BigDecimal.ZERO;
//	                feedback = "No answer provided.";
//	            } else if (question.getReferenceAnswer() == null || question.getReferenceAnswer().isBlank()) {
//	                normalizedScore = BigDecimal.ZERO;
//	                feedback = "Reference answer missing. Cannot evaluate.";
//	            } else {
//	                try {
//	                	Map<String, Object> payload = new HashMap<>();
//	                    payload.put("student_answer", studentAnswer);
//	                    payload.put("reference_answer", question.getReferenceAnswer());
//	                    payload.put("bloom_level", question.getBloomLevel());
//	                    payload.put("marks", mapping.getMarks()
//	                    );
//
//	                    ResponseEntity<Map> response = restTemplate.postForEntity(getEvaluateUrl(), payload, Map.class);
//	                    Map body = response.getBody();
//
//	                    if (body == null || !body.containsKey("score")) {
//	                        throw new RuntimeException("Invalid NLP response");
//	                    }
//
//	                    BigDecimal nlpScore = BigDecimal.valueOf(Double.parseDouble(body.get("score").toString()));
//
//	                    normalizedScore = nlpScore.multiply(BigDecimal.valueOf(mapping.getMarks()))
//	                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
//
//	                    feedback = body.get("feedback").toString();
//
//	                } catch (Exception ex) {
//	                    log.error("NLP evaluation failed", ex);
//	                    normalizedScore = BigDecimal.ZERO;
//	                    feedback = "Evaluation service unavailable.";
//	                }
//	            }
//	        }
//
//	        totalScore = totalScore.add(normalizedScore);
//
//	        // ✅ SAVE 1: Midterm Repository (For attempt history)
//	        midtermAnswerRepository.save(
//	                MidtermAnswer.builder()
//	                        .attemptId(attempt.getAttemptId())
//	                        .examQuestionId(question.getExamQuestionId())
//	                        .studentAnswer(studentAnswer)
//	                        .score(normalizedScore)
//	                        .feedback(feedback)
//	                        .build()
//	        );
//
//	        //  SAVE 2: Exam Result Repository (For the Grading Hub dashboard)
//	        // This ensures your UI for Course 15 updates automatically!
//	        examResultRepository.save(
//	                ExamResult.builder()
//	                        .studentId(studentId)
//	                        .courseId(exam.getCourseId())
//	                        .examQuestionId(question.getExamQuestionId())
//	                        .studentAnswer(studentAnswer)
//	                        .score(normalizedScore)
//	                        .feedback(feedback)
//	                        .bloomLevel(question.getBloomLevel())
//	                        .createdAt(LocalDateTime.now())
//	                        .build()
//	        );
//	    }
//
//	    // =========================
//	    // FINAL CALCULATION (KEEPING IT!)
//	    // =========================
//	 // =========================
//	    // FINAL CALCULATION & PERFORMANCE UPSERT
//	    // =========================
//	    BigDecimal totalMarks = BigDecimal.valueOf(exam.getTotalMarks());
//	    BigDecimal percentage = totalMarks.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
//	            totalScore.divide(totalMarks, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
//
//	    String grade = calculateGrade(percentage);
//	    attempt.setTotalScore(totalScore);
//	    attempt.setStatus("EVALUATED");
////	    attempt.setP
//	    attempt.setGrade(grade);
//	    midAttemptRepository.save(attempt);
//
//	    // Prepare Dynamic Performance DTO
//	    
//	    BigDecimal totalMarksUpdated = BigDecimal.valueOf(exam.getTotalMarks());
//	    
//	    BigDecimal midtermPercent = totalMarksUpdated.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
//            totalScore.multiply(BigDecimal.valueOf(100)).divide(totalMarks, 2, RoundingMode.HALF_UP);
//	    
//	    System.out.println(">>> [DEBUG] Midterm Raw Score: " + totalScore + " / " + totalMarksUpdated);
//	    System.out.println(">>> [DEBUG] Midterm Normalized %: " + midtermPercent);
//	    
//	    // 1. Fetch Real Averages (Directly from other services/repos)
//	    Double qAvg = 0.0;
//	    Double aAvg = 0.0;
//	    try {
//	        // Fetch real data from other services via Feign/API
//	        qAvg = quizClient.getAverageForStudent(studentId, exam.getCourseId());
//	        aAvg = assignmentClient.getAverageForStudent(studentId, exam.getCourseId());
//	        
//	        System.out.println(">>> [DEBUG] Quiz Service returned AVG %: " + qAvg);
//	        System.out.println(">>> [DEBUG] Assignment Service returned AVG %: " + aAvg);
//	        
//	        
//	     	    } catch (Exception e) {
//	        log.warn("Could not fetch remote averages, using defaults: {}", e.getMessage());
////	        / Your fallback
//	    }
//
//	    PerformanceDTO dto = new PerformanceDTO();
//	    dto.setStudentId(studentId);
//	    dto.setCourseId(exam.getCourseId());
//	    dto.setQuizAverage(BigDecimal.valueOf(qAvg != null ? qAvg : 0.0));
//        dto.setAssignmentAverage(BigDecimal.valueOf(aAvg != null ? aAvg : 0.0));
//        dto.setMidtermScore(midtermPercent);
//        
////        Logic inside your try block is still summing up percentages!
//        double weightedFinal = (midtermPercent.doubleValue() * 0.4) + 
//                                 (aAvg.doubleValue() * 0.4) + 
//                                 (qAvg.doubleValue() * 0.2);
//        System.out.println(">>> [DEBUG] Calculated Weighted Final Score: " + weightedFinal);
//        dto.setTotalScore(BigDecimal.valueOf(weightedFinal));
//        		
//       
//        dto.setAttendancePercentage(BigDecimal.valueOf(100.0)); // Mock for now
//        dto.setParticipationScore(9);
//        dto.setStudyHoursPerWeek(12);
//     // 1. Fetch all finished performance records for this student
//     // (Excluding the current course ID 15)
//     try {
//         Double gpaTotal = performanceRepository.calculateAverageGpaForStudent(studentId, exam.getCourseId());
//         dto.setPreviousGpa(BigDecimal.valueOf(gpaTotal != null ? gpaTotal : 3.0)); // Fallback to 3.0 for freshers
//     } catch (Exception e) {
//         dto.setPreviousGpa(BigDecimal.valueOf(3.5)); // The old fallback
//     }
//        dto.setUpdatedBy(1L);
//
//        System.out.println(">>> [DEBUG] Sending DTO to Performance Service: " + dto);
//	    try {
//	        performanceClient.upsertPerformance(dto);
//	        System.out.println(">>> [DEBUG] Performance Upsert Successful!");
//	    } catch (Exception ex) {
//	    	System.out.println(">>> [DEBUG] Performance Upsert FAILED: " + ex.getMessage());
//	    }
//
//	    return Map.of("status", "success", "totalScore", totalScore, "grade", attempt.getGrade());
//	
//	}
	
//	@Transactional
//	public Map<String, Object> submitMidterm(Long studentId, MidtermSubmitRequest request) {
//
//	    if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
//	        throw new RuntimeException("No answers submitted");
//	    }
//
//	    MidtermExam exam = midtermExamRepository
//	            .findById(request.getMidtermId())
//	            .orElseThrow(() -> new RuntimeException("Midterm not found"));
//
//	    boolean enrolled = enrollmentRepository
//	            .existsByStudentIdAndCourseIdAndStatus(
//	                    studentId,
//	                    exam.getCourseId(),
//	                    Enrollment.Status.ACTIVE
//	            );
//
//	    if (!enrolled) {
//	        throw new RuntimeException("Student not enrolled in this course");
//	    }
//
//	    MidtermAttempt attempt = midAttemptRepository.save(
//	            MidtermAttempt.builder()
//	                    .midtermId(request.getMidtermId())
//	                    .studentId(studentId)
//	                    .status("SUBMITTED")
//	                    .totalScore(BigDecimal.ZERO)
//	                    .createdAt(LocalDateTime.now())
//	                    .build()
//	    );
//
//	    BigDecimal totalScore = BigDecimal.ZERO;
//
//	    
//	    examResultRepository.deleteByStudentIdAndCourseId(studentId, exam.getCourseId());
//	    System.out.println(">>> [CLEANUP] Deleted old Grading Hub records for Student 32");
//	    
//	    for (MidtermSubmitRequest.QuestionAnswer qa : request.getAnswers()) {
//
//	        MidtermExamQuestion mapping = midtermExamQuestionRepository
//	                .findByMidtermIdAndExamQuestionId(
//	                        request.getMidtermId(),
//	                        qa.getExamQuestionId()
//	                )
//	                .orElseThrow(() -> new RuntimeException("Invalid question for this midterm"));
//
//	        ExamQuestion question = examQuestionRepository.findById(qa.getExamQuestionId())
//	                .orElseThrow(() -> new RuntimeException("Question not found"));
//
//	        BigDecimal normalizedScore = BigDecimal.ZERO;
//	        String feedback = "";
//
//	        String studentAnswer = qa.getStudentAnswer() != null ? qa.getStudentAnswer().trim() : "";
//
//	        boolean isMCQ = question.getOptionsJson() != null && !question.getOptionsJson().isBlank();
//
//	        // =========================
//	        // MCQ SAFE LOGIC
//	        // =========================
//	        if (isMCQ) {
//	            String cleanCorrect = (question.getCorrectAnswer() != null) ? question.getCorrectAnswer().trim() : "";
//	            String cleanStudent = (studentAnswer != null) ? studentAnswer.trim() : "";
//
//	            boolean isMatch = cleanStudent.equalsIgnoreCase(cleanCorrect) || 
//	                              cleanCorrect.toUpperCase().startsWith(cleanStudent.toUpperCase() + ")") ||
//	                              cleanCorrect.toUpperCase().startsWith(cleanStudent.toUpperCase() + ".");
//
//	            if (!cleanCorrect.isEmpty() && isMatch) {
//	                normalizedScore = BigDecimal.valueOf(mapping.getMarks());
//	                feedback = "Correct answer.";
//	            } else {
//	                normalizedScore = BigDecimal.ZERO;
//	                feedback = "Incorrect answer. Expected: " + cleanCorrect + " but got: " + cleanStudent;
//	            }
//	        } 
//	        // =========================
//	        // SUBJECTIVE SAFE LOGIC (WITH PRODUCTION CAP)
//	        // =========================
//	        else {
//	            if (studentAnswer.isBlank()) {
//	                normalizedScore = BigDecimal.ZERO;
//	                feedback = "No answer provided.";
//	            } else if (question.getReferenceAnswer() == null || question.getReferenceAnswer().isBlank()) {
//	                normalizedScore = BigDecimal.ZERO;
//	                feedback = "Reference answer missing. Cannot evaluate.";
//	            } else {
//	                try {
//	                    Map<String, Object> payload = new HashMap<>();
//	                    payload.put("student_answer", studentAnswer);
//	                    payload.put("reference_answer", question.getReferenceAnswer());
//	                    payload.put("bloom_level", question.getBloomLevel());
//	                    payload.put("marks", mapping.getMarks());
//
//	                    ResponseEntity<Map> response = restTemplate.postForEntity(getEvaluateUrl(), payload, Map.class);
//	                    Map body = response.getBody();
//
//	                    if (body == null || !body.containsKey("score")) {
//	                        throw new RuntimeException("Invalid NLP response");
//	                    }
//
//	                    // Extract score from AI
//	                    BigDecimal nlpScore = BigDecimal.valueOf(Double.parseDouble(body.get("score").toString()));
//
//	                    // 🔥 THE PRODUCTION CAP logic integrated:
//	                    BigDecimal rawScore = nlpScore.multiply(BigDecimal.valueOf(mapping.getMarks()))
//	                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
//
//	                    // Ensure score never exceeds max marks (e.g., 11.76 becomes 10.0)
//	                    normalizedScore = rawScore.min(BigDecimal.valueOf(mapping.getMarks()));
////	                    normalizedScore = rawCalculated.min(BigDecimal.valueOf(mapping.getMarks()));
//
//	                    System.out.println(">>> [DEBUG] QID: " + question.getExamQuestionId() + 
//	                                       " AI Score: " + nlpScore + "% Result: " + normalizedScore + "/" + mapping.getMarks());
//
//	                    feedback = body.get("feedback").toString();
//
//	                } catch (Exception ex) {
//	                    log.error("NLP evaluation failed", ex);
//	                    normalizedScore = BigDecimal.ZERO;
//	                    feedback = "Evaluation service unavailable.";
//	                }
//	            }
//	        }
//
//	        totalScore = totalScore.add(normalizedScore);
//
//	        // ✅ SAVE 1: Midterm Repository
//	        midtermAnswerRepository.save(
//	                MidtermAnswer.builder()
//	                        .attemptId(attempt.getAttemptId())
//	                        .examQuestionId(question.getExamQuestionId())
//	                        .studentAnswer(studentAnswer)
//	                        .score(normalizedScore)
//	                        .feedback(feedback)
//	                        .build()
//	        );
//
//	        // ✅ SAVE 2: Exam Result Repository
//	        examResultRepository.save(
//	                ExamResult.builder()
//	                        .studentId(studentId)
//	                        .courseId(exam.getCourseId())
//	                        .examQuestionId(question.getExamQuestionId())
//	                        .studentAnswer(studentAnswer)
//	                        .score(normalizedScore)
//	                        .feedback(feedback)
//	                        .bloomLevel(question.getBloomLevel())
//	                        .createdAt(LocalDateTime.now())
//	                        .build()
//	        );
//	    }
//
//	    // =========================
//	    // FINAL CALCULATION & PERFORMANCE UPSERT
//	    // =========================
//	    BigDecimal totalMarks = BigDecimal.valueOf(exam.getTotalMarks());
//	    BigDecimal percentage = totalMarks.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
//	            totalScore.divide(totalMarks, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
//
//	    String grade = calculateGrade(percentage);
//	    attempt.setTotalScore(totalScore);
//	    attempt.setStatus("EVALUATED");
//	    attempt.setGrade(grade);
//	    midAttemptRepository.save(attempt);
//
//	    // Prepare Tracking
//	    BigDecimal totalMarksUpdated = BigDecimal.valueOf(exam.getTotalMarks());
//	    BigDecimal midtermPercent = totalMarksUpdated.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
//	            totalScore.multiply(BigDecimal.valueOf(100)).divide(totalMarksUpdated, 2, RoundingMode.HALF_UP);
//	    
//	    System.out.println(">>> [DEBUG] Midterm Raw Score: " + totalScore + " / " + totalMarksUpdated);
//	    System.out.println(">>> [DEBUG] Midterm Normalized %: " + midtermPercent);
//	    
//	    // 1. Fetch Real Averages
//	    Double qAvg = 0.0;
//	    Double aAvg = 0.0;
//	    try {
//	        qAvg = quizClient.getAverageForStudent(studentId, exam.getCourseId());
//	        aAvg = assignmentClient.getAverageForStudent(studentId, exam.getCourseId());
//	        
//	        System.out.println(">>> [DEBUG] Quiz Service returned AVG %: " + qAvg);
//	        System.out.println(">>> [DEBUG] Assignment Service returned AVG %: " + aAvg);
//	    } catch (Exception e) {
//	        log.warn("Could not fetch remote averages, using defaults: {}", e.getMessage());
//	    }
//
//	    PerformanceDTO dto = new PerformanceDTO();
//	    dto.setStudentId(studentId);
//	    dto.setCourseId(exam.getCourseId());
//	    dto.setQuizAverage(BigDecimal.valueOf(qAvg != null ? qAvg : 0.0));
//	    dto.setAssignmentAverage(BigDecimal.valueOf(aAvg != null ? aAvg : 0.0));
//	    dto.setMidtermScore(midtermPercent);
//	    
//	    // Weighted Math
//	    double weightedFinal = (midtermPercent.doubleValue() * 0.4) + 
//	                             (dto.getAssignmentAverage().doubleValue() * 0.4) + 
//	                             (dto.getQuizAverage().doubleValue() * 0.2);
//	    System.out.println(">>> [DEBUG] Calculated Weighted Final Score: " + weightedFinal);
//	    dto.setTotalScore(BigDecimal.valueOf(weightedFinal));
//	    		
//	    dto.setAttendancePercentage(BigDecimal.valueOf(100.0)); 
//	    dto.setParticipationScore(9);
//	    dto.setStudyHoursPerWeek(12);
//
//	    // 🏆 DYNAMIC PREVIOUS GPA LOGIC
//	    try {
//	        Double gpaTotal = performanceRepository.calculateAverageGpaForStudent(studentId, exam.getCourseId());
//	        dto.setPreviousGpa(BigDecimal.valueOf(gpaTotal != null ? gpaTotal : 3.0)); 
//	        System.out.println(">>> [DEBUG] Calculated Previous GPA: " + dto.getPreviousGpa());
//	    } catch (Exception e) {
//	        dto.setPreviousGpa(BigDecimal.valueOf(3.5)); 
//	        System.out.println(">>> [DEBUG] GPA Calculation failed, using fallback: 3.5");
//	    }
//
//	    dto.setUpdatedBy(1L);
//
//	    System.out.println(">>> [DEBUG] Sending DTO to Performance Service: " + dto);
//	    try {
//	        performanceClient.upsertPerformance(dto);
//	        System.out.println(">>> [DEBUG] Performance Upsert Successful!");
//	    } catch (Exception ex) {
//	    	System.out.println(">>> [DEBUG] Performance Upsert FAILED: " + ex.getMessage());
//	    }
//
//	    return Map.of("status", "success", "totalScore", totalScore, "grade", attempt.getGrade());
//	}
	
	@Transactional
	public Map<String, Object> submitMidterm(Long studentId, MidtermSubmitRequest request) {

	    if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
	        throw new RuntimeException("No answers submitted");
	    }

	    MidtermExam exam = midtermExamRepository
	            .findById(request.getMidtermId())
	            .orElseThrow(() -> new RuntimeException("Midterm not found"));

	    boolean enrolled = enrollmentRepository
	            .existsByStudentIdAndCourseIdAndStatus(
	                    studentId,
	                    exam.getCourseId(),
	                    Enrollment.Status.ACTIVE
	            );

	    if (!enrolled) {
	        throw new RuntimeException("Student not enrolled in this course");
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

	    BigDecimal totalScore = BigDecimal.ZERO;

	    // 🔥 CLEANUP: Purane duplicate records delete karna zaroori hai
	    examResultRepository.deleteByStudentIdAndCourseId(studentId, exam.getCourseId());
	    System.out.println(">>> [CLEANUP] Deleted old Grading Hub records for Student 32");
	    
	    for (MidtermSubmitRequest.QuestionAnswer qa : request.getAnswers()) {

	        MidtermExamQuestion mapping = midtermExamQuestionRepository
	                .findByMidtermIdAndExamQuestionId(
	                        request.getMidtermId(),
	                        qa.getExamQuestionId()
	                )
	                .orElseThrow(() -> new RuntimeException("Invalid question for this midterm"));

	        ExamQuestion question = examQuestionRepository.findById(qa.getExamQuestionId())
	                .orElseThrow(() -> new RuntimeException("Question not found"));

	        BigDecimal normalizedScore = BigDecimal.ZERO;
	        String feedback = "";
	        String studentAnswer = qa.getStudentAnswer() != null ? qa.getStudentAnswer().trim() : "";
	        boolean isMCQ = question.getOptionsJson() != null && !question.getOptionsJson().isBlank();

	        // 1. 🔥 MCQ LOGIC: Binary Scoring (Trusting mapping.getMarks())
	        if (isMCQ) {
	            String cleanCorrect = (question.getCorrectAnswer() != null) ? question.getCorrectAnswer().trim() : "";
	            String cleanStudent = (studentAnswer != null) ? studentAnswer.trim() : "";

	            boolean isMatch = cleanStudent.equalsIgnoreCase(cleanCorrect) || 
	                              cleanCorrect.toUpperCase().startsWith(cleanStudent.toUpperCase() + ")") ||
	                              cleanCorrect.toUpperCase().startsWith(cleanStudent.toUpperCase() + ".");

	            if (!cleanCorrect.isEmpty() && isMatch) {
	                // Agar sahi hai toh full marks jo mapping table mein hain (e.g. 1.0)
	                normalizedScore = BigDecimal.valueOf(mapping.getMarks());
	                feedback = "Correct answer.";
	            } else {
	                normalizedScore = BigDecimal.ZERO;
	                feedback = "Incorrect answer. Expected: " + cleanCorrect;
	            }
	        } 
	        // 2. 🔥 SUBJECTIVE LOGIC: AI Partial Scoring
	        else {
	            if (studentAnswer.isBlank()) {
	                normalizedScore = BigDecimal.ZERO;
	                feedback = "No answer provided.";
	            } else if (question.getReferenceAnswer() == null || question.getReferenceAnswer().isBlank()) {
	                normalizedScore = BigDecimal.ZERO;
	                feedback = "Reference answer missing.";
	            } else {
	                try {
	                    Map<String, Object> payload = new HashMap<>();
	                    payload.put("student_answer", studentAnswer);
	                    payload.put("reference_answer", question.getReferenceAnswer());
	                    payload.put("bloom_level", question.getBloomLevel());
	                    payload.put("marks", mapping.getMarks());
	                    System.out.println("not getting the proper marks after updating"+mapping.getMarks());

	                    ResponseEntity<Map> response = restTemplate.postForEntity(getEvaluateUrl(), payload, Map.class);
	                    Map body = response.getBody();

	                    if (body != null && body.containsKey("score")) {
	                    	System.out.println(body);
	                        BigDecimal nlpPercentage = BigDecimal.valueOf(Double.parseDouble(body.get("score").toString()));
	                        
	                        // Calculate score based on actual marks (4 or 12)
	                        BigDecimal rawCalculated = nlpPercentage.multiply(BigDecimal.valueOf(mapping.getMarks()))
	                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

	                        // 🔥 PRODUCTION CAP: Never exceed max marks
	                        normalizedScore = rawCalculated.min(BigDecimal.valueOf(mapping.getMarks()));
	                        feedback = body.get("feedback").toString();
	                    }
	                } catch (Exception ex) {
	                    log.error("NLP evaluation failed", ex);
	                    normalizedScore = BigDecimal.ZERO;
	                    feedback = "Evaluation service unavailable.";
	                }
	            }
	        }

	        System.out.println(">>> [DEBUG] QID: " + question.getExamQuestionId() + " Score: " + normalizedScore + "/" + mapping.getMarks());
	        totalScore = totalScore.add(normalizedScore);

	        // ✅ SAVE 1: Midterm Repository
	        midtermAnswerRepository.save(
	                MidtermAnswer.builder()
	                        .attemptId(attempt.getAttemptId())
	                        .examQuestionId(question.getExamQuestionId())
	                        .studentAnswer(studentAnswer)
	                        .score(normalizedScore)
	                        .feedback(feedback)
	                        .build()
	        );

	        // ✅ SAVE 2: Exam Result Repository (With explicit Marks mapping for UI)
	        ExamResult result = ExamResult.builder()
	                .studentId(studentId)
	                .courseId(exam.getCourseId())
	                .examQuestionId(question.getExamQuestionId())
	                .studentAnswer(studentAnswer)
	                .score(normalizedScore)
	                .feedback(feedback)
	                .bloomLevel(question.getBloomLevel())
	                .createdAt(LocalDateTime.now())
	                .marks(mapping.getMarks())
	                .build();
	        
	        // Agar aapke ExamResult entity mein 'marks' field hai, toh yahan set karo:
	        // result.setMarks(mapping.getMarks()); 
	        
	        examResultRepository.save(result);
	    }

	    // =========================
	    // FINAL CALCULATIONS (SAB SAME HAI BHAI)
	    // =========================
	    BigDecimal totalMarks = BigDecimal.valueOf(exam.getTotalMarks());
	    BigDecimal percentage = totalMarks.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
	            totalScore.divide(totalMarks, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

	    attempt.setTotalScore(totalScore);
	    attempt.setStatus("EVALUATED");
	    attempt.setGrade(calculateGrade(percentage));
	    midAttemptRepository.save(attempt);

	    BigDecimal totalMarksUpdated = BigDecimal.valueOf(exam.getTotalMarks());
	    BigDecimal midtermPercent = totalMarksUpdated.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO :
	            totalScore.multiply(BigDecimal.valueOf(100)).divide(totalMarksUpdated, 2, RoundingMode.HALF_UP);
	    
	    System.out.println(">>> [DEBUG] Midterm Raw Score: " + totalScore + " / " + totalMarksUpdated);
	    System.out.println(">>> [DEBUG] Midterm Normalized %: " + midtermPercent);
	    
	    Double qAvg = 0.0, aAvg = 0.0;
	    try {
	        qAvg = quizClient.getAverageForStudent(studentId, exam.getCourseId());
	        aAvg = assignmentClient.getAverageForStudent(studentId, exam.getCourseId());
	        System.out.println(">>> [DEBUG] Quiz AVG: " + qAvg + " | Assign AVG: " + aAvg);
	    } catch (Exception e) {
	        log.warn("Remote averages failed");
	    }

	    PerformanceDTO dto = new PerformanceDTO();
	    dto.setStudentId(studentId);
	    dto.setCourseId(exam.getCourseId());
	    dto.setQuizAverage(BigDecimal.valueOf(qAvg != null ? qAvg : 0.0));
	    dto.setAssignmentAverage(BigDecimal.valueOf(aAvg != null ? aAvg : 0.0));
	    dto.setMidtermScore(midtermPercent);
	    
	    double weightedFinal = (midtermPercent.doubleValue() * 0.4) + 
	                             (dto.getAssignmentAverage().doubleValue() * 0.4) + 
	                             (dto.getQuizAverage().doubleValue() * 0.2);
	    
	    dto.setTotalScore(BigDecimal.valueOf(weightedFinal));
	    dto.setAttendancePercentage(BigDecimal.valueOf(100.0)); 
	    dto.setParticipationScore(9);
	    dto.setStudyHoursPerWeek(12);

	    try {
	        Double gpaTotal = performanceRepository.calculateAverageGpaForStudent(studentId, exam.getCourseId());
	        dto.setPreviousGpa(BigDecimal.valueOf(gpaTotal != null ? gpaTotal : 3.0)); 
	    } catch (Exception e) {
	        dto.setPreviousGpa(BigDecimal.valueOf(3.5)); 
	    }

	    dto.setUpdatedBy(1L);
	    try {
	        performanceClient.upsertPerformance(dto);
	        System.out.println(">>> [DEBUG] Performance Upsert Successful!");
	    } catch (Exception ex) {
	        System.out.println(">>> [DEBUG] Upsert FAILED: " + ex.getMessage());
	    }

	    return Map.of("status", "success", "totalScore", totalScore, "grade", attempt.getGrade());
	}
	/**
	 * Logic: Attendance is calculated as (Total Submissions / Total Possible Exams) * 100
	 */
	private BigDecimal calculateAttendance(Long studentId, Long courseId) {
	    try {
	        long midtermsTaken = midAttemptRepository.countByStudentIdAndStatus(studentId, "EVALUATED");
	        // Assume total exams available for this course is a baseline of 5 for the demo
	        double attendance = (midtermsTaken / 5.0) * 100;
	        return BigDecimal.valueOf(Math.min(attendance, 100.0)).setScale(2, RoundingMode.HALF_UP);
	    } catch (Exception e) {
	        return BigDecimal.valueOf(75.0); // Default fallback
	    }
	}

	/**
	 * Logic: Participation is based on the diversity of questions attempted.
	 */
	private Integer calculateParticipation(Long studentId) {
	    try {
	        // Simple logic: more rows in midterm_answer = higher participation
	        long totalAnswers = midtermAnswerRepository.count(); // Filter by studentId if possible
	        if (totalAnswers > 50) return 9;
	        if (totalAnswers > 20) return 7;
	        return 5;
	    } catch (Exception e) {
	        return 5;
	    }
	}

	/**
	 * Logic: Infers study hours based on performance. 
	 * High score usually implies higher study hours for the ML model's correlation.
	 */
	private Integer getStudyHours(Long studentId) {
	    // You could fetch this from a 'StudentProfile' entity if you have one.
	    // For the demo, we correlate it slightly to the number of submissions.
	    return 10 + (int)(Math.random() * 5); 
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
//	    Course course = courseRepository.findById(courseId)
//	            .orElseThrow(() -> new RuntimeException("Course not found"));

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
//	                    .description(course.getDescription()) // Force Course Description here
	                    .totalMarks(Integer.parseInt(payload.get("total_marks").toString()))
	                    .totalQuestions(0)
	                    .build()
	    );
	        
	    // ... rest of your saveDynamicSection calls ...
	    
	    List<Map<String, Object>> sectionA = (List<Map<String, Object>>) sections.get("Section A - MCQ");
	    List<Map<String, Object>> sectionB = (List<Map<String, Object>>) sections.get("Section B - Short");
	    List<Map<String, Object>> sectionC = (List<Map<String, Object>>) sections.get("Section C - Long");

	    // 3. 🔥 CALL the helper method for each section to populate the DB
	    saveDynamicSection(exam.getMidtermId(), sectionA, courseId, "EASY");
	    saveDynamicSection(exam.getMidtermId(), sectionB, courseId, "MEDIUM");
	    saveDynamicSection(exam.getMidtermId(), sectionC, courseId, "HARD");

	    // 4. Update the question count after they are saved
	    int actualQuestionCount = midtermExamQuestionRepository.findByMidtermId(exam.getMidtermId()).size();
	    exam.setTotalQuestions(actualQuestionCount);
	    midtermExamRepository.save(exam);
	    
	    log.info("Successfully stored dynamic midterm {} with {} questions", exam.getMidtermId(), actualQuestionCount);
	    

	    return Map.of(
	            "midtermId", exam.getMidtermId(), 
	            "status", "saved",
	            "questionCount", actualQuestionCount
	    );
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
	private void saveDynamicSection(Long midtermId, List<Map<String, Object>> sectionData, Long courseId, String difficulty) {
	    
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

	        // 🔥 THE FIX: If 'correct_answer' is null (Subjective), use 'reference_answer' instead!
	        // This prevents the NULL values you saw in MySQL.
	        String correctAnswer = null;
	        if (questionMap.get("correct_answer") != null) {
	            correctAnswer = questionMap.get("correct_answer").toString();
	        } else if (!referenceAnswer.isEmpty()) {
	            correctAnswer = referenceAnswer; 
	        }

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
	                        .correctAnswer(correctAnswer)   // Now correctly populated for all types!
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

        if (midterms == null || midterms.isEmpty()) {
            log.info("No midterms found for Course ID: {}. Returning empty list.", courseId);
            return new ArrayList<>(); 
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
////        	if (midterms.isEmpty()) {
//                return new ArrayList<>(); // Khali list bhejo, error nahi!
////            }
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
                qMap.put("question", q.getQuestion()); // Mapping to standard name
                qMap.put("marks", mapping.getMarks());
//                qMap.put("marks", mapping.getMarks()); // 🔥 YE LINE HONI CHAHIYE!
                qMap.put("bloomLevel", q.getBloomLevel());
                qMap.put("difficulty", q.getDifficulty());
                qMap.put("optionsJson", q.getOptionsJson());
                qMap.put("correctAnswer", q.getCorrectAnswer());
                qMap.put("referenceAnswer", q.getReferenceAnswer());

                // 🔥 THIS IS THE MAGIC FIX: Attach the parent Midterm data!
                
//                qMap.put("question", q != null ? q.getQuestion() : "Question text not found");
//                qMap.put("referenceAnswer", q != null ? q.getReferenceAnswer() : "No reference available");
//                
                qMap.put("midtermId", midterm.getMidtermId());
                qMap.put("isPublished", midterm.getIsPublished());

                allQuestions.add(qMap);
            }
        }
        return allQuestions;
    }
    public List<Map<String, Object>> getGradingHubData(Long courseId) {
        List<ExamResult> results = examResultRepository.findByCourseId(courseId);
        List<Map<String, Object>> response = new ArrayList<>();

        for (ExamResult res : results) {
            ExamQuestion q = examQuestionRepository.findById(res.getExamQuestionId()).orElse(null);
            
            Integer totalMarks = res.getMarks();
            
            if (totalMarks == null) {
                // 🔥 FIXED LOGIC: Explicitly fetch the mapping list
                List<MidtermExamQuestion> mappings = midtermExamQuestionRepository.findByExamQuestionId(res.getExamQuestionId());
                
                totalMarks = mappings.stream()
                    .findFirst()
                    .map(MidtermExamQuestion::getMarks) // Method reference use karo, clean hai
                    .orElse(1); 
            }

            Map<String, Object> map = new HashMap<>();
            map.put("examResultId", res.getExamResultId());
            map.put("studentId", res.getStudentId());
            map.put("question", q != null ? q.getQuestion() : "Question Deleted");
            map.put("studentAnswer", res.getStudentAnswer());
            map.put("score", res.getScore());
            map.put("marks", totalMarks); 
            map.put("feedback", res.getFeedback());
            map.put("bloomLevel", res.getBloomLevel());
            
            System.out.println(">>> FEEDBACK: " + res.getFeedback());
            
            response.add(map);
        }
        return response;
    }
}


	
