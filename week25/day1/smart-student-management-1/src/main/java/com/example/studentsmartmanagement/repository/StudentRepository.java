package com.example.studentsmartmanagement.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.studentsmartmanagement.entity.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long>{

	Optional<Student> findByRollNumber(String rollNumber);
	
	Optional<Student> findByUser_UserId(Long userId);
	
	@Query("select s from Student s where "+
			"lower(s.user.username) like lower(concat('%', :keyword, '%')) or " +
			"lower(s.firstName) like lower(concat('%', :keyword, '%')) or " +
			"lower(s.lastName) like lower(concat('%', :keyword, '%'))")
	List<Student> searchStudent(@Param("keyword") String keyword);
}
