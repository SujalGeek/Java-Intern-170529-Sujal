package com.example.studentsmartmanagement.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.studentsmartmanagement.dto.CourseResponseDto;
import com.example.studentsmartmanagement.dto.StudentDto;
import com.example.studentsmartmanagement.entity.Course;
import com.example.studentsmartmanagement.entity.Enrollment;
import com.example.studentsmartmanagement.entity.EnrollmentStatus;
import com.example.studentsmartmanagement.entity.Student;
import com.example.studentsmartmanagement.exception.BusinessException;
import com.example.studentsmartmanagement.exception.ResourceNotFoundException;
import com.example.studentsmartmanagement.repository.CourseRepository;
import com.example.studentsmartmanagement.repository.EnrollmentRepository;
import com.example.studentsmartmanagement.repository.StudentRepository;
import com.example.studentsmartmanagement.service.StudentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService{

	private final StudentRepository studentRepository;
	private final CourseRepository courseRepository;
	private final EnrollmentRepository enrollmentRepository;
	
	
	
	@Override
	@Transactional
	public Long enrollmentCourse(Long studentId, Long courseId) {
		// TODO Auto-generated method stub
		Student student = studentRepository.findById(studentId)
				.orElseThrow(
						() -> new ResourceNotFoundException("Student Not Found with ID: "+ studentId));
		
		Course course = courseRepository.findById(courseId)
				.orElseThrow(
						() -> new ResourceNotFoundException("Course Not Found with Id: "+ courseId));
				
		if(enrollmentRepository.findByStudent_StudentIdAndCourse_CourseId(studentId, courseId).isPresent())
		{
			throw new BusinessException("Student is already enrolled in this course");
		}
	
		Enrollment enrollment = Enrollment.builder()
				.student(student)
				.course(course)
				.semester(student.getCurrentSemester())
				.status(EnrollmentStatus.ACTIVE)
				.build();
		
		Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
		
		return savedEnrollment.getEnrollmentId();
	
	}



	@Override
	public List<StudentDto> searchStudents(String keyword) {
		
		List<Student> students = studentRepository.searchStudent(keyword);
		
		return students.stream().map(this::mapToDto).collect(Collectors.toList());
	}
	
	private StudentDto mapToDto(Student student) {
	StudentDto dto = new StudentDto();
	dto.setStudentId(student.getStudentId());
	dto.setFirstName(student.getFirstName());
	dto.setLastName(student.getLastName());
	
	if(student.getUser() != null)
	{
		dto.setEmail(student.getUser().getUsername());
	}
	dto.setDepartment(student.getDepartment());
	dto.setCurrentSemester(student.getCurrentSemester());
	return dto;
	}



	@Override
	public List<CourseResponseDto> getEnrolledCourses(Long studentId) {

		List<Enrollment> enrollments = enrollmentRepository.findByStudent_StudentId(studentId);
		
		return enrollments.stream()
				.map(
					enrollment ->{
						Course course = enrollment.getCourse();
						
						String teacherName = (course.getTeacher() != null)
								? course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName()
								: "Unknown";
					
					return CourseResponseDto.builder()
							.courseId(course.getCourseId())
							.courseName(course.getCourseName())
							.courseCode(course.getCourseCode())
							.credits(course.getCredits())
							.teacherName(teacherName)
							.build();
					}).collect(Collectors.toList());
					
			}
	

}
