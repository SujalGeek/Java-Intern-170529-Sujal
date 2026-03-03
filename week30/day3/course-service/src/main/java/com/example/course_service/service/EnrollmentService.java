package com.example.course_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.course_service.dto.EnrollmentDto;
import com.example.course_service.entity.Course;
import com.example.course_service.entity.Enrollment;
import com.example.course_service.repository.CourseRepository;
import com.example.course_service.repository.EnrollmentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

	private final EnrollmentRepository enrollmentRepository;
	
	private final CourseRepository courseRepository;
	
	public List<Enrollment> getStudentEnrollment(Long studentId)
	{
		List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
		if(enrollments.isEmpty())
		{
			throw new RuntimeException("No enrollments found for Student Id: "+ studentId);
		}
	
		return enrollments;
	}
	
	
	@Transactional
	public Enrollment enrollStudent(Long studentId, Long courseId)
	{
		Course course = courseRepository.findById(courseId).orElseThrow(
				() -> new RuntimeException("Cannot enroll: Course ID"+ courseId));
		
		if(enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId).isPresent())
		{
			throw new RuntimeException("Student is already enrolled in this course");
		}
		
		long activeEnrollments = enrollmentRepository.countByStudentIdAndStatus(studentId, Enrollment.Status.ACTIVE);
			
		if(activeEnrollments >= 3)
		{
			throw new RuntimeException("Enrollment Limit Reached (Max 3 courses).");
		}
		
		
		long currentClassSize = enrollmentRepository.countByCourseIdAndStatus(courseId, Enrollment.Status.ACTIVE);
		
		if(currentClassSize >= course.getMaxStudents())
				{
				throw new RuntimeException("Course Full: Capacity is" + course.getMaxStudents());
				}
		
		Enrollment enrollment = new Enrollment();
		enrollment.setStudentId(studentId);
		enrollment.setCourseId(courseId);
		enrollment.setStatus(Enrollment.Status.ACTIVE);
//		enrollment.setEnrollmentDate(Date));
		return enrollmentRepository.save(enrollment);
		
	}
	
	public Enrollment updateStatus(Long enrollmentId,Enrollment.Status status)
	{
		Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
				.orElseThrow(
						() -> new RuntimeException("Enrollment Id:"+ enrollmentId + " not found")
						);
		
		enrollment.setStatus(status);
		
		return enrollmentRepository.save(enrollment);
	}
	
	
	
	@Transactional
	public void dropCourse(Long enrollmentId)
	{
		Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
				.orElseThrow(
						() -> new RuntimeException("Enrollment not found"));
		enrollment.setStatus(Enrollment.Status.DROPPED);
		
		enrollmentRepository.save(enrollment);
	}
	
}
