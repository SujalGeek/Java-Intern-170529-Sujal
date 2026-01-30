package com.example.studentsmartmanagement.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.studentsmartmanagement.dto.CourseResponseDto;
import com.example.studentsmartmanagement.entity.Course;
import com.example.studentsmartmanagement.exception.BusinessException;
import com.example.studentsmartmanagement.repository.CourseRepository;
import com.example.studentsmartmanagement.service.CourseService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

	private final CourseRepository courseRepository;

	@Override
	@Transactional(readOnly = true)
	public List<CourseResponseDto> getAllCourses() {
//		return courseRepository.findAll();
		List<Course> courses = courseRepository.findAll();
		return courses.stream().map(this::mapToDto).collect(Collectors.toList());
	}
	
	private CourseResponseDto mapToDto(Course course)
	{
		String tName = "Unknown";
		if(course.getTeacher() != null)
		{
			tName = course.getTeacher().getFirstName() + " " + course.getTeacher().getLastName();
		}
		return CourseResponseDto.builder()
				.courseId(course.getCourseId())
				.courseName(course.getCourseName())
				.courseCode(course.getCourseCode())
				.credits(course.getCredits())
				.teacherName(tName)
				.build();
		
	}

	@Override
	@Transactional(readOnly = true)
	public List<Course> getCoursesByTeacher(Long teacherId) {

	if(teacherId == null)
	{
		throw new BusinessException("Teacher Id must not be null");
	}
	return courseRepository.findByTeacher_TeacherId(teacherId);
		
	}

	@Override
	public Course createCourse(Course course) {
	
		if(course.getCourseName() == null || course.getCourseName().trim().isEmpty())
		{
			throw new BusinessException("Course Name is not Empty");
		}
		
		if(course.getCourseCode() ==  null || course.getCourseCode().trim().isEmpty())
		{
			throw new BusinessException("Course Code cannot be empty.");
		}
		
		
		Optional<Course> existingCourse = courseRepository.findByCourseCode(course.getCourseCode());
		
		if(existingCourse.isPresent())
		{
			throw new BusinessException("Course with code "+ course.getCourseCode() + "already exits");
		}
		
		if(course.getCredits() == null || course.getCredits() < 1)
		{
			throw new BusinessException("Course must have at least 1 credit");
		}
		
		return courseRepository.save(course);
	}
	
	
}
