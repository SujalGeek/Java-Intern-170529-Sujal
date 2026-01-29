package com.example.course_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.course_service.dto.CourseDto;
import com.example.course_service.entity.Course;
import com.example.course_service.repository.AssignmentRepository;
import com.example.course_service.repository.CourseRepository;
import com.example.course_service.repository.EnrollmentRepository;
import com.example.course_service.repository.SubmissionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

	private final CourseRepository courseRepository;
	
	public List<Course> getAllCourses(){
		List<Course> courses = courseRepository.findAll();
		
		if(courses.isEmpty())
		{
			throw new RuntimeException("No courses found in the database.");
		}
		
		return courses;
	}
	
	public Course getCourseById(Long courseId)
	{
		return courseRepository.findById(courseId)
				.orElseThrow(
						() -> new RuntimeException("Course with ID "+ courseId + "not found."));
	}
	
	public List<Course> getCourseByTeacher(Long teacherId)
	{
		List<Course> courses = courseRepository.findByTeacherId(teacherId);
		
		if(courses.isEmpty())
		{
			throw new RuntimeException("No courses found for Teacher ID: "+ teacherId);
		}
		return courses;
	}
	
	
	@Transactional
	public Course createCourse(CourseDto dto)
	{
		if(courseRepository.existsByCourseCode(dto.getCourseCode()))
		{
			throw new RuntimeException("Course Code " + dto.getCourseCode() + " already exits");
		}
		
		
		Course course = new Course();
		course.setCourseCode(dto.getCourseCode());
		course.setCourseName(dto.getCourseName());
		course.setTeacherId(dto.getTeacherId());
		course.setSemester(dto.getSemester());
		course.setYear(dto.getYear());
		course.setDescription(dto.getDescription());
		
		course.setMaxStudents(dto.getMaxStudents() != null ? dto.getMaxStudents() : 50);
		course.setCredits(dto.getCredits() != null ? dto.getCredits() : 3);
		course.setIsActive(true);
		
		return courseRepository.save(course);
	}
	
	@Transactional
	public Course updateCourse(Long courseId,CourseDto dto)
	{
		Course course = getCourseById(courseId);
		
		if(dto.getCourseName() != null)
		{
			course.setCourseName(dto.getCourseName());
		}
		if(dto.getTeacherId() != null)
		{
			course.setTeacherId(dto.getTeacherId());
		}
		
		if (dto.getSemester() != null) course.setSemester(dto.getSemester());
        if (dto.getYear() != null) course.setYear(dto.getYear());
        if (dto.getMaxStudents() != null) course.setMaxStudents(dto.getMaxStudents());
        if (dto.getCredits() != null) course.setCredits(dto.getCredits());
        if (dto.getDescription() != null) course.setDescription(dto.getDescription());
        if (dto.getIsActive() != null) course.setIsActive(dto.getIsActive());
	
        return courseRepository.save(course);
	}
	
	public void deleteCourse(Long courseId)
	{
		if(!courseRepository.existsById(courseId))
		{
			throw new RuntimeException("Cannot delete: Course Id "+ courseId + " does not exist.");
		}
		
		courseRepository.deleteById(courseId);
	}
	
}
