package com.example.course_service.service;

import org.springframework.stereotype.Service;

import com.example.course_service.dto.SubmissionDto;
import com.example.course_service.entity.Submission;
import com.example.course_service.repository.AssignmentRepository;
import com.example.course_service.repository.SubmissionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubmissionService {

	private final SubmissionRepository submissionRepository;
	
	private final AssignmentRepository assignmentRepository;
	
	
	@Transactional
	public Submission submitAssignment(SubmissionDto dto)
	{
		if(!assignmentRepository.existsById(dto.getAssignmentId()))
		{
			throw new RuntimeException("Assignment Id " + dto.getAssignmentId()+ " not found");
		}
		
		
		if(submissionRepository.findByAssignmentIdAndStudentId(dto.getAssignmentId(), dto.getStudentId()).isPresent())
		{
			throw new RuntimeException("Duplicate: You have already submitted this assignment.");
		}
		
		
		Submission sub = new Submission();
		sub.setAssignmentId(dto.getAssignmentId());
		sub.setStudentId(dto.getStudentId());
		sub.setStudentAnswer(dto.getStudentAnswer());
		sub.setStatus(Submission.Status.PENDING);
		
		return submissionRepository.save(sub);
	}
	
	
	@Transactional
	public Submission gradeSubmission(Long submissionId,Double grade,String feedback)
	{
		Submission sub = submissionRepository.findById(submissionId).orElseThrow(
				() -> new RuntimeException("Grading Failed: Submission Id" + submissionId + "not found.")
				);
		
		sub.setGradeObtained(grade);
		sub.setAiFeedback(feedback);
		sub.setStatus(Submission.Status.GRADED);
		
		return submissionRepository.save(sub);
	}
}
