package com.example.ai_integration_service.controller;

import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.ai_integration_service.dto.GenerateQuestionRequest;
import com.example.ai_integration_service.dto.MidTermRequest;
import com.example.ai_integration_service.dto.MidtermSubmitRequest;
import com.example.ai_integration_service.dto.QuizRequestDto;
import com.example.ai_integration_service.entity.ExamQuestion;
import com.example.ai_integration_service.service.AiIntegrationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
public class QuizController {

    private final AiIntegrationService aiIntegrationService;

    // --- 1. QUIZ GENERATION ---
    @PostMapping("/generate")
    public ResponseEntity<?> generateExamQuestions(
            @RequestHeader("X-User-Role") Integer role,
            @RequestBody QuizRequestDto requestDto) {

        if (role != 1 && role != 2) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Only teachers or admin can generate exams");
        }
        return ResponseEntity.ok(aiIntegrationService.generateExamQuestions(requestDto));
    }

    // --- 2. QUESTION BANK GENERATION ---
    @PostMapping("/generate-store")
    public ResponseEntity<?> generateAndStore(
            @RequestHeader("X-User-Role") Integer role,
            @RequestBody GenerateQuestionRequest request) {
        
        if (role != 2) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Teachers only");
        return ResponseEntity.ok(aiIntegrationService.generateAndStore(request));
    }

    // --- 3. MIDTERM ASSEMBLY (From Bank) ---
    @PostMapping("/build")
    public ResponseEntity<?> buildMidterm(
            @RequestHeader("X-User-Role") Integer role,
            @RequestBody MidTermRequest request) {
        
        if (role != 2) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Teachers only");
        return ResponseEntity.ok(aiIntegrationService.buildMidterm(request));
    }

    // --- 4. DYNAMIC MIDTERM GENERATION (Direct AI to Paper) ---
    @PostMapping("/generate-dynamic")
    public ResponseEntity<?> generateDynamicMidterm(
            @RequestHeader("X-User-Role") Integer role,
            @RequestBody Map<String, Object> payload) {
        
        if (role != 2) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Teachers only");
        return ResponseEntity.ok(aiIntegrationService.generateAndStoreDynamicMidterm(payload));
    }

    // --- 5. FETCH SPECIFIC MIDTERM ---
    @GetMapping("/{midtermId}")
    public ResponseEntity<?> getMidterm(@PathVariable Long midtermId) {
        return ResponseEntity.ok(aiIntegrationService.getMidterm(midtermId));
    }

    // --- 6. FETCH COURSE EXAMS (For Dashboard) ---
    @GetMapping("/course/{courseId}")
    public ResponseEntity<?> getPaperByCourse(@PathVariable Long courseId) {
        try {
            return ResponseEntity.ok(aiIntegrationService.getPaperByCourse(courseId));
        } catch (Exception e) {
            // 🔥 Print the actual error to the console so we can see what's wrong!
            e.printStackTrace(); 
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "No exams found for this course"));
        }
    }

    // --- 7. EXPORT PDF ---
    @GetMapping(value = "/{midtermId}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadMidtermPdf(@PathVariable Long midtermId) {
        try {
            byte[] pdfBytes = aiIntegrationService.exportMidtermPdf(midtermId);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentDispositionFormData("attachment", "Midterm_Paper_" + midtermId + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // --- 8. STUDENT SUBMISSION ---
    @PostMapping("/submit")
    public ResponseEntity<?> submitMidterm(
            @RequestHeader("X-User-Id") Long studentId,
            @RequestBody MidtermSubmitRequest request) {
        
        return ResponseEntity.ok(aiIntegrationService.submitMidterm(studentId, request));
    }

    // --- 9. TEACHER ANSWER KEY ---
    @GetMapping("/answer-key/{midtermId}")
    public ResponseEntity<?> getAnswerKey(@PathVariable Long midtermId) {
        return ResponseEntity.ok(aiIntegrationService.generateAnswerKey(midtermId));
    }

    // --- 10. AI EVALUATION HELPER ---
    @PostMapping("/evaluate")
    public ResponseEntity<?> evaluate(@RequestBody Map<String, Object> request) {
        // Receives {student_answer, reference_answer, bloom_level}
        return ResponseEntity.ok(aiIntegrationService.evaluateStudentAnswer(request));
    }
    
 // --- 11. FETCH RAW QUESTION BANK ---
    @GetMapping("/questions/course/{courseId}")
    public ResponseEntity<?> getQuestionsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(aiIntegrationService.getQuestionsByCourse(courseId));
    }

    // --- 12. EDIT A QUESTION ---
    @PutMapping("/questions/{questionId}")
    public ResponseEntity<?> updateQuestion(@PathVariable Long questionId, @RequestBody ExamQuestion updatedQuestion) {
        return ResponseEntity.ok(aiIntegrationService.updateQuestion(questionId, updatedQuestion));
    }

    // --- 13. DELETE A QUESTION ---
    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable Long questionId) {
        aiIntegrationService.deleteQuestion(questionId);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Question deleted"));
    }
}