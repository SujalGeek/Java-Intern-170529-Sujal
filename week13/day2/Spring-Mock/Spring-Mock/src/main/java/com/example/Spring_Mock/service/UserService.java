package com.example.Spring_Mock.service;

import com.example.Spring_Mock.model.User;
import com.example.Spring_Mock.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;



    public User getUserById(int id){
        return userRepository.findById(id).orElseThrow(()-> new RuntimeException("User not found"));
    }
}
