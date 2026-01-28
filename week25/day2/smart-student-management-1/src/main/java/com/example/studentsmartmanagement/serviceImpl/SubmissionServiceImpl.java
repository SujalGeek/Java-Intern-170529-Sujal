package com.example.studentsmartmanagement.serviceImpl;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.studentsmartmanagement.dto.ExamSubmissionDto;
import com.example.studentsmartmanagement.entity.Assignment;
import com.example.studentsmartmanagement.entity.Question;
import com.example.studentsmartmanagement.entity.QuestionType;
import com.example.studentsmartmanagement.entity.Student;
import com.example.studentsmartmanagement.entity.Submission;
import com.example.studentsmartmanagement.entity.SubmissionAnswer;
import com.example.studentsmartmanagement.repository.AssignmentRepository;
import com.example.studentsmartmanagement.repository.QuestionRepository;
import com.example.studentsmartmanagement.repository.StudentRepository;
import com.example.studentsmartmanagement.repository.SubmissionAnswerRepository;
import com.example.studentsmartmanagement.repository.SubmissionRepository;
import com.example.studentsmartmanagement.service.SubmissionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubmissionServiceImpl implements SubmissionService {

    private final SubmissionRepository submissionRepository;
    private final SubmissionAnswerRepository submissionAnswerRepository;
    private final QuestionRepository questionRepository;
    private final AssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;
    
    private final String PYTHON_GRADING_URL = "http://localhost:5000/grade-theory";

    @Override
    @Transactional
    public Submission submitExam(ExamSubmissionDto dto) {
        
        // 1. Fetch Student & Assignment Entities
        Student student = studentRepository.findById(dto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + dto.getStudentId()));

        Assignment assignment = assignmentRepository.findById(dto.getAssignmentId())
                .orElseThrow(() -> new RuntimeException("Assignment not found with ID: " + dto.getAssignmentId()));

        // 2. Initialize Parent Submission (The "Header")
        Submission submission = new Submission();
        submission.setStudent(student);
        submission.setAssignment(assignment);
        submission.setSubmissionType("ONLINE_EXAM");
        submission.setSubmissionDate(LocalDateTime.now());
        
        // Default to GRADED (for MCQs), will change if Theory questions exist
        submission.setStatus("GRADED");
        
        // Save first to generate the Submission ID
        Submission savedSubmission = submissionRepository.save(submission);

        // 3. Process Answers
        double totalScore = 0.0;
        List<SubmissionAnswer> answerList = new ArrayList<>();
        boolean allAutoGraded = true;
        

        for (ExamSubmissionDto.StudentAnswerDto ansDto : dto.getAnswers()) {
            
            Question question = questionRepository.findById(ansDto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not Found with Id: " + ansDto.getQuestionId()));

            SubmissionAnswer answer = new SubmissionAnswer();
            answer.setSubmission(savedSubmission); // Link to parent
            answer.setQuestion(question);
            answer.setStudentAnswer(ansDto.getSelectedAnswer());

            // --- Auto-Grading Logic ---
            if (question.getQuestionType() == QuestionType.MCQ) {
                // Safe Trim & Case-Insensitive Check
                String studentAns = (ansDto.getSelectedAnswer() != null) ? ansDto.getSelectedAnswer().trim() : "";
                String correctAns = (question.getCorrectAnswer() != null) ? question.getCorrectAnswer().trim() : "";

                if (studentAns.equalsIgnoreCase(correctAns)) {
                    answer.setCorrect(true);
                    answer.setMarksAwarded(1.0); // Assuming 1 Mark per Question
                    totalScore += 1.0;
                } else {
                    answer.setCorrect(false);
                    answer.setMarksAwarded(0.0);
                }
            } else {
                // THEORY Question -> Manual Review Required
            	
            	String modelAns = question.getCorrectAnswer();
            	String studentAns = ansDto.getSelectedAnswer();
            	
            	
            	if(modelAns != null && !modelAns.isEmpty() && studentAns != null && !studentAns.isEmpty())
            	{
            		try {
            			RestTemplate restTemplate = new RestTemplate();
            			Map<String, Object> body = new HashMap<>();
            			body.put("student_answer", studentAns);
            			body.put("model_answer", modelAns);
            			body.put("max_marks", 5);
            			
            			
            			HttpHeaders headers = new HttpHeaders();
            			headers.setContentType(MediaType.APPLICATION_JSON);
            			HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body,headers);
            			
            			Map<String, Object> response = restTemplate.postForObject(PYTHON_GRADING_URL, entity, Map.class);
            			
            			if(response != null && Boolean.TRUE.equals(response.get("success")))
            			{
            				Double marks = Double.valueOf(response.get("marks_awarded").toString());
            				answer.setMarksAwarded(marks);
            				answer.setCorrect(marks >= 2.0);
            				totalScore += marks;
            			}
            			else {
            				allAutoGraded = false;
            			}
            		} catch(Exception e)
            		{
            		System.err.println("Ai Grading Failure "+ e.getMessage());
            		answer.setMarksAwarded(0.0);
            		allAutoGraded = false;
            		}
            	}
            	else {
            		answer.setMarksAwarded(0.0);
            		allAutoGraded = false;
            	}
            }

            // Add to list for batch save
            answerList.add(answer);
        }

        // 4. Batch Save All Answers (Performance Optimization)
        submissionAnswerRepository.saveAll(answerList);
       
        if (!allAutoGraded) {
            savedSubmission.setStatus("PENDING_REVIEW");
        } else {
            savedSubmission.setStatus("GRADED");
        }
        
        // 5. Update Final Score on Parent Submission
        savedSubmission.setGradeObtained(totalScore);
        
        // Return final saved entity
        return submissionRepository.save(savedSubmission);
    }
}