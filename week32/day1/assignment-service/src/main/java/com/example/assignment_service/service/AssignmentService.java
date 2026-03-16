//package com.example.assignment_service.service;
//
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import com.example.assignment_service.dto.AssignmentDetailsResponse;
//import com.example.assignment_service.dto.QuestionDto;
//import com.example.assignment_service.dto.StudentAnswerDto;
//import com.example.assignment_service.dto.SubmitAssignmentRequest;
//import com.example.assignment_service.entity.Assignment;
//import com.example.assignment_service.entity.AssignmentAnswer;
//import com.example.assignment_service.entity.AssignmentAttempt;
//import com.example.assignment_service.entity.AssignmentQuestion;
//import com.example.assignment_service.repository.AssignmentAnswerRepository;
//import com.example.assignment_service.repository.AssignmentAttemptRepository;
//import com.example.assignment_service.repository.AssignmentQuestionRepository;
//import com.example.assignment_service.repository.AssignmentRepository;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class AssignmentService {
//
//	private final AssignmentRepository assignmentRepository;
//	private final AssignmentAnswerRepository assignmentAnswerRepository;
//	private final AssignmentAttemptRepository assignmentAttemptRepository;
//	private final AssignmentQuestionRepository assignmentQuestionRepository;
//	
//	@Autowired // This overrides Lombok for this specific field
//    @Qualifier("externalRestTemplate")
//    private RestTemplate restTemplate; // Remove 'final' here!
//    
//    @Value("${nlp.service.url}")
//    private String nlpServiceUrl;
//
//
////	@Value("${nlp.service.url}")
////	private String nlpServiceUrl;
//	
//	public String getnlpAssignment() {
//		 return nlpServiceUrl + "/generate-assignment";
//	}
//	
//	public String getnlpEvaluateAssignment() {
//		return nlpServiceUrl + "/evaluate-assignment";
//	}
//	
////	private static final String NLP_URL = "http://localhost:5001/generate-assignment";
////	private static final String EVAL_URL = "http://localhost:5001/evaluate-assignment";
////	
//	
//	public Assignment generateAssignment(Long courseId,String description,Integer totalMarks, String difficulty)
//	{
//		Map<String, Object> request = new HashMap<>();
//		request.put("course_id", courseId);
//		request.put("description", description);
//		request.put("total_marks", totalMarks);
//		request.put("difficulty", difficulty);
//		
//		System.out.println("Sending Assignment Request: " + request);
//		
//		ResponseEntity<Map> response = 
//				restTemplate.postForEntity(getnlpAssignment(), request, Map.class);
//		
//		Map assignmentData = (Map) response.getBody().get("assignment");
//		
//		Assignment assignment = new Assignment();
//		assignment.setCourseId(courseId);
//		assignment.setTotalMarks((Integer) assignmentData.get("total_marks"));
//		
//		
//		assignment.setCourseId(courseId);
////        assignment.setDescription(description);
////        assignment.setTotalMarks(totalMarks);
//		assignment = assignmentRepository.save(assignment);
//		
//		List<Map<String, Object>> questions =(List<Map<String,Object>>) assignmentData.get("questions");		
//		for(Map<String, Object> q : questions)
//		{
//		
//			AssignmentQuestion question = new AssignmentQuestion();
//			question.setAssignmentId(assignment.getAssignmentId());
//			question.setQuestionText((String) q.get("question"));
//			question.setMarks((Integer)q.get("marks"));
//			question.setBloomLevel((String) q.get("bloom_level"));
//		
//			assignmentQuestionRepository.save(question);
//		}
//		
//		return assignment;
//	}
//	
////	public AssignmentAttempt submitAssignment(SubmitAssignmentRequest request)
////	{
////		List<AssignmentQuestion> questions = assignmentQuestionRepository.findByAssignmentId(request.getAssignmentId());
////		
////		Map<String, Object> request2 = new HashMap<>();
////		List<Map<String, Object>> questionPayload = questions.stream().map(q -> {
////		    Map<String, Object> map = new HashMap<>();
////		    map.put("question", q.getQuestionText());
////		    map.put("marks", q.getMarks());
////		    return map;
////		}).toList();
////
////		request2.put("questions", questionPayload);
////		request2.put("answers", request.getAnswers());
////		
////		if (questions.size() != request.getAnswers().size()) {
////		    throw new RuntimeException("Answer count mismatch");
////		}
////		
////		ResponseEntity<Map> response = restTemplate.postForEntity(EVAL_URL, request2, Map.class);
////		
////		BigDecimal totalScore = BigDecimal.valueOf((Double) response.getBody().get("total_score"));
////		
////		BigDecimal percentage = BigDecimal.valueOf((Double) response.getBody().get("percentage"));
////		
////		String grade = calculateGrade(percentage);
////		
////		AssignmentAttempt attempt = new AssignmentAttempt();
////		attempt.setAssignmentId(request.getAssignmentId());
////		attempt.setStudentId(request.getStudentId());
////		attempt.setTotalScore(totalScore);
////		attempt.setGrade(grade);
////		attempt.setPercentage(percentage);
////		
////		attempt = assignmentAttemptRepository.save(attempt);
////		
////		return attempt;
////	}
//	@Transactional
//	public AssignmentAttempt submitAssignment(Long studentId,SubmitAssignmentRequest request) {
//
//	    List<AssignmentQuestion> questions =
//	            assignmentQuestionRepository.findByAssignmentId(request.getAssignmentId());
//
//	    if (questions.isEmpty()) {
//	        throw new RuntimeException("Assignment not found");
//	    }
//
//	    // Convert DB questions to Map for fast lookup
//	    Map<Long, AssignmentQuestion> questionMap =
//	            questions.stream()
//	                     .collect(Collectors.toMap(
//	                         AssignmentQuestion::getQuestionId,
//	                         q -> q
//	                     ));
//
//	    // Build payload for Flask
//	    List<Map<String, Object>> questionPayload = new ArrayList<>();
//	    List<String> answerPayload = new ArrayList<>();
//
//	    for (StudentAnswerDto studentAnswer : request.getAnswers()) {
//
//	        AssignmentQuestion question =
//	                questionMap.get(studentAnswer.getQuestionId());
//
//	        if (question == null) {
//	            throw new RuntimeException("Invalid questionId: "
//	                    + studentAnswer.getQuestionId());
//	        }
//
//	        Map<String, Object> qMap = new HashMap<>();
//	        qMap.put("question", question.getQuestionText());
//	        qMap.put("marks", question.getMarks());
//
//	        questionPayload.add(qMap);
//	        answerPayload.add(studentAnswer.getAnswer());
//	    }
//
//	    Map<String, Object> flaskRequest = new HashMap<>();
//	    flaskRequest.put("questions", questionPayload);
//	    flaskRequest.put("answers", answerPayload);
//
//	    boolean alreadySubmitted = assignmentAttemptRepository.existsByAssignmentIdAndStudentId(
//	    		request.getAssignmentId(),
//	    		studentId
//	    		);
//	    if(alreadySubmitted)
//	    {
//	    	throw new RuntimeException("Assignment already submitted");
//	    }
//	    
//	    ResponseEntity<Map> response =
//	            restTemplate.postForEntity(getnlpEvaluateAssignment(), flaskRequest, Map.class);
//
//	    Number totalScoreNum =
//	            (Number) response.getBody().get("total_score");
//	    Number percentageNum =
//	            (Number) response.getBody().get("percentage");
//
//	    BigDecimal totalScore =
//	            BigDecimal.valueOf(totalScoreNum.doubleValue());
//	    BigDecimal percentage =
//	            BigDecimal.valueOf(percentageNum.doubleValue());
//
//	    String grade = calculateGrade(percentage);
//
//	    AssignmentAttempt attempt = new AssignmentAttempt();
//	    attempt.setAssignmentId(request.getAssignmentId());
//	    attempt.setStudentId(studentId);
//	    attempt.setTotalScore(totalScore);
//	    attempt.setPercentage(percentage);
//	    attempt.setGrade(grade);
//
//	    attempt = assignmentAttemptRepository.save(attempt);
//
//	    List<Map<String, Object>> results =
//	            (List<Map<String, Object>>) response.getBody().get("results");
//
//	    for (int i = 0; i < request.getAnswers().size(); i++) {
//
//	        StudentAnswerDto studentAnswer = request.getAnswers().get(i);
//	        Map<String, Object> result = results.get(i);
//
//	        AssignmentAnswer answer = new AssignmentAnswer();
//	        answer.setAttemptId(attempt.getAttemptId());
//	        answer.setQuestionId(studentAnswer.getQuestionId());
//	        answer.setStudentAnswer(studentAnswer.getAnswer());
//	        answer.setScore(BigDecimal.valueOf(
//	                ((Number) result.get("score")).doubleValue()
//	        ));
//	        answer.setFeedback((String) result.get("feedback"));
//
//	        assignmentAnswerRepository.save(answer);
//	    }
//	    
//	    
//	    return attempt;
//	}
//
//	private String calculateGrade(BigDecimal percentage) {
//		  if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) return "A+";
//		    if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) return "A";
//		    if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) return "B";
//		    if (percentage.compareTo(BigDecimal.valueOf(60)) >= 0) return "C";
//		    if (percentage.compareTo(BigDecimal.valueOf(50)) >= 0) return "D";
//		    return "F";
//	}
//	
//	
//	public AssignmentDetailsResponse getAssignmentById(Long assignmentId)
//	{
//		Assignment assignment = assignmentRepository.findById(assignmentId)
//				.orElseThrow(() -> new RuntimeException("Assignment not found"));
//		
//		List<AssignmentQuestion> questions = assignmentQuestionRepository.findByAssignmentId(assignmentId);
//		
//		List<QuestionDto> questionDtos = questions.stream()
//				.map( q-> new QuestionDto(
//						q.getQuestionId(),
//						q.getQuestionText(),
//						q.getMarks(),
//						q.getBloomLevel()
//					)).toList();
//				
//				return new AssignmentDetailsResponse(
//						assignment.getAssignmentId(),
//						assignment.getCourseId(),
//						assignment.getTotalMarks(),
//						questionDtos
//						);
//	}
//
//	// Inside AssignmentService.java
//	public List<Assignment> getAllAssignments() {
//        return assignmentRepository.findAll();
//    }
//
//    /**
//     * 🔍 TARGETED SYNC: Used for student-specific dashboards
//     */
//    public List<Assignment> getAssignmentsByCourseIds(List<Long> courseIds) {
//        return assignmentRepository.findByCourseIdIn(courseIds);
//    }
//    
//    }
//

