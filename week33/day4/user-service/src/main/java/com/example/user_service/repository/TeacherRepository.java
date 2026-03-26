package com.example.user_service.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.user_service.entity.Teacher;
import com.example.user_service.entity.User;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long>{

	Optional<Teacher> findByUser_UserId(Long userId);
	
    Optional<Teacher> findByEmployeeId(String employeeId);
    Optional<Teacher> findByUser(User user); 

    boolean existsByEmployeeId(String employeeId);
}
