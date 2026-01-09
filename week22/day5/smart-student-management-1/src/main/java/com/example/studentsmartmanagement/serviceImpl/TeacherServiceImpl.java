package com.example.studentsmartmanagement.serviceImpl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.studentsmartmanagement.dto.CourseRequest;
import com.example.studentsmartmanagement.dto.GradeRequest;
import com.example.studentsmartmanagement.entity.Course;
import com.example.studentsmartmanagement.entity.Enrollment;
import com.example.studentsmartmanagement.entity.Grade;
import com.example.studentsmartmanagement.entity.Teacher;
import com.example.studentsmartmanagement.repository.CourseRepository;
import com.example.studentsmartmanagement.repository.EnrollmentRepository;
import com.example.studentsmartmanagement.repository.GradeRepository;
import com.example.studentsmartmanagement.repository.TeacherRepository;
import com.example.studentsmartmanagement.service.TeacherService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherServiceImpl implements TeacherService{

	private final TeacherRepository teacherRepository;
	private final CourseRepository courseRepository;
	private final EnrollmentRepository enrollmentRepository;
	private final GradeRepository gradeRepository;
	
	@Override
	@Transactional
	public void addCourse(CourseRequest request) {
		// TODO Auto-generated method stub
		log.info("Request to add course: {} by Teacher ID: {}",request.getCourseName(),request.getTeacherId());
		
		Teacher teacher = teacherRepository.findById(request.getTeacherId())
				.orElseThrow(
						() -> new RuntimeException("Teacher not found with ID: "+ request.getTeacherId()));
						
		if(courseRepository.findByCourseCode(request.getCourseCode()).isPresent())
		{
			throw new RuntimeException("Course Code: "+request.getCourseCode());
		}
		
		Course course = Course.builder()
				.courseName(request.getCourseName())
				.courseCode(request.getCourseCode())
				.credits(request.getCredits())
				.teacher(teacher)
				.build();
		
		courseRepository.save(course);
		log.info("Course created Successfully:{}", course.getCourseCode());
				
	}

	@Override
	@Transactional
	public void assignGrade(GradeRequest request) {
		// TODO Auto-generated method stub
		
		log.info("Grading Enrollment ID: {} | Exam: {}",request.getEnrollmentId());
		
		Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
				.orElseThrow(
						() -> new RuntimeException("Enrollment not found Exception"));
	
	if(request.getScoreObtained() > request.getMaxScore())
	{
		throw new RuntimeException("Score obtained cannot be greater than Max Score");
	}
	
	Grade grade = Grade.builder()
			.enrollment(enrollment)
			.examType(request.getExamType())
			.scoreObtained(request.getScoreObtained())
			.maxScore(request.getMaxScore())
			.feedback(request.getFeedback())
			.build();
	
	gradeRepository.save(grade);
	log.info("Grade assigned Successfully for student ID: {}",enrollment.getStudent().getStudentId());
	}

}