package com.example.assignment_service.service;

import java.math.BigDecimal; 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.assignment_service.dto.AssignmentDetailsResponse;
import com.example.assignment_service.dto.QuestionDto;
import com.example.assignment_service.dto.StudentAnswerDto;
import com.example.assignment_service.dto.SubmitAssignmentRequest;
import com.example.assignment_service.entity.Assignment;
import com.example.assignment_service.entity.AssignmentAnswer;
import com.example.assignment_service.entity.AssignmentAttempt;
import com.example.assignment_service.entity.AssignmentQuestion;
import com.example.assignment_service.repository.AssignmentAnswerRepository;
import com.example.assignment_service.repository.AssignmentAttemptRepository;
import com.example.assignment_service.repository.AssignmentQuestionRepository;
import com.example.assignment_service.repository.AssignmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentAnswerRepository assignmentAnswerRepository;
    private final AssignmentAttemptRepository assignmentAttemptRepository;
    private final AssignmentQuestionRepository assignmentQuestionRepository;
    
    @Autowired 
    @Qualifier("externalRestTemplate")
    private RestTemplate restTemplate; 
    
    @Value("${nlp.service.url}")
    private String nlpServiceUrl;

    public String getnlpAssignment() {
         return nlpServiceUrl + "/generate-assignment";
    }
    
    public String getnlpEvaluateAssignment() {
        return nlpServiceUrl + "/evaluate-assignment";
    }
    
    // --- 1. GLOBAL FETCH FOR REGISTRY (Keeps UI Working) ---
    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    // --- 2. GENERATION LOGIC (Your logic, perfectly preserved) ---
