package com.example.studentsmartmanagement.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.studentsmartmanagement.dto.RegisterRequest;
import com.example.studentsmartmanagement.entity.Role;
import com.example.studentsmartmanagement.entity.Student;
import com.example.studentsmartmanagement.entity.Teacher;
import com.example.studentsmartmanagement.entity.User;
import com.example.studentsmartmanagement.repository.StudentRepository;
import com.example.studentsmartmanagement.repository.TeacherRepository;
import com.example.studentsmartmanagement.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;
	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Transactional
	public String register(RegisterRequest request)
	{
		if(userRepository.exitsByUsername(request.getUsername()))
		{
			throw new RuntimeException("User already exits with the email!!");
		}
		
		User user = User.builder()
				.username(request.getUsername())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(request.getRole())
				.isActive(true)
				.build();
		
		if(request.getRole() == Role.STUDENT)
		{
			createStudentProfile(user,request);
		}
		else if(request.getRole() == Role.TEACHER)
		{
			createTeacherProfile(user,request);
		}
		else {
			userRepository.save(user);
		}
		return "User regsiter SuccessFully";
		
	}

	private void createTeacherProfile(User user, RegisterRequest request) {
		Teacher teacher = Teacher.builder()
				.user(user)
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.department(request.getDepartment())
				.build();
		
		teacherRepository.save(teacher);
	}

	private void createStudentProfile(User user, RegisterRequest request) {
		Student student = Student.builder()
				.user(user)
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.department(request.getDepartment())
				.rollNumber("TEMP-"+System.currentTimeMillis())
				.currentSemester(1)
				.cgpa(0.0)
				.build();
		
		studentRepository.save(student);
	}
	
}
