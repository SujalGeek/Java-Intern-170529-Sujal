package com.example.user_service.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.user_service.dto.UserDTO;
import com.example.user_service.entity.Student;
import com.example.user_service.entity.Teacher;
import com.example.user_service.entity.User;
import com.example.user_service.repository.StudentRepository;
import com.example.user_service.repository.TeacherRepository;
import com.example.user_service.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public List<User> getUsersByRole(Integer role) {
        return userRepository.findByRole(role);
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

  
    @Transactional
    public User createUser(UserDTO userDTO) {
        // Validation Checks
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 1. Create Base User
        User user = new User();
        user.setUsername(userDTO.getUsername());
        // 🔒 SECURITY: Hash the password before saving
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setEmail(userDTO.getEmail());
        user.setRole(userDTO.getRole());
        user.setFullName(userDTO.getFullName());
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        // 2. Create Specific Profile based on Role
        if (userDTO.getRole() == 2) { 
            // Role 2 = TEACHER
            Teacher teacher = new Teacher();
            teacher.setUser(savedUser); // Link strictly to User entity
            teacher.setEmployeeId(userDTO.getEmployeeId());
            teacher.setDepartment(userDTO.getDepartment());
            teacher.setOfficeLocation(userDTO.getOfficeLocation());
            teacher.setPhone(userDTO.getPhone());
            teacherRepository.save(teacher);

        } else if (userDTO.getRole() == 3) { 
            // Role 3 = STUDENT
            Student student = new Student();
            student.setUser(savedUser); 
            student.setStudentNumber(userDTO.getStudentNumber());
            student.setMajor(userDTO.getMajor());
            student.setYear(userDTO.getYear());
            student.setSemester(userDTO.getSemester());
            student.setMaxCoursePerSemester(userDTO.getMaxCoursesPerSemester() != null ? userDTO.getMaxCoursesPerSemester() : 3);
            studentRepository.save(student);
        }

        return savedUser;
    }


    @Transactional
    public User updateUser(Long userId, UserDTO userDTO) {
//        Optional<User> userOptional = userRepository.findById(userId);
//
//        if (userOptional.isEmpty()) {
//            throw new RuntimeException("User not found");
//        }
//
//        User user = userOptional.get();
//
//        // Update Email (Check for duplicates if changed)
//        if (userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail())) {
//            if (userRepository.existsByEmail(userDTO.getEmail())) {
//                throw new RuntimeException("Email already exists");
//            }
//            user.setEmail(userDTO.getEmail());
//        }
//
//        // Update Password (Only if a new one is provided)
//        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
//            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
//        }
//
//        // Update other fields
//        if (userDTO.getFullName() != null) {
//            user.setFullName(userDTO.getFullName());
//        }
//
//        if (userDTO.getIsActive() != null) {
//            user.setIsActive(userDTO.getIsActive());
//        }
//
//        // Note: Updating specific Student/Teacher fields (like Department) would go here 
//        // by fetching studentRepository.findByUser_UserId(userId) if needed.
//
//        return userRepository.save(user);
//    }
    User user = userRepository.findById(userId)
    		.orElseThrow(
    				() -> new RuntimeException("User not found")
    				);
    
    if(userDTO.getEmail() != null && !userDTO.getEmail().equals(user.getEmail()))
    {
    	if(userRepository.existsByEmail(userDTO.getEmail())) {
    		throw new RuntimeException("Email already Exits");
    	}
    	user.setEmail(userDTO.getEmail());
    }
    
    if(userDTO.getFullName() != null)
    {
    	user.setFullName(userDTO.getFullName());
    }
    if(userDTO.getPassword() != null && !userDTO.getPassword().isEmpty())
    {
    	user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
    }
    
    if(user.getRole() == 2)
    {
    	Teacher teacher = teacherRepository.findByUser(user)
    			.orElseThrow(
    					() -> new RuntimeException("Teacher profile not found")
    					);
    System.out.println("Updating teacher ID:"+ teacher.getTeacherId());
    System.out.println("New Employee ID: "+userDTO.getEmployeeId());
    
    if(userDTO.getEmployeeId() != null )
    {
    	teacher.setEmployeeId(userDTO.getEmployeeId());
    }
    if(userDTO.getDepartment() != null)
    {
    	teacher.setDepartment(userDTO.getDepartment());
    }
    if(userDTO.getOfficeLocation() != null)
    {
    	teacher.setOfficeLocation(userDTO.getOfficeLocation());
    }
    if(userDTO.getPhone() != null)
    {
    	teacher.setPhone(userDTO.getPhone());
    }
    	teacherRepository.saveAndFlush(teacher);
    }
    
    else if(user.getRole() == 3)
    {
    	Student student = studentRepository.findByUser(user)
    			.orElseThrow(
    					() -> new RuntimeException("Student profile not found"));

    	if(userDTO.getMajor() != null)
    	{
    		student.setMajor(userDTO.getMajor());
    	}
    	if(userDTO.getYear() != null)
    	{
    		student.setYear(userDTO.getYear());
    	}
    	if(userDTO.getSemester() != null)
    	{
    		student.setSemester(userDTO.getSemester());
    	}
    	if(userDTO.getMaxCoursesPerSemester() != null)
    	{
    		student.setMaxCoursePerSemester(userDTO.getMaxCoursesPerSemester());
    	}
    	
    	studentRepository.saveAndFlush(student);
    }
    
    return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        // Because of Cascade Type in DB, deleting User will auto-delete the Student/Teacher profile
        userRepository.deleteById(userId);
    }
}