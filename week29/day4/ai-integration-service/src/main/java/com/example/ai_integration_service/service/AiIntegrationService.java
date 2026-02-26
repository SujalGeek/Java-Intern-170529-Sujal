package com.example.ai_integration_service.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import lombok.RequiredArgsConstructor;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

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
    private final RestTemplate restTemplate;
    
//    @Autowired
    private final QuestionBankRepository questionBankRepository;
    
//    @Autowired
    private final MidtermAttemptRepository midAttemptRepository;
    
//    @Autowired
    private final MidtermAnswerRepository midtermAnswerRepository;
    
    @Value("${nlp.service.url}")
    private String nlpServiceUrl;
    
    @Value("${performance.service.url}")
    private String performanceServiceUrl;
    

    private static final Logger log =
            LoggerFactory.getLogger(AiIntegrationService.class);

    
//    private final String FLASK_API_URL = nlpServiceUrl + "/generate-quiz";
    private String getGenerateQuizUrl() {
        return nlpServiceUrl + "/generate-quiz";
    }


    
    public QuizResponseDto generateExamQuestions(QuizRequestDto requestPayload) {
        
//    	httpheaders ko define karo fir headers ko payload ma add karo
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    
        HttpEntity<QuizRequestDto> request = new HttpEntity<>(requestPayload, headers);
        
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
                
                // 2. Map the data to the database entity
                savedBank.setConcept(responseBody.getConcept());
                savedBank.setBloomLevel(responseBody.getBloom_level());
                savedBank.setQuestions(responseBody.getGenerated_questions());
                
                // Grab the description from the incoming request since Python doesn't return it
                savedBank.setDescription(requestPayload.getDescription());
                
                // 3. Save to MySQL/PostgreSQL
                questionBankRepository.save(savedBank);
                
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
			String GENERATED_URL = nlpServiceUrl +  "/generate-quiz";
			
			
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			
			Map<String, Object> payload = Map.of(
				"concept",dto.getConcept(),
				"description",dto.getConcept(),
				"bloom_level",dto.getBloomLevel(),
				"count",dto.getCount()
					);
			
			HttpEntity<Map<String, Object>> request = new HttpEntity<>(
					payload,headers);
			
			ResponseEntity<Map> response = 
					restTemplate.postForEntity(GENERATED_URL,
							request,
							Map.class);
			Map body = response.getBody();
			
			if(body == null || !body.containsKey("generated_questions"))
			{
				throw new RuntimeException("Invalid Response from AI Service");
			}
			
			
			List<String> questions = (List<String>) body.get("generated_questions");
		
		
			if(questions.isEmpty())
			{
				throw new RuntimeException("No questions generated");
			}
			
			
			String REFERENCE_URL =  nlpServiceUrl + "/generate-reference";
			
			int savedCount = 0;
			
			for(String questionText: questions)
			{
				Map<String, Object> refPayload = Map.of(
						"question", questionText,
						"bloom_level", dto.getBloomLevel()
						);
			
			HttpEntity<Map<String, Object>> refRequest = 
					new HttpEntity<>(refPayload,headers);
			
			ResponseEntity<Map> refResponse = 
					restTemplate.postForEntity(REFERENCE_URL, refRequest, Map.class);
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
		
		Pageable easyPage = PageRequest.of(0, request.getEasyCount());
		Pageable mediumPage = PageRequest.of(0, request.getMediumCount());
		Pageable hardPage = PageRequest.of(0, request.getHardCount());
				
		
		List<ExamQuestion> easy = examQuestionRepository.findRandomByDifficulty(
				request.getCourseId()
				, "EASY", 
				easyPage);
		
		List<ExamQuestion> medium = examQuestionRepository.findRandomByDifficulty(
				request.getCourseId()
				, "MEDIUM", 
				mediumPage);
		
		List<ExamQuestion> hard = examQuestionRepository.findRandomByDifficulty(
				request.getCourseId()
				, "HARD", 
				hardPage);
		
		if(easy.size() < request.getEasyCount()
		|| medium.size() < request.getMediumCount() ||
		hard.size() < request.getHardCount())
		{
			throw new RuntimeException("Not enough questions to build midterm");
		}
//		
//		return Map.of(
//				"courseId", request.getCourseId(),
//				"easy",easy,
//				"medium", medium,
//				"hard",hard,
//				"totalQuestions", easy.size() + medium.size() + hard.size()
//				);
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
	
	public Map<String, Object> getMidterm(Long midtermId) {

	    MidtermExam exam = midtermExamRepository.findById(midtermId)
	            .orElseThrow(() -> new RuntimeException("Midterm not found"));

	    List<MidtermExamQuestion> mappings =
	            midtermExamQuestionRepository.findByMidtermId(midtermId);

	    List<Map<String, Object>> safeQuestions = new ArrayList<>();

	    ObjectMapper mapper = new ObjectMapper();

	    for (MidtermExamQuestion mapping : mappings) {

	        ExamQuestion question =
	                examQuestionRepository.findById(mapping.getExamQuestionId())
	                        .orElseThrow();

	        List<String> options = null;

	        if (question.getOptionsJson() != null) {
	            try {
	                options = mapper.readValue(
	                        question.getOptionsJson(),
	                        new TypeReference<List<String>>() {}
	                );
	            } catch (Exception ignored) {}
	        }

	        safeQuestions.add(Map.of(
	                "examQuestionId", question.getExamQuestionId(),
	                "question", question.getQuestion(),
	                "marks", mapping.getMarks(),
	                "options", options   // NO correctAnswer exposed
	        ));
	    }

	    return Map.of(
	            "midtermId", exam.getMidtermId(),
	            "courseId", exam.getCourseId(),
	            "totalMarks", exam.getTotalMarks(),
	            "totalQuestions", mappings.size(),
	            "questions", safeQuestions
	    );
	}
	
	@Transactional
	public Map<String, Object> submitMidterm(MidtermSubmitRequest request) {

		if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
		    throw new RuntimeException("No answers submitted");
		}
		
	    MidtermExam exam = midtermExamRepository
	            .findById(request.getMidtermId())
	            .orElseThrow(() -> new RuntimeException("Midterm not found"));

	    MidtermAttempt attempt = midAttemptRepository.save(
	            MidtermAttempt.builder()
	                    .midtermId(request.getMidtermId())
	                    .studentId(request.getStudentId())
	                    .status("SUBMITTED")
	                    .totalScore(BigDecimal.ZERO)
	                    .createdAt(LocalDateTime.now())
	                    .build()
	    );

	    String URL = nlpServiceUrl + "/evaluate-answer";

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

	                Map<String, Object> payload = Map.of(
	                        "student_answer", studentAnswer,
	                        "reference_answer", question.getReferenceAnswer(),
	                        "bloom_level", question.getBloomLevel()
	                );

	                ResponseEntity<Map> response;
	                try {
	                    response = restTemplate.postForEntity(URL, payload, Map.class);
	                } catch (Exception ex) {
	                    log.error("NLP evaluation failed", ex);
	                    normalizedScore = BigDecimal.ZERO;
	                    feedback = "Evaluation service unavailable.";
	                    continue;
	                }
	                
	                Map body = response.getBody();

	                if (body == null || !body.containsKey("score"))
	                    throw new RuntimeException("Invalid NLP response");

	                BigDecimal nlpScore =
	                        BigDecimal.valueOf(
	                                Double.parseDouble(body.get("score").toString()));

	                normalizedScore =
	                        nlpScore.multiply(
	                                BigDecimal.valueOf(mapping.getMarks()))
	                                .divide(BigDecimal.valueOf(100),
	                                        2, RoundingMode.HALF_UP);

	                feedback = body.get("feedback").toString();
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
	    dto.setStudentId(request.getStudentId());
	    dto.setCourseId(exam.getCourseId());
	    dto.setMidtermScore(totalScore);
	    dto.setUpdatedBy(1L);

	    try {
	        restTemplate.postForEntity(
	                performanceServiceUrl + "/api/performance/upsert",
	                dto,
	                Object.class
	        );
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
	
	public Map<String, Object> generateAndStoreDynamicMidterm(Map<String, Object> payload) {

	    String URL = nlpServiceUrl + "/generate-dynamic-midterm";

	    ResponseEntity<Map> response =
	            restTemplate.postForEntity(URL, payload, Map.class);

	    Map body = response.getBody();
	    System.out.println("RAW NLP RESPONSE");
	    System.out.println(response.getBody());
	    log.info("Dynamic NLP Response: {}",body);

	    if (body == null || !body.containsKey("sections"))
	        throw new RuntimeException("Invalid NLP response");

//	    Map examData = (Map) body.get("exam");
	    Object sectionObj = body.get("sections");
	    if (!(sectionObj instanceof Map)) {
	        throw new RuntimeException("Invalid sections format from NLP");
	    }

	    Map<String, Object> sections = (Map<String, Object>) sectionObj;

	    // Save into midterm_exam table
	    
	    Long courseId = Long.parseLong(payload.get("courseId").toString());
	    int totalMarks = Integer.parseInt(payload.get("total_marks").toString());
//	    int totalQuestions = Integer.parseInt(payload.get("total_questions").toString());
	    
	    MidtermExam exam = midtermExamRepository.save(
	            MidtermExam.builder()
	                    .courseId(courseId)
	                    .totalMarks(totalMarks)
	                    .totalQuestions(0)
	                    .build()
	    );
	    
	    
	    
	    log.info("Dynamic midterm created with ID: {}", exam.getMidtermId());
	    
	    Map<String, Object> sections2 = (Map<String, Object>) body.get("sections");

	    List<Map<String, Object>> sectionA =
	            (List<Map<String, Object>>) sections2.get("Section A - MCQ");

	    List<Map<String, Object>> sectionB =
	            (List<Map<String, Object>>) sections2.get("Section B - Short");

	    List<Map<String, Object>> sectionC =
	            (List<Map<String, Object>>) sections2.get("Section C - Long");

	    saveDynamicSection(exam.getMidtermId(), sectionA, courseId);
	    saveDynamicSection(exam.getMidtermId(), sectionB, courseId);
	    saveDynamicSection(exam.getMidtermId(), sectionC, courseId);
//	    
	    int questionCount = midtermExamQuestionRepository
	            .findByMidtermId(exam.getMidtermId())
	            .size();

	    exam.setTotalQuestions(questionCount);
	    midtermExamRepository.save(exam);

	    log.info("Dynamic midterm created with ID: {}", exam.getMidtermId());

	    return Map.of(
	            "midtermId", exam.getMidtermId(),
	            "status", "saved"
	    );
	}
	
	public Map<String, Object> generateAnswerKey(Long midtermId) {

	    List<MidtermExamQuestion> questions =
	            midtermExamQuestionRepository.findByMidtermId(midtermId);

	    return Map.of(
	            "midtermId", midtermId,
	            "answerKey", questions
	    );
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
	private void saveDynamicSection(
	        Long midtermId,
	        List<Map<String, Object>> sectionData,
	        Long courseId
	) {

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
	        if (questionMap.containsKey("bloom_level") &&
	            questionMap.get("bloom_level") != null) {
	            dynamicBloom = questionMap.get("bloom_level").toString().toUpperCase();
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
	                        .difficulty("DYNAMIC")
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
	
	
}


	
