package com.example.chat_app_backend.controllers;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.chat_app_backend.entities.User;
import com.example.chat_app_backend.repository.UserRepository;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("http://localhost:5173")
public class AuthController {
	
	
	private final UserRepository userRespository;
	private final PasswordEncoder passwordEncoder;
	
	
	public AuthController(UserRepository userRespository,PasswordEncoder passwordEncoder)
	{
		this.userRespository=userRespository;
		this.passwordEncoder=passwordEncoder;
	}
	
	@PostMapping("/signup")
	public ResponseEntity<?> signup(@RequestParam("username") String username,
			@RequestParam("password") String password,
			@RequestParam("email") String email,
			@RequestParam(value = "file", required = false) MultipartFile file)
	{
		
		if(userRespository.existsByUsername(username))
		{
		
			return ResponseEntity.badRequest().body("Username already exists");	
		}
		
		if(userRespository.existsByEmail(email))
		{
			return ResponseEntity.badRequest().body("Email already registered");	
		}
		
		User user = new User();
		user.setUsername(username);
		user.setPassword(passwordEncoder.encode(password));
		user.setEmail(email);
		
		if(file != null && !file.isEmpty())
		try{
			{
				String contentType = file.getContentType();
				String base64Image = Base64.getEncoder().encodeToString(file.getBytes());	
			
				user.setProfilePic("data: "+contentType+";base64, "+base64Image);
			}
		}catch(IOException e)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading image");
		}
		
		else {
			user.setProfilePic("https://raw.githubusercontent.com/Ashwinvalento/cartoon-avatar/master/lib/images/male/45.png");
		}
		userRespository.save(user);
		return ResponseEntity.ok("User registered Successfully");
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody User loginRequest)
	{
		Optional<User> userOptional = userRespository.findByUsername(loginRequest.getUsername());
		
		if(userOptional.isPresent())
		{
			User user = userOptional.get();
			
			if(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()))
			{
				return ResponseEntity.ok(user);
			}
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
	}
}