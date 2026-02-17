package com.example.ai_integration_service.service;

import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.ai_integration_service.dto.GenerateQuestionRequest;
import com.example.ai_integration_service.dto.MidTermRequest;
import com.example.ai_integration_service.dto.QuizRequestDto;
import com.example.ai_integration_service.dto.QuizResponseDto;
import com.example.ai_integration_service.entity.ExamQuestion;
import com.example.ai_integration_service.entity.MidtermExam;
import com.example.ai_integration_service.entity.MidtermExamQuestion;
import com.example.ai_integration_service.entity.QuestionBank;
import com.example.ai_integration_service.repository.ExamQuestionRepository;
import com.example.ai_integration_service.repository.MidtermExamQuestionRepository;
import com.example.ai_integration_service.repository.MidtermExamRepository;
import com.example.ai_integration_service.repository.QuestionBankRepository;

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
    
    
    private final String FLASK_API_URL = "http://localhost:5001/generate-quiz";

    
    public QuizResponseDto generateExamQuestions(QuizRequestDto requestPayload) {
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    
        HttpEntity<QuizRequestDto> request = new HttpEntity<>(requestPayload, headers);
        
        try {
            ResponseEntity<QuizResponseDto> response = restTemplate.postForEntity(
                    FLASK_API_URL, 
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
            throw new RuntimeException("Failed to connect to Python AI Service: " + e.getMessage());
        }
    }

	public Object generateAndStore(GenerateQuestionRequest dto) {

		try {
			String GENERATED_URL = "http://localhost:5001/generate-quiz";
			
			
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
			
			
			String REFERENCE_URL = "http://localhost:5001/generate-reference";
			
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

}