//    public Assignment generateAssignment(Long courseId, String description, Integer totalMarks, String difficulty) {
//        Map<String, Object> request = new HashMap<>();
//        request.put("course_id", courseId);
//        request.put("description", description);
//        request.put("total_marks", totalMarks);
//        request.put("difficulty", difficulty);
//        
//        // Hits your Flask NLP Core
//        ResponseEntity<Map> response = restTemplate.postForEntity(getnlpAssignment(), request, Map.class);
//        
//        // Extracting exactly how your Python service sends it
//        Map assignmentData = (Map) response.getBody().get("assignment");
//        
//        Assignment assignment = new Assignment();
//        assignment.setCourseId(courseId);
//        // This is the line you were worried about - I'm using your exact logic!
//        assignment.setTotalMarks((Integer) assignmentData.get("total_marks"));
//        
//        assignment = assignmentRepository.save(assignment);
//        
//        List<Map<String, Object>> questions = (List<Map<String,Object>>) assignmentData.get("questions");        
//        for(Map<String, Object> q : questions) {
//            AssignmentQuestion question = new AssignmentQuestion();
//            question.setAssignmentId(assignment.getAssignmentId());
//            question.setQuestionText((String) q.get("question"));
//            question.setMarks((Integer)q.get("marks"));
//            question.setBloomLevel((String) q.get("bloom_level"));
//            assignmentQuestionRepository.save(question);
//        }
//        
//        return assignment;
//    }
    public Assignment generateAssignment(Long courseId, String description, Integer totalMarks, String difficulty) {
        Map<String, Object> request = new HashMap<>();
        request.put("course_id", courseId);
        request.put("description", description);
        request.put("total_marks", totalMarks);
        request.put("difficulty", difficulty);
        
        // Hits your Flask NLP Core
        ResponseEntity<Map> response = restTemplate.postForEntity(getnlpAssignment(), request, Map.class);
        
        // Extracting exactly how your Python service sends it
        Map assignmentData = (Map) response.getBody().get("assignment");
        
        Assignment assignment = new Assignment();
        assignment.setCourseId(courseId);
        assignment.setTotalMarks((Integer) assignmentData.get("total_marks"));
        
        assignment = assignmentRepository.save(assignment);
        
        List<Map<String, Object>> questions = (List<Map<String,Object>>) assignmentData.get("questions");        
        for(Map<String, Object> q : questions) {
            AssignmentQuestion question = new AssignmentQuestion();
            question.setAssignmentId(assignment.getAssignmentId());
            question.setQuestionText((String) q.get("question"));
            question.setMarks((Integer)q.get("marks"));
            question.setBloomLevel((String) q.get("bloom_level"));
            
            // 🔥 THE FIX: Capture the Subjective Rubric/Answer from Python
            // We check a few common keys just in case Python names it differently
            String refAnswer = (String) q.get("ideal_answer");
            if (refAnswer == null) refAnswer = (String) q.get("reference_answer");
            if (refAnswer == null) refAnswer = (String) q.get("answer");
            if (refAnswer == null) refAnswer = "Subjective Evaluation / AI Rubric Pending"; // Safe fallback
            
            // NOTE: Make sure your AssignmentQuestion.java entity has this field and setter!
            question.setIdealAnswer(refAnswer); 
            
            assignmentQuestionRepository.save(question);
        }
        
        return assignment;
    }

    // --- 3. SUBMISSION LOGIC (Transactional & Secure) ---
    @Transactional
    public AssignmentAttempt submitAssignment(Long studentId, SubmitAssignmentRequest request) {
        List<AssignmentQuestion> questions = assignmentQuestionRepository.findByAssignmentId(request.getAssignmentId());

        if (questions.isEmpty()) { throw new RuntimeException("Assignment sequence not found"); }

        // Logic check to prevent double-submitting the same task
        boolean alreadySubmitted = assignmentAttemptRepository.existsByAssignmentIdAndStudentId(request.getAssignmentId(), studentId);
        if(alreadySubmitted) { throw new RuntimeException("Assignment already finalized by student"); }

        Map<Long, AssignmentQuestion> questionMap = questions.stream()
                                 .collect(Collectors.toMap(AssignmentQuestion::getQuestionId, q -> q));

        List<Map<String, Object>> questionPayload = new ArrayList<>();
        List<String> answerPayload = new ArrayList<>();

        for (StudentAnswerDto studentAnswer : request.getAnswers()) {
            AssignmentQuestion question = questionMap.get(studentAnswer.getQuestionId());
            if (question == null) { throw new RuntimeException("Question ID Mismatch"); }

            Map<String, Object> qMap = new HashMap<>();
            qMap.put("question", question.getQuestionText());
            qMap.put("marks", question.getMarks());
            questionPayload.add(qMap);
            answerPayload.add(studentAnswer.getAnswer());
        }

        Map<String, Object> flaskRequest = new HashMap<>();
        flaskRequest.put("questions", questionPayload);
        flaskRequest.put("answers", answerPayload);

        ResponseEntity<Map> response = restTemplate.postForEntity(getnlpEvaluateAssignment(), flaskRequest, Map.class);

        // Mapping NLP Scores to Java BigDecimal
        Number totalScoreNum = (Number) response.getBody().get("total_score");
        Number percentageNum = (Number) response.getBody().get("percentage");

        AssignmentAttempt attempt = new AssignmentAttempt();
        attempt.setAssignmentId(request.getAssignmentId());
        attempt.setStudentId(studentId);
        attempt.setTotalScore(BigDecimal.valueOf(totalScoreNum.doubleValue()));
        attempt.setPercentage(BigDecimal.valueOf(percentageNum.doubleValue()));
        attempt.setGrade(calculateGrade(attempt.getPercentage()));

        attempt = assignmentAttemptRepository.save(attempt);

        // Saving individual answer feedback from AI
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.getBody().get("results");
        for (int i = 0; i < request.getAnswers().size(); i++) {
            StudentAnswerDto studentAnswer = request.getAnswers().get(i);
            Map<String, Object> result = results.get(i);

            AssignmentAnswer answer = new AssignmentAnswer();
            answer.setAttemptId(attempt.getAttemptId());
            answer.setQuestionId(studentAnswer.getQuestionId());
            answer.setStudentAnswer(studentAnswer.getAnswer());
            answer.setScore(BigDecimal.valueOf(((Number) result.get("score")).doubleValue()));
            answer.setFeedback((String) result.get("feedback"));
            assignmentAnswerRepository.save(answer);
        }
        
        return attempt;
    }

    private String calculateGrade(BigDecimal percentage) {
        if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) return "A+";
        if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) return "A";
        if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) return "B";
        if (percentage.compareTo(BigDecimal.valueOf(60)) >= 0) return "C";
        if (percentage.compareTo(BigDecimal.valueOf(50)) >= 0) return "D";
        return "F";
    }
    
 // 🔥 Used for the Live Preview in AssessmentBuilderTab
    public AssignmentDetailsResponse getAssignmentById(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment record not found"));
        
        List<AssignmentQuestion> questions = assignmentQuestionRepository.findByAssignmentId(assignmentId);
        List<QuestionDto> questionDtos = questions.stream()
                // 🔥 THE FIX: Pass q.getIdealAnswer() into the DTO!
                .map( q -> new QuestionDto(q.getQuestionId(), q.getQuestionText(), q.getMarks(), q.getBloomLevel(), q.getIdealAnswer()))
                .toList();
                
        return new AssignmentDetailsResponse(assignment.getAssignmentId(), assignment.getCourseId(), assignment.getTotalMarks(), questionDtos);
    }

    // 🔥 Used for the Question Bank Tab
