package com.example.sre_remediation_service.controller;

import com.example.sre_remediation_service.service.GitHubAutomationService; 
import com.example.sre_remediation_service.service.GeminiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/sre")
public class SreRemediationController {

    private static final Logger logger = LoggerFactory.getLogger(SreRemediationController.class);

    @Autowired
    private GitHubAutomationService githubService;

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/automate-fix")
    public ResponseEntity<String> automateRemediation(@RequestBody Map<String, Object> payload) {
        logger.info("🚀 Incoming alert detected! Starting Self-Healing Process...");

        // 1. Path of the vulnerable file
        System.out.println("working aftet the logged one ");
//        String filePath = "src/main/java/com/example/user_service/controller/SreTestController.java";
        String filePath = "user-service/src/main/java/com/example/user_service/controller/SreTestController.java";
        
        String errorLog = payload.toString();

        try {
            // 2. LIVE CONTEXT: Fetch actual code from GitHub
        	System.out.println("try ma gaya bhai code");
            logger.info("Fetching current code from GitHub for context...");
            String liveCode = githubService.getFileContent(filePath);
            
            if (liveCode == null) {
                logger.warn("Could not fetch live code, falling back to basic analysis.");
                liveCode = "// Original code unavailable, error log provided below\n" + errorLog;
            }

            // 3. AI ANALYSIS: Surgical Strike Fix
            logger.info("Sending code and logs to Gemini for surgical fix...");
            Map<String, String> aiResult = geminiService.getFixFromAI(errorLog, liveCode);
            
            if ("IGNORE".equals(aiResult.get("action"))) {
                logger.info("SRE Agent Decision: IGNORE. Reason: {}", aiResult.get("reason"));
                return ResponseEntity.ok("Ignored: User-side error.");
            }

            if (aiResult.containsKey("error")) {
                return ResponseEntity.status(500).body("AI Analysis Failed: " + aiResult.get("error"));
            }

            logger.info("AI Reasoning: {}", aiResult.get("reasoning"));
            String aiGeneratedFix = aiResult.get("aiResponse");

            // 4. PR AUTOMATION: Create the Fix PR
            String bugDescription = "ArithmeticException Fix (Surgical)";
            String prUrl = githubService.createAutoHotfixPR(filePath, aiGeneratedFix, bugDescription);

            logger.info("✅ Success! PR created at: {}", prUrl);
            return ResponseEntity.ok("Success! Self-Healing PR Created: " + prUrl);

        } catch (Exception e) {
            logger.error("Error during remediation: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}