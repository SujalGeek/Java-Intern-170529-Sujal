package com.example.user_service.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.user_service.dto.UserDTO;
import com.example.user_service.entity.Student;
import com.example.user_service.entity.Teacher;
import com.example.user_service.entity.User;
import com.example.user_service.service.UserService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

   
    @GetMapping
    public ResponseEntity<?> getAllUsers(
            @RequestHeader("X-User-Role") Integer role) {

        if (role != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Admin access required");
        }

        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(
            @RequestHeader("X-User-Role") Integer role,
            @RequestHeader("X-User-Id") Long loggedUserId,
            @PathVariable Long id) {

        // Admin can view anyone
        if (role != 1 && !loggedUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied");
        }

        Optional<User> userOptional = userService.getUserById(id);

        return userOptional
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("message", "User not found")));
    }
 
    
    
    @GetMapping("/role/{targetRole}")
    public ResponseEntity<?> getUsersByRole(
            @RequestHeader("X-User-Role") Integer role,
            @PathVariable Integer targetRole) {

        if (role != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Admin access required");
        }

        return ResponseEntity.ok(userService.getUsersByRole(targetRole));
    }

//    @PostMapping
//    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO) {
//        try {
//            User user = userService.createUser(userDTO);
//            
//            Map<String, Object> response = new HashMap<>();
//            response.put("message", "User created successfully");
//            response.put("userId", user.getUserId());
//            response.put("username", user.getUsername());
//            response.put("role", user.getRole());
//            
//            return ResponseEntity.status(HttpStatus.CREATED).body(response);
//        } catch (RuntimeException e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Collections.singletonMap("message", e.getMessage()));
//        }
//    }
    
    @PostMapping
    public ResponseEntity<?> createUser(
            @RequestHeader("X-User-Role") Integer role,
            @Valid @RequestBody UserDTO userDTO) {

        if (role != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Admin access required");
        }

        User user = userService.createUser(userDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "User created successfully",
                        "userId", user.getUserId(),
                        "username", user.getUsername(),
                        "role", user.getRole()
                ));
    }

  
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @RequestHeader("X-User-Role") Integer role,
            @RequestHeader("X-User-Id") Long loggedUserId,
            @PathVariable Long id,
            @RequestBody UserDTO userDTO) {

        // Admin can update anyone
        // Others can update only themselves
        if (role != 1 && !loggedUserId.equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied");
        }

        User user = userService.updateUser(id, userDTO);

        return ResponseEntity.ok(Map.of(
                "message", "User updated successfully",
                "user", user
        ));
    }

  
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @RequestHeader("X-User-Role") Integer role,
            @PathVariable Long id) {
    	try {
        if (role != 1) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Admin access required");
        }

        userService.deleteUser(id);

        return ResponseEntity.ok(
                Collections.singletonMap("message", "User deleted successfully")
        );
    	}	
//            userService.deleteUser(id);
//            return ResponseEntity.ok(Collections.singletonMap("message", "User deleted successfully"));
         catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("message", e.getMessage()));
        }
    }
   
    
    @GetMapping("/teachers")
    public ResponseEntity<?> getAllTeachers(
    		@RequestHeader("X-User-Role") Integer role
    		) {
    	  if (role != 1) {
              return ResponseEntity.status(HttpStatus.FORBIDDEN)
                      .body("Admin access required");
          }

    	
        List<Teacher> teachers = userService.getAllTeachers();
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/students")
    public ResponseEntity<?> getAllStudents(
    		@RequestHeader("X-User-Role") Integer role
    		) {
    	
    	  if (role != 1) {
              return ResponseEntity.status(HttpStatus.FORBIDDEN)
                      .body("Admin access required");
          }

        List<Student> students = userService.getAllStudents();
        return ResponseEntity.ok(students);
    }
}