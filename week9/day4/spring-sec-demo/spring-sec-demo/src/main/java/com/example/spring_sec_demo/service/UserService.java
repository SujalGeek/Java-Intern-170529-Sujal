package com.example.spring_sec_demo.service;

import com.example.spring_sec_demo.model.User;
import com.example.spring_sec_demo.repositry.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public User saveUser(User user){

        return userRepo.save(user);
    }
}
