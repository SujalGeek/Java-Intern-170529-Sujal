package com.example.spring_sec_demo.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("hello")
    public String hello(){
        return "Hello World";
    }

    @GetMapping("about")
    public String about(HttpServletRequest req)
    {
        return "Sujal" + req.getSession().getId();
    }
}
