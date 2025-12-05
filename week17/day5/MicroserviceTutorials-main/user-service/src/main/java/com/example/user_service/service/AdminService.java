package com.example.user_service.service;

import com.example.user_service.entity.Role;
import com.example.user_service.entity.Score;
import com.example.user_service.entity.User;
import com.example.user_service.repository.ScoreRepository;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void promoteToTeacher(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(Role.TEACHER);
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public List<Score> getScoresByQuiz(Integer quizId) {
        return scoreRepository.findByQuizId(quizId);
    }

    public List<Score> getScoresByUser(Long userId) {
        return scoreRepository.findByUserId(userId);
    }
}