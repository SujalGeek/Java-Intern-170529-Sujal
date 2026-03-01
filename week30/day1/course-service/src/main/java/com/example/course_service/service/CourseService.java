package com.example.course_service.service;

import java.io.IOException;
import java.util.List; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.course_service.dto.CourseDto;
import com.example.course_service.dto.CourseRequestDTO;
import com.example.course_service.dto.CourseResponseDTO;
import com.example.course_service.entity.Course;
import com.example.course_service.repository.AssignmentRepository;
import com.example.course_service.repository.CourseRepository;
import com.example.course_service.repository.EnrollmentRepository;
import com.example.course_service.repository.SubmissionRepository;
import com.example.course_service.util.MultipartInputStreamFileResource;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseService {

	private final CourseRepository courseRepository;
	
	private final RestTemplate restTemplate;
	
	@Value("${hybrid.nlp.url}")
	private String hybridNlpUrl;
	
	public String gethybridNlpUrl() {
		return hybridNlpUrl + "/index-book";
	}
	
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
	public CourseResponseDTO createCourse(Long teacherId, CourseRequestDTO request) {

	    if (courseRepository.existsByCourseCode(request.getCourseCode())) {
	        throw new RuntimeException("Course code already exists");
	    }

	    Course course = new Course();
	    course.setCourseCode(request.getCourseCode());
	    course.setCourseName(request.getCourseName());
	    course.setTeacherId(teacherId);
	    course.setSemester(request.getSemester());
	    course.setYear(request.getYear());

	    course.setCredits(
	        request.getCredits() != null ? request.getCredits() : 3
	    );

	    course.setMaxStudents(
	        request.getMaxStudents() != null ? request.getMaxStudents() : 50
	    );

	    course.setDescription(request.getDescription());
	    course.setIsActive(true);
	    course.setIsIndexed(false);

	    Course saved = courseRepository.save(course);

	    return mapToResponse(saved);
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

	public List<Course> getCoursesBySemester(Integer semester, Integer year) {
		
		return courseRepository.findBySemesterAndYear(semester,year);
	}
	
	public void uploadAndIndexBook(Long courseId, MultipartFile file) throws IOException {

	    Course course = courseRepository.findById(courseId)
	        .orElseThrow(() -> new RuntimeException("Course not found"));

	    MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
	    body.add("file", new MultipartInputStreamFileResource(
	            file.getInputStream(),
	            file.getOriginalFilename()
	    ));
	    body.add("course_id", courseId.toString());

	    HttpHeaders headers = new HttpHeaders();
	    headers.setContentType(MediaType.MULTIPART_FORM_DATA);

	    HttpEntity<MultiValueMap<String, Object>> requestEntity =
	            new HttpEntity<>(body, headers);

	    restTemplate.postForObject(
	    		gethybridNlpUrl(),
	            requestEntity,
	            String.class
	    );

	    course.setIsIndexed(true);
	    courseRepository.save(course);
	}
	
	private CourseResponseDTO mapToResponse(Course course) {

	    return CourseResponseDTO.builder()
	            .courseId(course.getCourseId())
	            .courseCode(course.getCourseCode())
	            .courseName(course.getCourseName())
	            .teacherId(course.getTeacherId())
	            .semester(course.getSemester())
	            .year(course.getYear())
	            .credits(course.getCredits())
	            .maxStudents(course.getMaxStudents())
	            .description(course.getDescription())
	            .isActive(course.getIsActive())
	            .isIndexed(course.getIsIndexed())
	            .createdAt(course.getCreatedAt())
	            .build();
	}
}
