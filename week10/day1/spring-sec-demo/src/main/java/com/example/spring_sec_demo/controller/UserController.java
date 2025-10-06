package com.example.spring_sec_demo.controller;

import com.example.spring_sec_demo.model.User;
import com.example.spring_sec_demo.service.JwtService;
import com.example.spring_sec_demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

    @PostMapping("register")
    public User register(@RequestBody User user)
    {

        return userService.saveUser(user);
    }

    @PostMapping("login")
    public String login(@RequestBody User user)
    {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(),user.getPassword()));

        if(authentication.isAuthenticated())
        {
            return jwtService.generateToken(user.getUsername());
        }
        else{
            return "login failed";
        }


    }
}
