package com.example.sre_remediation_service.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.sre_remediation_service.service.GitHubAutomationService;
import com.example.sre_remediation_service.service.GeminiService;

@RestController
public class AlertWebhookController {
    
    @Autowired // FIX 1: Ye zaruri hai dependency injection ke liye
    private GitHubAutomationService gitHubAutomationService;

    @Autowired // FIX 2: Gemini ko bulana padega code theek karne ke liye
    private GeminiService geminiService;

    @PostMapping("/alertmanager")
    public ResponseEntity<String> handleAlertmanagerWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println("🚨 ALERT RECEIVED FROM PROMETHEUS: " + payload);
        
        try {
            // Alertmanager payload mein 'alerts' array bhejta hai
            List<Map<String, Object>> alerts = (List<Map<String, Object>>) payload.get("alerts");
            
            for (Map<String, Object> alert : alerts) {
                Map<String, String> labels = (Map<String, String>) alert.get("labels");
                
                // Service ka naam identify karo
                String serviceName = labels.get("job"); 
                String filePathToFix = "";
                
                // Smart Routing Logic:
                if ("user-service".equals(serviceName)) {
                    filePathToFix = "user-service/src/main/java/com/example/user_service/controller/SreTestController.java";
                } else if ("course-service".equals(serviceName)) {
                    filePathToFix = "course-service/src/main/java/com/example/course_service/controller/CourseController.java"; 
                }

                // Agar path mil gaya, toh asli jadoo (AI Fix) shuru karo
                if (!filePathToFix.isEmpty()) {
                    System.out.println("Triggering AI Fix for: " + serviceName + " at path: " + filePathToFix);
                    
                    String errorLog = alert.toString(); // Error details
                    
                    // Step 1: GitHub se kharab code uthao
                    String liveCode = gitHubAutomationService.getFileContent(filePathToFix);
                    if (liveCode == null) {
                        liveCode = "// Code unavailable, error log provided below\n" + errorLog;
                    }

                    // Step 2: Gemini AI se fix maango
                    Map<String, String> aiResult = geminiService.getFixFromAI(errorLog, liveCode);
                    
                    // Step 3: Agar Gemini ne fix diya, toh automatically GitHub PR bana do
                    if ("FIX".equals(aiResult.get("action"))) {
                        String aiGeneratedFix = aiResult.get("aiResponse");
                        String prUrl = gitHubAutomationService.createAutoHotfixPR(filePathToFix, aiGeneratedFix, "Auto Fix for " + serviceName);
                        System.out.println("✅ Success! Self-Healing PR created at: " + prUrl);
                    } else {
                        System.out.println("⚠️ AI Decision: " + aiResult.get("action") + " - " + aiResult.get("reason"));
                    }
                }
            }
            return ResponseEntity.ok("Alert processed and handled safely!");
            
        } catch (Exception e) {
            System.out.println("Critical Error in Webhook: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }
}