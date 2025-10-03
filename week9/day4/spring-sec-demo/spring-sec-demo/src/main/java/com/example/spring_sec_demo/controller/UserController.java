package com.example.spring_sec_demo.controller;

import com.example.spring_sec_demo.model.User;
import com.example.spring_sec_demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    @PostMapping("register")
    public User register(@RequestBody User user)
    {
        user.setPassword(encoder.encode(user.getPassword()));
        System.out.println(user.getPassword());
        return userService.saveUser(user);
    }
}
