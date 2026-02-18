package com.example.ai_integration_service.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
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

@Service
public class AiIntegrationService {

	@Autowired
    private  ExamQuestionRepository examQuestionRepository;
    
	@Autowired
	private  MidtermExamQuestionRepository midtermExamQuestionRepository;
    
	@Autowired
	private  MidtermExamRepository midtermExamRepository;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private QuestionBankRepository questionBankRepository;
    
    @Autowired
    private MidtermAttemptRepository midAttemptRepository;
    
    @Autowired
    private MidtermAnswerRepository midtermAnswerRepository;
    
    @Value("${nlp.service.url}")
    private String nlpServiceUrl;

    private static final Logger log =
            LoggerFactory.getLogger(AiIntegrationService.class);

    
//    private final String FLASK_API_URL = nlpServiceUrl + "/generate-quiz";
    private String getGenerateQuizUrl() {
        return nlpServiceUrl + "/generate-quiz";
    }


    
    public QuizResponseDto generateExamQuestions(QuizRequestDto requestPayload) {
        
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

	    return Map.of(
	            "midtermId", exam.getMidtermId(),
	            "courseId", exam.getCourseId(),
	            "totalMarks", exam.getTotalMarks(),
	            "totalQuestions", exam.getTotalQuestions(),
	            "questions", mappings
	    );
	}
	
	@Transactional
	public Map<String, Object> submitMidterm(MidtermSubmitRequest request) {

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
	                        .orElseThrow(() -> new RuntimeException("Invalid question"));

	        ExamQuestion question =
	                examQuestionRepository.findById(qa.getExamQuestionId())
	                        .orElseThrow(() -> new RuntimeException("Question not found"));

	        Map<String, Object> payload = Map.of(
	                "student_answer", qa.getStudentAnswer(),
	                "reference_answer", question.getReferenceAnswer(),
	                "bloom_level", question.getBloomLevel()
	        );

	        ResponseEntity<Map> response =
	                restTemplate.postForEntity(
	                        URL,
	                        payload,
	                        Map.class
	                );

	        Map body = response.getBody();

	        if (body == null || !body.containsKey("score"))
	            throw new RuntimeException("Invalid NLP response");

	        BigDecimal nlpScore =
	                BigDecimal.valueOf(Double.parseDouble(body.get("score").toString()));

	        BigDecimal normalizedScore =
	                nlpScore.multiply(BigDecimal.valueOf(mapping.getMarks()))
	                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

	        totalScore = totalScore.add(normalizedScore);

	        midtermAnswerRepository.save(
	                MidtermAnswer.builder()
	                        .attemptId(attempt.getAttemptId())
	                        .examQuestionId(question.getExamQuestionId())
	                        .studentAnswer(qa.getStudentAnswer())
	                        .score(normalizedScore)
	                        .feedback(body.get("feedback").toString())
	                        .build()
	        );
	    }

	    attempt.setTotalScore(totalScore);
	    midAttemptRepository.save(attempt);

	    return Map.of(
	            "status", "success",
	            "attemptId", attempt.getAttemptId(),
	            "totalScore", totalScore
	    );
	}
	public Map<String, Object> generateAndStoreDynamicMidterm(Map<String, Object> payload) {

	    String URL = nlpServiceUrl + "/generate-dynamic-midterm";

	    ResponseEntity<Map> response =
	            restTemplate.postForEntity(URL, payload, Map.class);

	    Map body = response.getBody();
	    System.out.println("RAW NLP RESPONSE");
	    System.out.println(response.getBody());

	    if (body == null || !body.containsKey("sections"))
	        throw new RuntimeException("Invalid NLP response");

//	    Map examData = (Map) body.get("exam");
	    Map sections = (Map) body.get("sections");

	    // Save into midterm_exam table
	    
	    Long courseId = Long.parseLong(payload.get("courseId").toString());
	    int totalMarks = Integer.parseInt(payload.get("total_marks").toString());
	    int totalQuestions = Integer.parseInt(payload.get("total_questions").toString());
	    
	    MidtermExam exam = midtermExamRepository.save(
	            MidtermExam.builder()
	                    .courseId(courseId)
	                    .totalMarks(totalMarks)
	                    .totalQuestions(totalQuestions)
	                    .build()
	    );
	    
	    saveDynamicSection(exam.getMidtermId(), sections.get("Section A - MCQ"), 2, courseId);
	    saveDynamicSection(exam.getMidtermId(), sections.get("Section B - Short Answer"), 5, courseId);
	    saveDynamicSection(exam.getMidtermId(), sections.get("Section C - Long Answer"), 10, courseId);
	    

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

//	    document.add(new Paragraph("MIDTERM EXAM"));
	    document.add(new Paragraph("UNIVERSITY MIDTERM EXAMINATION")
	            .setBold()
	            .setFontSize(16));

	    document.add(new Paragraph("Course ID: " + 
	            midtermExamRepository.findById(midtermId).orElseThrow().getCourseId()));

	    document.add(new Paragraph(" "));

	    document.add(new Paragraph("Midterm ID: " + midtermId));

	    List<MidtermExamQuestion> questions =
	            midtermExamQuestionRepository.findByMidtermId(midtermId);

	    int counter = 1;

	    for (MidtermExamQuestion q : questions) {

	        ExamQuestion question =
	                examQuestionRepository.findById(q.getExamQuestionId())
	                        .orElseThrow();

	        document.add(new Paragraph(counter++ + ". " + question.getQuestion()));
	        document.add(new Paragraph("Marks: " + q.getMarks()));
	        document.add(new Paragraph(" "));
	    }

	    document.close();
	    return out.toByteArray();
	}

	private void saveDynamicSection(Long midtermId, Object sectionData, int marks, Long courseId) {

	    if (sectionData == null) return;

	    List<String> questions = (List<String>) sectionData;

	    for (String qText : questions) {

	        ExamQuestion question = examQuestionRepository.save(
	                ExamQuestion.builder()
	                        .courseId(courseId)
	                        .question(qText)
	                        .difficulty("DYNAMIC")
	                        .bloomLevel("DYNAMIC")
	                        .referenceAnswer("") // optional later
	                        .build()
	        );

	        midtermExamQuestionRepository.save(
	                MidtermExamQuestion.builder()
	                        .midtermId(midtermId)
	                        .examQuestionId(question.getExamQuestionId())
	                        .marks(marks)
	                        .build()
	        );
	    }
	}


	
}