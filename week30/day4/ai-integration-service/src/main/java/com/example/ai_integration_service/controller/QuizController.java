package com.example.ai_integration_service.controller;

import java.util.Map;  

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.ai_integration_service.dto.GenerateQuestionRequest;
import com.example.ai_integration_service.dto.MidTermRequest;
import com.example.ai_integration_service.dto.MidtermSubmitRequest;
import com.example.ai_integration_service.dto.QuizRequestDto;
import com.example.ai_integration_service.dto.QuizResponseDto;
import com.example.ai_integration_service.service.AiIntegrationService;

import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/api/v1/exams")
//@CrossOrigin(origins = "*")
//public class QuizController {
//
//	@Autowired
//	private AiIntegrationService aiIntegrationService;
//	
//	@Autowired
//	private RestTemplate restTemplate;
//	
//	@PostMapping("/generate")
//	public ResponseEntity<?> generateQuiz(@RequestBody QuizRequestDto requestDto)
//	{
//		try {
//			QuizResponseDto aiResponse = aiIntegrationService.generateExamQuestions(requestDto);
//			return ResponseEntity.ok(aiResponse);
//		} catch (Exception e) {
//			// TODO: handle exception
//			return ResponseEntity.internalServerError().body("Error generating error: "+ e.getMessage());
//		}
//	}
//	
//	@PostMapping("/evaluate")
//	public ResponseEntity<?> evaluateAnswer(@RequestBody Map<String, Object> request)
//	{
//		String NLP_URL = "http://localhost:5001/evaluate-answer";
//		
//		ResponseEntity<Map> response = restTemplate.postForEntity(
//				NLP_URL
//				, request, Map.class);
//		
//		return ResponseEntity.ok(response.getBody());
//	}
//	
//	@PostMapping("/generate-question-bank")
//	public ResponseEntity<?> generateQuestionBank(@RequestBody GenerateQuestionRequest dto)
//	{
//		return ResponseEntity.ok(aiIntegrationService.generateAndStore(dto));
//	}
//	
//	@PostMapping("/build-midterm")
//	public ResponseEntity<?> buildMidterm(@RequestBody MidTermRequest request) {
//	    return ResponseEntity.ok(aiIntegrationService.buildMidterm(request));
//	}
//	
//	@GetMapping("/midterm-paper/{midtermId}")
//	public ResponseEntity<?> getMidterm(@PathVariable Long midtermId)
//	{
//		return ResponseEntity.ok(aiIntegrationService.getMidterm(midtermId));
//	}
//	
//	@PostMapping("/midterm/submit")
//	public ResponseEntity<?> submitMidterm(
//	        @RequestBody MidtermSubmitRequest request) {
//
//	    return ResponseEntity.ok(
//	            aiIntegrationService.submitMidterm(request)
//	    );
//	}
//
//	
//	@PostMapping("/dynamic-midterm")
//	public ResponseEntity<?> generateDynamicMidterm(
//	        @RequestBody Map<String, Object> request) {
//
//	    return ResponseEntity.ok(
//	            aiIntegrationService.generateAndStoreDynamicMidterm(request)
//	    );
//	}
//
//	
//	@GetMapping("/midterm/{id}/pdf")
//	public ResponseEntity<byte[]> downloadMidterm(@PathVariable Long id) throws Exception {
//
//	    byte[] pdfBytes = aiIntegrationService.exportMidtermPdf(id);
//
//	    return ResponseEntity.ok()
//	            .header("Content-Disposition", "attachment; filename=midterm_" + id + ".pdf")
//	            .contentType(MediaType.APPLICATION_PDF)
//	            .body(pdfBytes);
//	}
//	
//}

@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
public class QuizController {

    private final AiIntegrationService aiIntegrationService;

