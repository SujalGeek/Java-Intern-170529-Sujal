package com.example.studentsmartmanagement.serviceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.studentsmartmanagement.entity.Assignment;
import com.example.studentsmartmanagement.entity.Submission;
import com.example.studentsmartmanagement.repository.AssignmentRepository;
import com.example.studentsmartmanagement.repository.SubmissionRepository;
import com.example.studentsmartmanagement.service.StudentDashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentDashboardServiceImpl implements StudentDashboardService{
	
	private final AssignmentRepository assignmentRepository;
	private final SubmissionRepository submissionRepository;
	
	@Override
	public List<Map<String, Object>> getPendingExams(Long studentId, Long courseId) {
		List<Assignment> assignments = assignmentRepository.findPendingExamsForStudent(courseId,studentId);
		
		
		return assignments.stream().map( a-> {
		Map<String,Object> map = new HashMap<>();	
		map.put("assignmentId", a.getAssignmentId());
		map.put("title",a.getTitle());
		map.put("dueDate", a.getDueDate());
		map.put("totalMarks", a.getTotalMarks());
		map.put("teacherName", a.getTeacher() != null ? a.getTeacher().getFirstName() : "Unknown");
		return map;
		}).collect(Collectors.toList());
				
	}

	@Override
	public List<Map<String, Object>> getStudentResults(Long studentId) {
		List<Submission> submissions = submissionRepository.findByStudent_StudentId(studentId);
		
		return submissions.stream().map(s->{
			Map<String, Object> map = new HashMap<>();
			map.put("submissionId", s.getSubmissionId());
			map.put("examTitle", s.getAssignment().getTitle());
			map.put("dateTaken", s.getSubmissionDate());
			map.put("score", s.getGradeObtained());
			map.put("totalMarks", s.getAssignment().getTotalMarks());
			map.put("status", s.getStatus());
			
			return map;
		}).collect(Collectors.toList());
	
	}

}
