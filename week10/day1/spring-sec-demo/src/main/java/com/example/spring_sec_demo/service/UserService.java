package com.example.spring_sec_demo.service;

import com.example.spring_sec_demo.model.User;
import com.example.spring_sec_demo.repositry.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public User saveUser(User user){
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return userRepo.save(user);
    }
}
