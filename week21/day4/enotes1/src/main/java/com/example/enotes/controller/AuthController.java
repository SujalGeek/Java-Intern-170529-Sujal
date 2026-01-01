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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.enotes.dto.LoginRequest;
import com.example.enotes.dto.LoginResponse;
import com.example.enotes.dto.PasswordResetRequest;
import com.example.enotes.dto.UserDto;
import com.example.enotes.endpoint.AuthEndpoint;
import com.example.enotes.entities.User;
import com.example.enotes.repository.UserRepository;
import com.example.enotes.service.JwtService;
import com.example.enotes.service.UserService;
import com.example.enotes.util.CommonUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AuthController implements AuthEndpoint {
	
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
	
	
	
	@Override
	public ResponseEntity<?> registerUser(UserDto userDto)
	{
		log.info("AuthController : registerUser(): Execution Start");
		
		Boolean register = userService.register(userDto);
		
		if(register)
		{
			log.info("AuthController : registerUser() : Exceution End");
			return CommonUtil.createBuildResponseMessage("Register Success", HttpStatus.CREATED);
		}
		log.info("Error: {}","Registered failed");
		return CommonUtil.createErrorResponseMessage("Register failed", HttpStatus.INTERNAL_SERVER_ERROR);
	}
	

	@Override
	public ResponseEntity<?> login(LoginRequest loginRequest) throws Exception {
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
	
	
	@Override
	public ResponseEntity<?> forgotPassword(String email)
	{
		userService.sendResetPasswordEmail(email);
		
		return CommonUtil.createBuildResponseMessage("If Email exits, reset link sent", HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<?> resetPassword(PasswordResetRequest request)
	{
		try {
			userService.resetPassword(request.getToken(), request.getNewPassword());
			return CommonUtil.createBuildResponseMessage("Password Reset Successfully",HttpStatus.OK);
		}
		catch(Exception e)
		{
		
			return CommonUtil.createErrorResponseMessage(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
	
	
}
