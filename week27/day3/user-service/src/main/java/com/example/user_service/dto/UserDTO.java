package com.example.user_service.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {
    private Long userId;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "Role is required (1=Admin, 2=Teacher, 3=Student)")
    private Integer role;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private Boolean isActive;

    // Teacher fields
    private String employeeId;
    private String department;
    private String officeLocation;
    private String phone;

    // Student fields
    private String studentNumber;
    private String major;
    private Integer year;
    private Integer semester;
    private Integer maxCoursesPerSemester;
}