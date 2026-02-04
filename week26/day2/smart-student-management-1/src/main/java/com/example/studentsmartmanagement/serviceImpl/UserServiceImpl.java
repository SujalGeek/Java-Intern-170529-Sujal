package com.example.studentsmartmanagement.serviceImpl;

import com.example.studentsmartmanagement.dto.LoginRequest;
import com.example.studentsmartmanagement.dto.LoginResponse;
import com.example.studentsmartmanagement.dto.RefreshTokenRequest;
import com.example.studentsmartmanagement.dto.UserDto;
import com.example.studentsmartmanagement.entity.Role;
import com.example.studentsmartmanagement.entity.Student;
import com.example.studentsmartmanagement.entity.Teacher;
import com.example.studentsmartmanagement.entity.User;
import com.example.studentsmartmanagement.exception.BusinessException;
import com.example.studentsmartmanagement.exception.ResourceNotFoundException;
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
    @Transactional
    public Boolean register(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getEmail())) {
            throw new BusinessException("Email already exists: " + userDto.getEmail());
        }

        User user = modelMapper.map(userDto, User.class);
        user.setUsername(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setActive(true);

        User savedUser = userRepository.save(user);

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

        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        User user = userRepository.findByUsername(loginRequest.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 🌟 THIS will now call the new generateToken(User) method in JwtService
        // which includes the ROLE!
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        UserDto userDto = modelMapper.map(user, UserDto.class);

        Long studentId = null;
        Long teacherId = null;

        if(user.getRole() == Role.STUDENT) {
            Student student = studentRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Student Profile Not Found"));
            studentId = student.getStudentId();
        } else if(user.getRole() == Role.TEACHER) {
            Teacher teacher = teacherRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher Not Found"));
            teacherId = teacher.getTeacherId();
        }

        return LoginResponse.builder()
                .accesstoken(accessToken)
                .refreshToken(refreshToken)
                .user(userDto)
                .studentId(studentId)
                .teacherId(teacherId)
                .build();
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            User user = userRepository.findByUsername(userEmail)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            if (jwtService.isTokenValid(refreshToken, user)) {
                // Generate new access token (will also include Role now)
                String newAccessToken = jwtService.generateToken(user);

                return LoginResponse.builder()
                        .accesstoken(newAccessToken)
                        .refreshToken(refreshToken)
                        .user(modelMapper.map(user, UserDto.class))
                        .build();
            }
        }
        throw new BusinessException("Invalid Refresh or Expired Token");
    }

    // --- Helper Methods ---
    private void createStudentProfile(User user, UserDto dto) {
        Student student = Student.builder()
                .user(user)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .department(dto.getDepartment())
                .rollNumber("TEMP-" + System.currentTimeMillis())
                .currentSemester(dto.getCurrentSemester() != null ? dto.getCurrentSemester() : 1)
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
        if (!userRepository.existsByUsername(email)) {
            throw new ResourceNotFoundException("User with email " + email + " not found.");
        }
        log.info("Sending reset email to {}", email);
    }

    @Override
    public void resetPassword(String token, String newPassword) {
        log.info("Resetting password using token {}", token);
    }
}