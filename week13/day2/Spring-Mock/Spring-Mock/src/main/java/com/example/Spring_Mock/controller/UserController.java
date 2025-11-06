package com.example.Spring_Mock.controller;


import com.example.Spring_Mock.model.User;
import com.example.Spring_Mock.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")

public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("{id}")
    public ResponseEntity<User> getUser(@PathVariable int id)
    {
        return ResponseEntity.ok(userService.getUserById(id));
    }
}
