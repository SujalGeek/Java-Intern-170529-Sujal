package com.becoder.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.becoder.model.User;
import com.becoder.repository.UserRepository;

@RestController
public class UserController {

	@Autowired
	private UserRepository userRepository;

	@PostMapping("/save")
	public ResponseEntity<?> saveUser(@RequestBody User user) {
		User saveUser = userRepository.save(user);
		return new ResponseEntity<>(saveUser, HttpStatus.CREATED);
	}

}
