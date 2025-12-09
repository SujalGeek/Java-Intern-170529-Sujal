package com.example.user_service.controller;

import com.example.user_service.entity.Score;
import com.example.user_service.entity.User;
import com.example.user_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 1. Manage Users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PutMapping("/users/promote/{id}")
    public ResponseEntity<String> promoteUser(@PathVariable Long id) {
        adminService.promoteToTeacher(id);
        return ResponseEntity.ok("User promoted to Teacher");
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }

    // 2. View Scores
    @GetMapping("/scores/quiz/{quizId}")
    public ResponseEntity<List<Score>> getScoresByQuiz(@PathVariable Integer quizId) {
        return ResponseEntity.ok(adminService.getScoresByQuiz(quizId));
    }

    @GetMapping("/scores/user/{userId}")
    public ResponseEntity<List<Score>> getScoresByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.getScoresByUser(userId));
    }
}