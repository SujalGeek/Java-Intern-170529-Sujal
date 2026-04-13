//package com.example.sre_remediation_service.controller;
//
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.example.sre_remediation_service.service.GitHubAutomationService;
//import com.example.sre_remediation_service.service.GeminiService;
//
//@RestController
//public class AlertWebhookController {
//    
//    @Autowired 
//    private GitHubAutomationService gitHubAutomationService;
//
//    @Autowired 
//    private GeminiService geminiService;
//
//    @PostMapping("/alertmanager")
//    public ResponseEntity<String> handleAlertmanagerWebhook(@RequestBody Map<String, Object> payload) {
//        System.out.println("🚨 ALERT RECEIVED FROM PROMETHEUS: " + payload);
//        
//        try {
//            List<Map<String, Object>> alerts = (List<Map<String, Object>>) payload.get("alerts");
//            
//            for (Map<String, Object> alert : alerts) {
//                Map<String, String> labels = (Map<String, String>) alert.get("labels");
//                
//                // Job name Prometheus se nikal rahe hain
//                String serviceName = labels.get("job"); 
//                String filePathToFix = "";
//                
//                // --- SMART ROUTING LOGIC (Updated for Quiz Service) ---
//                if ("user-service".equals(serviceName)) {
//                    filePathToFix = "user-service/src/main/java/com/example/user_service/controller/SreTestController.java";
//                } else if ("course-service".equals(serviceName)) {
//                    filePathToFix = "course-service/src/main/java/com/example/course_service/controller/CourseController.java"; 
//                } else if ("quiz-service".equals(serviceName)) {
//                    // Quiz Service ka path humne add kar diya
//                    filePathToFix = "quiz-service/src/main/java/com/example/quiz_service/controller/SreTestController.java";
//                }
//                else if ("assignment-service".equals(serviceName)) {
//                    // Ye path ekdum sahi hona chahiye GitHub ke hisaab se
//                    filePathToFix = "assignment-service/src/main/java/com/example/assignment_service/controller/SreTestController.java";
//                }
//                else if ("ai-integration-service".equals(serviceName)) {
//                    // 🚨 YE LINE MISSING HAI!
//                    filePathToFix = "ai-integration-service/src/main/java/com/example/ai_integration_service/controller/SreTestController.java";
//                }
//                else if ("performance-service".equals(serviceName)) {
//                    // 🚨 YE LINE MISSING HAI!
//                    filePathToFix = "performance-service/src/main/java/com/example/performance_service/controller/SreTestController.java";
//                }
//                else if ("prediction-service".equals(serviceName)) {
//                    // 🚨 YE LINE MISSING HAI!
//                    filePathToFix = "prediction-service/src/main/java/com/example/prediction_service/controller/PredictionController.java";
//                }
//                else if ("exam-result-service".equals(serviceName)) {
//                    // Ye path ekdum sahi hona chahiye GitHub ke hisaab se
//                    filePathToFix = "exam-result-service/src/main/java/com/example/exam_result_service/controller/SreTestController.java";
//                }
//                else if ("analytics-service".equals(serviceName)) {
//                    // Ye path ekdum sahi hona chahiye GitHub ke hisaab se
//                    filePathToFix = "analytics-service/src/main/java/com/example/analytics_service/controller/SreTestController.java";
//                }
//                // -------------------------------------------------------
//
//                if (!filePathToFix.isEmpty()) {
//                    System.out.println("Triggering AI Fix for: " + serviceName + " at path: " + filePathToFix);
//                    
//                    String errorLog = alert.toString(); 
//                    
//                    // Step 1: GitHub se code fetch karo
//                    String liveCode = gitHubAutomationService.getFileContent(filePathToFix);
//                    if (liveCode == null) {
//                        liveCode = "// Code unavailable on GitHub, providing error log: " + errorLog;
//                    }
//
//                    // Step 2: Gemini ko code aur error bhejo fix ke liye
//                    Map<String, String> aiResult = geminiService.getFixFromAI(errorLog, liveCode);
//                    
//                    // Step 3: Agar fix mil gaya toh PR create karo
//                    if ("FIX".equals(aiResult.get("action"))) {
//                        String aiGeneratedFix = aiResult.get("aiResponse");
//                        String prUrl = gitHubAutomationService.createAutoHotfixPR(filePathToFix, aiGeneratedFix, "Auto Fix for " + serviceName);
//                        System.out.println("✅ Success! Self-Healing PR created at: " + prUrl);
//                    } else {
//                        System.out.println("⚠️ AI Decision: " + aiResult.get("action") + " - " + aiResult.get("reason"));
//                    }
//                } else {
//                    System.out.println("ℹ️ No file path configured for service: " + serviceName);
//                }
//            }
//            return ResponseEntity.ok("Alert processed successfully!");
//            
//        } catch (Exception e) {
//            System.out.println("Critical Error in Webhook: " + e.getMessage());
//            e.printStackTrace();
//            return ResponseEntity.internalServerError().body("Error processing alert: " + e.getMessage());
//        }
//    }
//}


