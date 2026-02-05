package com.example.studentsmartmanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.studentsmartmanagement.entity.Teacher;
import com.example.studentsmartmanagement.entity.User;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

	Optional<Teacher> findByUser_UserId(Long userId);

	Optional<Teacher> findByUser(User user);
	
}
