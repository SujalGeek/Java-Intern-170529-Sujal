package com.example.studentsmartmanagement.controller;

import com.example.studentsmartmanagement.dto.ApiResponse;
import com.example.studentsmartmanagement.entity.*;
import com.example.studentsmartmanagement.repository.*;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final QuestionRepository questionRepository;
    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;   // <--- NEW
    private final TeacherRepository teacherRepository; // <--- NEW

    // Constructor Injection
    public ExamController(QuestionRepository questionRepository, 
                          AssignmentRepository assignmentRepository,
                          CourseRepository courseRepository,
                          TeacherRepository teacherRepository) {
        this.questionRepository = questionRepository;
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.teacherRepository = teacherRepository;
    }

    private final String UPLOAD_DIR = "uploads/";
    private final String PYTHON_API_URL = "http://localhost:5000/generate-exam";

    @PostMapping("/generate")
    public ResponseEntity<?> generateExam(
            @RequestParam("file") MultipartFile file,
            @RequestParam("instituteName") String instituteName,
            @RequestParam("courseId") Long courseId,    // <--- NEW PARAM
            @RequestParam("teacherId") Long teacherId   // <--- NEW PARAM
    ) {

        try {
            // --- 0. VALIDATE INPUTS FIRST ---
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new RuntimeException("Course not found with ID: " + courseId));
            
            Teacher teacher = teacherRepository.findById(teacherId)
                    .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + teacherId));


            // --- 1. FILE UPLOAD LOGIC ---
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            String originalName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileName = System.currentTimeMillis() + "_" + (originalName.isEmpty() ? "file.pdf" : originalName);
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String wslPath = convertToWslPath(filePath.toAbsolutePath().toString());
            
            // --- 2. PYTHON REQUEST ---
            RestTemplate restTemplate = new RestTemplate();
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("filePath", wslPath);
            requestBody.put("instituteName", instituteName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(PYTHON_API_URL, requestEntity, Map.class);
            Map<String, Object> body = response.getBody();

            if (body != null && Boolean.TRUE.equals(body.get("success"))) {

                Map<String, Object> rawData = (Map<String, Object>) body.get("rawData");
                List<Question> examQuestions = new ArrayList<>();

                // --- 3. SAVE MCQs ---
                List<Map<String, Object>> mcqs = (List<Map<String, Object>>) rawData.get("section_A_mcq");
                for (Map<String, Object> item : mcqs) {
                    Question q = new Question();
                    q.setSubject(course.getCourseName()); // Use Course Name as Subject!
                    q.setTopic("AI Generated");
                    q.setQuestionType(QuestionType.MCQ);
                    q.setQuestionText((String) item.get("question"));
                    q.setBloomLevel((String) item.get("bloom"));

                    List<String> opts = (List<String>) item.get("options");
                    if (opts.size() > 0) q.setOptionA(opts.get(0));
                    if (opts.size() > 1) q.setOptionB(opts.get(1));
                    if (opts.size() > 2) q.setOptionC(opts.get(2));
                    if (opts.size() > 3) q.setOptionD(opts.get(3));

                    q.setCorrectAnswer((String) item.get("correct"));
                    q.setExplanation((String) item.get("explanation"));
                    examQuestions.add(questionRepository.save(q));
                }

                // --- 4. SAVE THEORY ---
                List<Map<String, Object>> theory = (List<Map<String, Object>>) rawData.get("section_B_theory");
                if (theory != null) {
                    for (Map<String, Object> item : theory) {
                        Question q = new Question();
                        q.setSubject(course.getCourseName());
                        q.setTopic("AI Generated");
                        q.setQuestionType(QuestionType.THEORY);
                        q.setQuestionText((String) item.get("question"));
                        q.setBloomLevel((String) item.get("bloom"));
                        q.setCorrectAnswer((String) item.get("model_answer"));
                        examQuestions.add(questionRepository.save(q));
                    }
                }

                // --- 5. CREATE ASSIGNMENT (FIXED) ---
                Assignment exam = new Assignment();
                exam.setTitle("Mid-Term: " + course.getCourseName());
                exam.setDescription("AI Generated Exam for " + course.getCourseCode());
                exam.setType(AssignmentType.ONLINE_EXAM);
                exam.setTotalMarks((double) examQuestions.size()); 
                exam.setQuestions(examQuestions);
                
                // SET THE REQUIRED FIELDS
                exam.setCourse(course);   // <--- Solves "course_id cannot be null"
                exam.setTeacher(teacher); // <--- Solves "teacher_id cannot be null"
                
                // Optional: Set due date (e.g., 7 days from now)
                exam.setDueDate(LocalDateTime.now().plusDays(7));

                Assignment savedAssignment = assignmentRepository.save(exam);
                
                Map<String, Object> responseData = new HashMap<>(body);
                responseData.put("generatedAssignmentId", savedAssignment.getAssignmentId());

                return ResponseEntity.ok(ApiResponse.success(responseData, "Exam Generated Successfully!", HttpStatus.OK));
            }

            return ResponseEntity.ok(ApiResponse.success(body, "Process finished", HttpStatus.OK));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(ApiResponse.error("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

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