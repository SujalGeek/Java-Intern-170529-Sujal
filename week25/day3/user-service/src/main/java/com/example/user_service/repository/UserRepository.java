package com.example.user_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.user_service.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUserName(String username);
	
	Optional<User> findByEmail(String email);
	
	List<User> findByRole(Integer role);
	
	boolean existsByUsername(String username);
	
	boolean existsByEmail(String email);
}
