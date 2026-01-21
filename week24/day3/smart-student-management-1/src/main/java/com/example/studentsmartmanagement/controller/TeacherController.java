package com.example.studentsmartmanagement.controller;

import com.example.studentsmartmanagement.dto.ApiResponse;
import com.example.studentsmartmanagement.dto.CourseRequest;
import com.example.studentsmartmanagement.dto.GradeRequest;
import com.example.studentsmartmanagement.entity.Submission;
import com.example.studentsmartmanagement.entity.SubmissionAnswer;
import com.example.studentsmartmanagement.repository.SubmissionAnswerRepository;
import com.example.studentsmartmanagement.repository.SubmissionRepository;
import com.example.studentsmartmanagement.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final SubmissionRepository submissionRepository; // <--- You need this one!
    private final SubmissionAnswerRepository submissionAnswerRepository;

    
    @GetMapping("/pending-reviews")
    public ResponseEntity<ApiResponse<List<Submission>>> getPendingSubmission()
    {
    	List<Submission> pending = submissionRepository.findAll().stream()
    			.filter(s -> "PENDING_REVIEW".equals(s.getStatus()))
    			.toList();
    	
    	return ResponseEntity.ok(ApiResponse.success(pending, "Found " + pending.size() + "pending reviews.",HttpStatus.OK));
    }
    
    
    @PostMapping("/grade-answer")
    public ResponseEntity<ApiResponse<Map<String,Object>>> gradeExamAnswer(@RequestBody Map<String, Object> payload )
    {
    	
    	try {
    		Long answerId = Long.valueOf(payload.get("answerId").toString());
    		Double marks = Double.valueOf(payload.get("marks").toString());
    		
    		
    		SubmissionAnswer answer = submissionAnswerRepository.findById(answerId)
    				.orElseThrow(
    						() -> new RuntimeException("Answer not Found with Id:" + answerId));
    	
    	answer.setMarksAwarded(marks);
    	answer.setCorrect(marks>0);
    	submissionAnswerRepository.save(answer);
    	
    	Submission submission = answer.getSubmission();
    	double newTotal = submission.getAnswers().stream()
    			.mapToDouble(SubmissionAnswer::getMarksAwarded)
    			.sum();
    	
    	submission.setGradeObtained(newTotal);
    	
    	
    	Submission saved = submissionRepository.save(submission);

    	Map<String, Object> responseData = Map.of(
    			"submissionId",saved.getSubmissionId(),
    			"newGradeObtained",saved.getGradeObtained(),
    			"status",saved.getStatus()
    			);
    	
    	return ResponseEntity.ok(ApiResponse.success(responseData, "Answer Graded Successfully!", HttpStatus.OK));
    	
    	}
    	catch(Exception e)
    	{
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    				.body(ApiResponse.error("Error updating the grade: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    	}
    }

    // Endpoint 1: Create a new Course
    // URL: POST http://localhost:8080/api/teachers/courses
    @PostMapping("/courses")
    public ResponseEntity<ApiResponse<String>> addCourse(@RequestBody @Valid CourseRequest request) {
        teacherService.addCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Course added successfully", HttpStatus.CREATED));
    }

    // Endpoint 2: Grade a Student
    // URL: POST http://localhost:8080/api/teachers/grade
    @PostMapping("/grade")
    public ResponseEntity<ApiResponse<String>> assignGrade(@RequestBody @Valid GradeRequest request) {
        teacherService.assignGrade(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(null, "Grade assigned successfully", HttpStatus.CREATED));
    }
    
    
}