    // 🔥 TEACHER OR ADMIN
//    @PostMapping("/generate")
//    public ResponseEntity<?> generateQuiz(
//            @RequestHeader("X-User-Role") Integer role,
//            @RequestBody QuizRequestDto requestDto) {
//
//        if (role != 1 && role != 2) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Only teachers or admin can generate exams");
//        }
//
//        return ResponseEntity.ok(
//                aiIntegrationService.generateExamQuestions(requestDto)
//        );
//    }
//
//    // 🔥 STUDENT ONLY
//    @PostMapping("/midterm/submit")
//    public ResponseEntity<?> submitMidterm(
//            @RequestHeader("X-User-Role") Integer role,
//            @RequestHeader("X-User-Id") Long studentId,
//            @RequestBody MidtermSubmitRequest request) {
//
//        if (role != 3) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Only students can submit midterm");
//        }
//
//        return ResponseEntity.ok(
//                aiIntegrationService.submitMidterm(studentId, request)
//        );
//    }
//
//    // 🔥 TEACHER OR ADMIN
//    @PostMapping("/build-midterm")
//    public ResponseEntity<?> buildMidterm(
//            @RequestHeader("X-User-Role") Integer role,
//            @RequestBody MidTermRequest request) {
//
//        if (role != 1 && role != 2) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body("Access denied");
//        }
//
//        return ResponseEntity.ok(
//                aiIntegrationService.buildMidterm(request)
//        );
//    }
    
    @RestController
    @RequestMapping("/api/v1/exams")
    @RequiredArgsConstructor
    public class AiIntegrationController {

        private final AiIntegrationService aiIntegrationService;

        // This is the one you already have - Keep it as is
        @PostMapping("/generate")
        public ResponseEntity<?> generateExamQuestions(
                @RequestHeader("X-User-Role") Integer role,
                @RequestBody QuizRequestDto requestDto) {

            if (role != 1 && role != 2) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only teachers or admin can generate exams");
            }
            return ResponseEntity.ok(aiIntegrationService.generateExamQuestions(requestDto));
        }

        // 2. GENERATE AND STORE (Directly into ExamQuestion table)
        @PostMapping("/generate-store")
        public ResponseEntity<?> generateAndStore(
                @RequestHeader("X-User-Role") Integer role,
                @RequestBody GenerateQuestionRequest request) {
            if (role != 2) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Teachers only");
            
            return ResponseEntity.ok(aiIntegrationService.generateAndStore(request));
        }

        // 3. BUILD MIDTERM (Creates the MidtermId from Bank)
        @PostMapping("/build")
        public ResponseEntity<?> buildMidterm(
                @RequestHeader("X-User-Role") Integer role,
                @RequestBody MidTermRequest request) {
            
            if (role != 2) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Teachers only");
            
            return ResponseEntity.ok(aiIntegrationService.buildMidterm(request));
        }

        // 4. DYNAMIC GENERATION (All-in-one Sections)
        @PostMapping("/generate-dynamic")
        public ResponseEntity<?> generateDynamicMidterm(
                @RequestHeader("X-User-Role") Integer role,
                @RequestBody Map<String, Object> payload) {
            
            if (role != 2) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Teachers only");
            
            return ResponseEntity.ok(aiIntegrationService.generateAndStoreDynamicMidterm(payload));
        }

        // 5. GET MIDTERM FOR STUDENT (To view the paper)
        @GetMapping("/{midtermId}")
        public ResponseEntity<?> getMidterm(@PathVariable Long midtermId) {
            return ResponseEntity.ok(aiIntegrationService.getMidterm(midtermId));
        }

        // 6. STUDENT SUBMISSION
        @PostMapping("/submit")
        public ResponseEntity<?> submitMidterm(
                @RequestHeader("X-User-Id") Long studentId,
                @RequestBody MidtermSubmitRequest request) {
            
            return ResponseEntity.ok(aiIntegrationService.submitMidterm(studentId, request));
        }
        @GetMapping("/answer-key/{midtermId}")
        public ResponseEntity<?> getAnswerKey(@PathVariable Long midtermId) {
            return ResponseEntity.ok(aiIntegrationService.generateAnswerKey(midtermId));
        }
        
        @GetMapping("/export-pdf/{midtermId}")
        public ResponseEntity<byte[]> exportMidtermPdf(@PathVariable Long midtermId) throws Exception {
            byte[] pdfBytes = aiIntegrationService.exportMidtermPdf(midtermId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // This tells the browser to download the file
            headers.setContentDispositionFormData("attachment", "Midterm_Exam_" + midtermId + ".pdf");
            
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        }
    
        @PostMapping("/evaluate")
        public ResponseEntity<?> evaluate(@RequestBody Map<String, Object> request) {
            // This receives the map {student_answer, reference_answer, bloom_level}
            return ResponseEntity.ok(aiIntegrationService.evaluateStudentAnswer(request));
        }
    }
    
}