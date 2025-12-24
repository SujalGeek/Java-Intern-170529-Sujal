package com.example.enotes.serviceImpl;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // IMPORT THIS
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.example.enotes.dto.UserDto;
import com.example.enotes.entities.AccountStatus;
import com.example.enotes.entities.Role;
import com.example.enotes.entities.User;
import com.example.enotes.repository.RoleRepository;
import com.example.enotes.repository.UserRepository;
import com.example.enotes.service.EmailService;
import com.example.enotes.service.UserService;
import com.example.enotes.util.Validation;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private Validation validation;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder; // 1. Inject Password Encoder

    @Autowired
    private EmailService emailService; // 2. Inject Email Service

    @Override
    public Boolean register(UserDto userDto) {
        // Step 1: Validate Input
        validation.userValidation(userDto);

        // Step 2: Map DTO to Entity
        User user = modelMapper.map(userDto, User.class);

        // Step 3: Set Roles
        setRole(userDto, user);

        // Step 4: Encrypt Password (CRITICAL FOR LOGIN)
        // This converts "pass123" -> "$2a$10$..."
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Step 5: Create Account Status (For Email Verification)
        AccountStatus status = AccountStatus.builder()
                .isActive(false) // Account is locked initially
                .verficationCode(UUID.randomUUID().toString()) // Generate unique code
                .build();
        
        user.setStatus(status);

        // Step 6: Save to Database
        User saveUser = userRepository.save(user);

        // Step 7: Send Verification Email
        if (!ObjectUtils.isEmpty(saveUser)) {
            sendVerificationEmail(saveUser); // Call helper method
            return true;
        }
        return false;
    }

    private void setRole(UserDto userDto, User user) {
        List<Integer> reqRoleId = userDto.getRoles().stream().map(r -> r.getId()).toList();
        List<Role> roles = roleRepository.findAllById(reqRoleId);
        user.setRoles(roles);
    }

    private void sendVerificationEmail(User user) {
        String code = user.getStatus().getVerficationCode();
        // Adjust port if needed
        String verifyUrl = "http://localhost:8080/api/v1/home/verify?userId=" + user.getId() + "&code=" + code;

        String message = "<h1>Welcome, " + user.getFirstName() + "!</h1>"
                + "<p>Please verify your account by clicking the link below:</p>"
                + "<a href='" + verifyUrl + "'>Verify Account</a>";

        // Send using the EmailService we created earlier
        emailService.sendEmail(user.getEmail(), "Verify your E-Notes Account", message);
    }
}