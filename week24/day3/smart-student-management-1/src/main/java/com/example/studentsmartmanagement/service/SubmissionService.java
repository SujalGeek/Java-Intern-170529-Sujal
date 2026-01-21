package com.example.studentsmartmanagement.service;


import com.example.studentsmartmanagement.dto.ExamSubmissionDto;
import com.example.studentsmartmanagement.entity.Submission;


public interface SubmissionService {

	public Submission submitExam(ExamSubmissionDto dto);		

	
}
