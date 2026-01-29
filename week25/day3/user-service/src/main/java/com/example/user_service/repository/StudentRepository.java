package com.example.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.user_service.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>{

	Optional<Student> findByUser_UserId(Long userId);
	
	Optional<Student> findByStudentNumber(String studentNumber);
	
	boolean existsByStudentNumber(String studentNumber);
}
