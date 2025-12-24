package com.example.enotes.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.enotes.dto.LoginRequest;
import com.example.enotes.dto.LoginResponse;
import com.example.enotes.dto.UserDto;
import com.example.enotes.entities.User;
import com.example.enotes.repository.UserRepository;
import com.example.enotes.service.JwtService;
import com.example.enotes.service.UserService;
import com.example.enotes.util.CommonUtil;

@RestController
@RequestMapping("/api/v1/user")
public class AuthController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired 
	private JwtService jwtService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody UserDto userDto)
	{
		Boolean register = userService.register(userDto);
		
		if(register)
		{
			return CommonUtil.createBuildResponseMessage("Register Success", HttpStatus.CREATED);
		}
		return CommonUtil.createErrorResponseMessage("Register failed", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) throws Exception {
	    try {
	        authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
	        );
	    } catch (BadCredentialsException e) {
	        // FIX: You MUST 'return' here, otherwise code continues!
	        return CommonUtil.createErrorResponseMessage("Invalid Email or Password", HttpStatus.BAD_REQUEST);
	    }

	    // Code only reaches here if password is CORRECT
	    UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
	    String token = jwtService.generateToken(userDetails);
	    
	    User user = userRepository.findByEmail(loginRequest.getEmail());
	    UserDto userDto = modelMapper.map(user, UserDto.class);
	    
	    LoginResponse response = LoginResponse.builder()
	            .user(userDto)
	            .token(token)
	            .build();
	    
	    return CommonUtil.createBuildResponse(response, HttpStatus.OK);
	}
}
