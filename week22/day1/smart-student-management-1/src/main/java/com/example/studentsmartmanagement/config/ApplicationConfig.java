package com.example.studentsmartmanagement.config;

import org.modelmapper.ModelMapper; // Ensure you have the dependency or see note below
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.studentsmartmanagement.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {


	private final UserRepository userRepository;

	/*
	 * UserDetailsService: The logic to fetch user from DB
       We use a Lambda to look up the user by email in our Repository.
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}
	
	
	/*
	 * AuthenticationProvider: The Data Access Object (DAO) responsible for fetching
       UserDetails and encoding passwords to check if they match.
	 */
	
	@Bean
	public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService()); 
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
	
	// 3. AuthenticationManager: The "Boss" that actually processes the login request.
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}
	
	// 4. PasswordEncoder: The tool to encrypt passwords (BCrypt is standard).
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
    public ModelMapper  modelMapper() {
        return new ModelMapper();
    }
	
	
	
}
