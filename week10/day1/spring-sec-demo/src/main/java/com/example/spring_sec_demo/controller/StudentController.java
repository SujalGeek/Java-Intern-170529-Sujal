package com.example.spring_sec_demo.controller;

import com.example.spring_sec_demo.model.Student;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StudentController {

    List<Student> students = new ArrayList<>(List.of(
            new Student(1,"Sujal","Java"),
            new Student(2,"Guru","Python"),
            new Student(3,"Rohan","ML")

    ));


    @GetMapping("students")
    public List<Student> getStudents(){
        return students;
    }

    @GetMapping("csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest req)
    {
        return (CsrfToken) req.getAttribute("_csrf");
    }


    @PostMapping("students")
    public void addStudent(@RequestBody Student student)
    {
        students.add(student);
    }
}