//    public AssignmentDetailsResponse getAssignmentById(Long assignmentId) {
//        Assignment assignment = assignmentRepository.findById(assignmentId)
//                .orElseThrow(() -> new RuntimeException("Assignment record not found"));
//        
//        List<AssignmentQuestion> questions = assignmentQuestionRepository.findByAssignmentId(assignmentId);
//        List<QuestionDto> questionDtos = questions.stream()
//                // 🔥 THE FIX: Pass q.getIdealAnswer() into the DTO!
//                .map( q -> new QuestionDto(q.getQuestionId(), q.getQuestionText(), q.getMarks(), q.getBloomLevel(), q.getIdealAnswer()))
//                .toList();
//                
//        return new AssignmentDetailsResponse(assignment.getAssignmentId(), assignment.getCourseId(), assignment.getTotalMarks(), questionDtos);
//    }
    
    List<Assignment> getAssignmentsByCourseIds(List<Long> courseIds) {
        return assignmentRepository.findByCourseIdIn(courseIds);
    }

	@Transactional
    public void publishAssignment(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Assignment not found"));
        
        assignment.setIsPublished(true);
        assignmentRepository.save(assignment);
        System.out.println("✅ Assignment " + assignmentId + " has been approved and published by the Teacher.");
    }
	
	public List<Map<String, Object>> getAllQuestionsForCourse(Long courseId) {
        List<Assignment> assignments = assignmentRepository.findByCourseId(courseId);
        List<Map<String, Object>> allQuestions = new java.util.ArrayList<>();

        for (Assignment assignment : assignments) {
            List<AssignmentQuestion> questions = assignmentQuestionRepository.findByAssignmentId(assignment.getAssignmentId());
            
            for (AssignmentQuestion q : questions) {
                java.util.Map<String, Object> qMap = new java.util.HashMap<>();
                qMap.put("questionId", q.getQuestionId());
                qMap.put("questionText", q.getQuestionText());
                qMap.put("marks", q.getMarks());
                qMap.put("bloomLevel", q.getBloomLevel());
                qMap.put("referenceAnswer", q.getIdealAnswer());
                
                // Attach parent data so React knows if it's a Draft!
                qMap.put("assignmentId", assignment.getAssignmentId());
                qMap.put("isPublished", assignment.getIsPublished()); 
                
                allQuestions.add(qMap);
            }
        }
        return allQuestions;
    }

	@Transactional
    public void deleteQuestion(Long questionId) {
        assignmentQuestionRepository.deleteById(questionId);
        System.out.println(" Deleted Assignment Question ID: " + questionId);
    }
	// 🔥 HITL: Save edits to a subjective question
    @Transactional
    public AssignmentQuestion updateQuestion(Long questionId, Map<String, Object> updates) {
        AssignmentQuestion existing = assignmentQuestionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
                
        // 1. Update the Question Text
        if (updates.containsKey("questionText")) {
            existing.setQuestionText((String) updates.get("questionText"));
        } else if (updates.containsKey("question")) {
            existing.setQuestionText((String) updates.get("question"));
        }

        // 2. Update the AI Grading Rubric (React sends 'referenceAnswer', DB uses 'idealAnswer')
        if (updates.containsKey("referenceAnswer")) {
            existing.setIdealAnswer((String) updates.get("referenceAnswer"));
        } else if (updates.containsKey("idealAnswer")) {
            existing.setIdealAnswer((String) updates.get("idealAnswer"));
        }
        
        // 3. Update Bloom Level
        if (updates.containsKey("bloomLevel")) {
            existing.setBloomLevel((String) updates.get("bloomLevel"));
        }
        
        // 4. Update Marks
        if (updates.containsKey("marks")) {
            Object marksObj = updates.get("marks");
            if (marksObj instanceof Integer) {
                existing.setMarks((Integer) marksObj);
            } else if (marksObj instanceof String) {
                existing.setMarks(Integer.parseInt((String) marksObj));
            }
        }
        
        return assignmentQuestionRepository.save(existing);
    }
}