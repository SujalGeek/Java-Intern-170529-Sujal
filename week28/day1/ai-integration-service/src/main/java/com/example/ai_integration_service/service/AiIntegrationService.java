package com.example.ai_integration_service.service;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.ai_integration_service.dto.QuizRequestDto;
import com.example.ai_integration_service.dto.QuizResponseDto;
import com.example.ai_integration_service.entity.QuestionBank;
import com.example.ai_integration_service.repository.QuestionBankRepository;

@Service
public class AiIntegrationService {

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
}