package com.example.sre_remediation_service.controller;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.sre_remediation_service.service.GitHubAutomationService;
import com.example.sre_remediation_service.service.GeminiService;

@RestController
public class AlertWebhookController {

    @Autowired 
    private GitHubAutomationService gitHubAutomationService;

    @Autowired 
    private GeminiService geminiService;

    @PostMapping("/alertmanager")
    public ResponseEntity<String> handleAlertmanagerWebhook(@RequestBody Map<String, Object> payload) {
        System.out.println("🚨 ALERT RECEIVED FROM PROMETHEUS: " + payload);
        
        try {
            List<Map<String, Object>> alerts = (List<Map<String, Object>>) payload.get("alerts");
            
            for (Map<String, Object> alert : alerts) {
                Map<String, String> labels = (Map<String, String>) alert.get("labels");
                Map<String, String> annotations = (Map<String, String>) alert.get("annotations");
                
                String serviceName = labels.get("job");
                // Stack trace ya error message 'description' field mein hota hai
                String alertDescription = annotations.get("description");

                // --- DYNAMIC STACK TRACE PARSING ---
                String filePathToFix = parsePathFromStackTrace(serviceName, alertDescription);

                if (filePathToFix != null && !filePathToFix.isEmpty()) {
                    System.out.println("🚀 Target Identified: " + serviceName + " -> File: " + filePathToFix);
                    
                    String errorLog = alert.toString(); 
                    
                    // Step 1: GitHub se code fetch karo
                    String liveCode = gitHubAutomationService.getFileContent(filePathToFix);
                    
                    if (liveCode == null) {
                        System.out.println("⚠️ Could not fetch code for: " + filePathToFix);
                        continue;
                    }

                    // Step 2: Gemini se fix maango
                    Map<String, String> aiResult = geminiService.getFixFromAI(errorLog, liveCode);
                    
                    // Step 3: PR Create karo
                    if ("FIX".equals(aiResult.get("action"))) {
                        String aiGeneratedFix = aiResult.get("aiResponse");
                        String prUrl = gitHubAutomationService.createAutoHotfixPR(
                            filePathToFix, 
                            aiGeneratedFix, 
                            "Autonomous SRE Fix for " + serviceName
                        );
                        System.out.println("✅ PR Created: " + prUrl);
                    } else {
                        System.out.println("ℹ️ AI skipped fix: " + aiResult.get("reason"));
                    }
                } else {
                    System.out.println("ℹ️ Skipping: No valid Java class found in Stack Trace for " + serviceName);
                }
            }
            return ResponseEntity.ok("Alerts processed successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Critical Error in Webhook: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    /**
     * Parsing Logic: Stack trace se file path nikalna
     */
    private String parsePathFromStackTrace(String serviceName, String description) {
        if (description == null || description.isEmpty()) return "";

        // Regex: dhund raha hai 'com.example' se shuru hone wali class aur .java file
        // Pattern matches: com.example.service.MyService.method(MyService.java:45)
        Pattern pattern = Pattern.compile("com\\.example\\.([a-z_]+)\\.([a-zA-Z0-9.]+)\\.([a-zA-Z0-9]+)\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(description);

        if (matcher.find()) {
            String packageNamePart = matcher.group(1); // e.g., quiz_service
            String layerPart = matcher.group(2);       // e.g., controller or service
            String className = matcher.group(4).split(":")[0]; // e.g., SreTestController.java
            
            // Final Path Construction: service-name/src/main/java/com/example/package/layer/Class.java
            return String.format("%s/src/main/java/com/example/%s/%s/%s", 
                                 serviceName, packageNamePart, layerPart, className);
        }

        // Fallback: Agar regex fail ho jaye toh default controller path (Project structure ke hisab se)
        String basePackage = serviceName.replace("-", "_");
        return serviceName + "/src/main/java/com/example/" + basePackage + "/controller/SreTestController.java";
    }
}