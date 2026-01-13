package com.example.studentsmartmanagement.controller;

import com.example.studentsmartmanagement.dto.ApiResponse;
import com.example.studentsmartmanagement.entity.Question;
import com.example.studentsmartmanagement.entity.QuestionType;
import com.example.studentsmartmanagement.repository.QuestionRepository;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
public class ExamController {
	
	private final QuestionRepository questionRepository;

    // Use Constructor Injection
	public ExamController(QuestionRepository questionRepository) {
		this.questionRepository = questionRepository;
	}
	
    private final String UPLOAD_DIR = "uploads/";
    private final String PYTHON_API_URL = "http://localhost:5000/generate-exam";

    @PostMapping("/generate")
    public ResponseEntity<?> generateExam(
            @RequestParam("file") MultipartFile file,
            @RequestParam("instituteName") String instituteName) {

        try {
            // 1. Create Upload Directory
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Generate Safe Filename
            String originalName = StringUtils.cleanPath(file.getOriginalFilename());
            if (originalName == null || originalName.isEmpty()) {
                originalName = "uploaded_file.pdf"; 
            }
            String fileName = System.currentTimeMillis() + "_" + originalName;
            
            // 3. Save File
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4. PREPARE PATH FOR PYTHON
            String windowsPath = filePath.toAbsolutePath().toString();
            String wslPath = convertToWslPath(windowsPath);

            System.out.println("Sending to Python: " + wslPath);

            // 5. Send Request to Python
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("filePath", wslPath);
            requestBody.put("instituteName", instituteName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 6. Get Response
            ResponseEntity<Map> response = restTemplate.postForEntity(PYTHON_API_URL, requestEntity, Map.class);
            Map<String, Object> body = response.getBody();
            
            // FIX 1: Check "success" (lowercase) to match Python
            if(body != null && Boolean.TRUE.equals(body.get("success"))) {
            	
            	Map<String, Object> rawData = (Map<String, Object>) body.get("rawData");
            	
            	// --- SAVE MCQs ---
            	List<Map<String, Object>> mcqs = (List<Map<String, Object>>) rawData.get("section_A_mcq");
            	for(Map<String, Object> item : mcqs) {
            		Question q = new Question();
            		q.setSubject("OOP"); // Ideally pass this from Frontend
            		q.setQuestionType(QuestionType.MCQ);
            		q.setQuestionText((String) item.get("question"));
            		q.setBloomLevel((String) item.get("bloom"));
            		
            		List<String> opts = (List<String>) item.get("options");
            		if(opts.size() > 0) q.setOptionA(opts.get(0));
            		if(opts.size() > 1) q.setOptionB(opts.get(1));
            		if(opts.size() > 2) q.setOptionC(opts.get(2));
            		if(opts.size() > 3) q.setOptionD(opts.get(3));
            		
            		q.setCorrectAnswer((String) item.get("correct"));
            		q.setExplanation((String) item.get("explanation"));
            	
            		questionRepository.save(q);
            	}

                // --- FIX 2: SAVE THEORY QUESTIONS ---
                List<Map<String, Object>> theory = (List<Map<String, Object>>) rawData.get("section_B_theory");
                if (theory != null) {
                    for(Map<String, Object> item : theory) {
                        Question q = new Question();
                        q.setSubject("OOP");
                        q.setQuestionType(QuestionType.THEORY); // Use Enum
                        q.setQuestionText((String) item.get("question"));
                        q.setBloomLevel((String) item.get("bloom"));
                        
                        // Theory usually has a model answer, storing in correctAnswer field
                        q.setCorrectAnswer((String) item.get("model_answer")); 
                        
                        questionRepository.save(q);
                    }
                }
            }
            
            return ResponseEntity.ok(ApiResponse.success(body, "Exam Generated & Saved Successfully!", HttpStatus.OK));

        } catch (IOException e) {
            return ResponseEntity.status(500).body(ApiResponse.error("File upload failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.error("AI Generation failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * Helper to convert Windows paths (C:\Users\...) to WSL paths (/mnt/c/Users/...)
     */
    private String convertToWslPath(String windowsPath) {
        String converted = windowsPath.replace("\\", "/");
        if (converted.matches("^[a-zA-Z]:.*")) {
            char driveLetter = Character.toLowerCase(converted.charAt(0));
            String restOfPath = converted.substring(2);
            return "/mnt/" + driveLetter + restOfPath;
        }
        return converted;
    }
}