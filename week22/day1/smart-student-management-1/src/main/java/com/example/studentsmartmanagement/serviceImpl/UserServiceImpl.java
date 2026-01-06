package com.example.studentsmartmanagement.serviceImpl; // Check your package naming!

import com.example.studentsmartmanagement.dto.LoginRequest;
import com.example.studentsmartmanagement.dto.LoginResponse;
import com.example.studentsmartmanagement.dto.RefreshTokenRequest;
import com.example.studentsmartmanagement.dto.UserDto;
import com.example.studentsmartmanagement.entity.Role;
import com.example.studentsmartmanagement.entity.Student;
import com.example.studentsmartmanagement.entity.Teacher;
import com.example.studentsmartmanagement.entity.User;
import com.example.studentsmartmanagement.repository.StudentRepository;
import com.example.studentsmartmanagement.repository.TeacherRepository;
import com.example.studentsmartmanagement.repository.UserRepository;
import com.example.studentsmartmanagement.service.JwtService;
import com.example.studentsmartmanagement.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional // Ensures if Profile creation fails, User is not saved
    public Boolean register(UserDto userDto) {
        
        // 1. Validation
        if (userRepository.exitsByUsername(userDto.getEmail())) {
            log.warn("Attempt to register with existing email: {}", userDto.getEmail());
            return false;
        }

        // 2. Map & Create User
        User user = modelMapper.map(userDto, User.class);
        user.setUsername(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // Encrypt!
        user.setActive(true);
        
        User savedUser = userRepository.save(user);

        // 3. Create Specific Profile
        if (userDto.getRole() == Role.STUDENT) {
            createStudentProfile(savedUser, userDto);
        } else if (userDto.getRole() == Role.TEACHER) {
            createTeacherProfile(savedUser, userDto);
        }

        log.info("User registered successfully: {}", user.getUsername());
        return true;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        log.info("Login attempt for: {}", loginRequest.getEmail());

        // 1. Authenticate (Checks Password internally)
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        // 2. Fetch User
        User user = userRepository.findByUsername(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // 3. Generate BOTH Tokens
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user); // Calls the method using the refresh secret

        // 4. Map User Details
        UserDto userDto = modelMapper.map(user, UserDto.class);

        // 5. Return Response
        return LoginResponse.builder()
                .accesstoken(accessToken)
                .refreshToken(refreshToken)
                .user(userDto)
                .build();
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // 1. Extract Username from Refresh Token
        String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            // 2. Fetch User
            User user = userRepository.findByUsername(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // 3. Validate Refresh Token
            if (jwtService.isTokenValid(refreshToken, user)) {
                
                // 4. Generate NEW Access Token only
                String newAccessToken = jwtService.generateToken(user);

                // 5. Return response (Keep same Refresh Token)
                return LoginResponse.builder()
                        .accesstoken(newAccessToken)
                        .refreshToken(refreshToken) 
                        .user(modelMapper.map(user, UserDto.class))
                        .build();
            }
        }
        throw new RuntimeException("Invalid Refresh Token");
    }

    // --- Helper Methods (Private) ---

    private void createStudentProfile(User user, UserDto dto) {
        Student student = Student.builder()
                .user(user)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .department(dto.getDepartment())
                .rollNumber("TEMP-" + System.currentTimeMillis())
                .currentSemester(1)
                .cgpa(0.0)
                .build();
        studentRepository.save(student);
    }

    private void createTeacherProfile(User user, UserDto dto) {
        Teacher teacher = Teacher.builder()
                .user(user)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .department(dto.getDepartment())
                .qualification(dto.getQualification())
                .build();
        teacherRepository.save(teacher);
    }

    @Override
    public void sendRestPasswordEmail(String email) {
        // Placeholder for now
        log.info("Sending reset email to {}", email);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        // Placeholder for now
        log.info("Resetting password using token {}", token);
    }
}