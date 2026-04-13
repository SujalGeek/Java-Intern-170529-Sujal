package com.example.sre_remediation_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GeminiService {

    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key}")
    private String apiKey;
    
    @Value("${gemini.api.url}")
    private String geminiApiUrl;
    
    public Map<String, String> getFixFromAI(String errorLog, String currentCode) {
        Map<String, String> result = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();

        // --- PHASE 1: FILTERING (4xx vs 5xx Logic) ---
        if (errorLog.contains("401") || errorLog.contains("403") || errorLog.contains("400")) {
            result.put("action", "IGNORE");
            result.put("reason", "User-side error (4xx). No code fix needed.");
            return result;
        }

        // --- PHASE 2: CONTEXTUAL PROMPT (Reasoning Framework) ---
//        String structuredPrompt = String.format(
//                "You are a Senior SRE and Java Expert. You MUST return the COMPLETE JAVA FILE content.\n\n" +
//                "CONTEXT:\n" +
//                "1. Error Log: %s\n" +
//                "2. Original File Content:\n%s\n\n" +
//                "TASK:\n" +
//                "Step 1: Analyze the error and the original code.\n" +
//                "Step 2: Rewrite the ENTIRE file. DO NOT REMOVE the package declaration, DO NOT REMOVE existing imports, and DO NOT REMOVE class-level annotations.\n" +
//                "Step 3: Fix the buggy method inside the file while keeping everything else exactly the same.\n" +
//                "Step 4: Output ONLY the full fixed Java code inside triple backticks (```java ... ```).\n\n" +
//                "CRITICAL: If you change the package name or remove imports, the project will break. Keep the file structure identical to the original.",
//                errorLog, currentCode
//            );
        
        String structuredPrompt = String.format(
                "You are a Senior SRE and Java Expert. You MUST return the COMPLETE JAVA FILE content.\n\n" +
                "CONTEXT:\n" +
                "1. Error Log: %s\n" +
                "2. Original File Content:\n%s\n\n" +
                "TASK:\n" +
                "Step 1: Analyze the error and the original code.\n" +
                "Step 2: Rewrite the ENTIRE file. DO NOT REMOVE the package declaration, DO NOT REMOVE existing imports, and DO NOT REMOVE class-level annotations.\n" +
                "Step 3: Fix the buggy method inside the file while keeping everything else exactly the same.\n" +
                "Step 4: Output ONLY the full fixed Java code inside triple backticks (```java ... ```).\n\n" +
                "CRITICAL: If you change the package name or remove imports, the project will break. Keep the file structure identical to the original.",
                errorLog, currentCode
            );
        try {
            Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                    Map.of("parts", List.of(Map.of("text", structuredPrompt)))
                )
            );
            
            String fullUrl = geminiApiUrl + "?key=" + apiKey;
            logger.info("DEBUG: Calling Gemini API at URL: {}", geminiApiUrl);

            Map<String, Object> response = restTemplate.postForObject(fullUrl, requestBody, Map.class);
            
            // Response parsing
            List candidates = (List) response.get("candidates");
            Map firstCandidate = (Map) candidates.get(0);
            Map content = (Map) firstCandidate.get("content");
            List parts = (List) content.get("parts");
            Map firstPart = (Map) parts.get(0);
            String aiResponse = (String) firstPart.get("text");

            logger.info("AI FULL RESPONSE RECEIVED: \n{}", aiResponse);

            // --- PHASE 3: ROBUST CODE EXTRACTION (Regex) ---
            String finalCode = extractCode(aiResponse);

            logger.info("CLEANED CODE FOR PR: \n{}", finalCode);

            result.put("action", "FIX");
            result.put("reasoning", aiResponse);
            result.put("aiResponse", finalCode); 

        } catch (Exception e) {
            logger.error("CRITICAL ERROR in GeminiService: {}", e.getMessage());
            result.put("error", e.getMessage());
        }

        return result;
    }

    /**
     * Regex based method to extract content between ```java and ```
     */
    private String extractCode(String text) {
        // Pattern matches ```java (content) ``` or just ``` (content) ```
        Pattern pattern = Pattern.compile("```(?:java)?\\s*([\\s\\S]*?)\\s*```", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        
        // Fallback: If no backticks found, returning original text (last resort)
        return text;
    }
}