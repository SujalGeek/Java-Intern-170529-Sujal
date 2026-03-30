package com.example.sre_remediation_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    public Map<String, String> getFixFromAI(String errorLog, String currentCode) {
        RestTemplate restTemplate = new RestTemplate();
        
     // GeminiService.java mein prompt update karo:
        String prompt = "Analyze this Java error: " + errorLog + 
                        "\nAnd this code: " + currentCode + 
                        "\nProvide ONLY the fixed Java code. Do not include JSON keys, explanations, or markdown. Just the code.";
        

        Map<String, Object> requestBody = Map.of(
            "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );

        String fullUrl = apiUrl + "?key=" + apiKey;
        
        try {
            Map<String, Object> response = restTemplate.postForObject(fullUrl, requestBody, Map.class);
            
            // Gemini response structure extraction
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String aiText = (String) parts.get(0).get("text");

            // Simple logic: return AI text or parse it if you add Jackson/Gson later
            return Map.of("aiResponse", aiText);
        } catch (Exception e) {
            return Map.of("error", "AI Analysis failed: " + e.getMessage());
        }
    }
}