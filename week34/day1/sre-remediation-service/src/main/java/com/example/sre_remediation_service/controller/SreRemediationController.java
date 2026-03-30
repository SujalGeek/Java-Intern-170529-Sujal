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

    /**
     * This endpoint triggers the Full AI-SRE Loop:
     * 1. Analyze Error with Gemini
     * 2. Generate Fixed Code
     * 3. Create GitHub Pull Request
     */
    @PostMapping("/automate-fix")
    public ResponseEntity<String> automateRemediation(@RequestBody Map<String, String> request) {
        String filePath = request.get("filePath");     // Path of the buggy file
        String errorLog = request.get("errorLog");     // The stack trace/error from logs
        String currentCode = request.get("currentCode"); // The original buggy code

        logger.info("Starting AI Remediation for file: {}", filePath);

        try {
            // Step 1: Get Fix from Gemini
            Map<String, String> aiResult = geminiService.getFixFromAI(errorLog, currentCode);
            
            if (aiResult.containsKey("error")) {
                return ResponseEntity.status(500).body("AI Analysis Failed: " + aiResult.get("error"));
            }

            String aiGeneratedFix = aiResult.get("aiResponse");
            logger.info("Gemini suggest a fix. Opening Pull Request...");

            // Step 2: Open PR on GitHub using the AI's fix
            String bugDescription = "Automated Fix for detected error: " + 
                                    (errorLog.length() > 50 ? errorLog.substring(0, 50) : errorLog);
            
            String prUrl = githubService.createAutoHotfixPR(filePath, aiGeneratedFix, bugDescription);

            return ResponseEntity.ok("Remediation Successful! PR Link: " + prUrl);

        } catch (Exception e) {
            logger.error("Critical error during remediation: ", e);
            return ResponseEntity.status(500).body("SRE Agent Error: " + e.getMessage());
        }
    }

    // Keep the old manual test endpoint for debugging if needed
    @PostMapping("/test-remediation")
    public ResponseEntity<String> testFix(@RequestBody Map<String, String> request) {
        try {
            String prUrl = githubService.createAutoHotfixPR(
                request.get("filePath"), 
                request.get("fixedCode"), 
                request.get("bugReport")
            );
            return ResponseEntity.ok("Manual PR created at: " + prUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Manual Test failed: " + e.getMessage());
        }
    }
}