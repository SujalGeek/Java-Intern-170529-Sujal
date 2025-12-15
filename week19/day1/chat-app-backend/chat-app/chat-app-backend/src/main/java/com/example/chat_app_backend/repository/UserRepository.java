package com.example.chat_app_backend.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.example.chat_app_backend.entities.User;

@Repository

public interface UserRepository extends MongoRepository<User, String>{

	Optional<User> findByUsername(String username);

	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
	
}
