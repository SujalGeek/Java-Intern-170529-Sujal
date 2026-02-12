package com.example.exam_result_service.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.exam_result_service.dto.ExamSubmissionDTO;
import com.example.exam_result_service.entity.ExamResult;
import com.example.exam_result_service.exception.ResourceNotFoundException;
import com.example.exam_result_service.repository.ExamResultRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamResultService {

	private final ExamResultRepository examResultRepository;
	
	@Transactional
	public ExamResult saveResult(ExamSubmissionDTO dto)
	{
		ExamResult result = ExamResult.builder()

//				.studentId(dto.getStudentId())
//				.courseId(dto.getCourseId())
//				.question(dto.getQuestion())
//				.studentAnswer(dto.getStudentAnswer())
//				.referenceAnswer(dto.getReferenceAnswer())
//				.score(dto.getScore())
//				.feedback(dto.getFeedback())
//				.bloomLevel(dto.getBloomLevel())
//				.retrievalConfidence(dto.getRetrievalConfidence())
//				.retreivalMode(dto.getRetrievalMode())
				.build();
//		
		return examResultRepository.save(result);
	}
	
	public List<ExamResult> getByStudent(Long studentId)
	{
		List<ExamResult> results = examResultRepository.findByStudentId(studentId);
		
		if(results.isEmpty())
		{
			throw new ResourceNotFoundException("No Exam Results found for student: "+studentId);
		}
		return results;
	}
	
	public List<ExamResult> getByCourse(Long courseId)
	{
		List<ExamResult> results = examResultRepository.findByCourseId(courseId);
		
		if(results.isEmpty())
		{
			throw new ResourceNotFoundException("No exam results found for courses: "+ courseId);
		}
		return results;
	}
	
	public List<ExamResult> getByStudentIdAndCourse(Long studentId,Long courseId)
	{
		List<ExamResult> results = examResultRepository.findByStudentIdAndCourseId(studentId, courseId);
		
		if(results.isEmpty())
		{
			throw new ResourceNotFoundException("No results found for student and course");
		}
		return results;
		
	}
	
	
	
}
