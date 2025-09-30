package com.example.demo_jpa;

import com.example.demo_jpa.model.Student;
import com.example.demo_jpa.repo.StudentRepo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Optional;

@SpringBootApplication
public class DemoJpaApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DemoJpaApplication.class, args);

        StudentRepo repo = context.getBean(StudentRepo.class);

        Student s1 = new Student();
        s1.setRollNo(101);
        s1.setName("Sujal");
        s1.setMarks(60);

        Student s2 = new Student();
        s2.setRollNo(102);
        s2.setName("Dhruv");
        s2.setMarks(68);

        Student s3 = new Student();
        s3.setRollNo(103);
        s3.setName("Meet");
        s3.setMarks(75);

        repo.save(s1);
        repo.save(s2);
        repo.save(s3);

        System.out.println(repo.findAll());

        Optional<Student> s = repo.findById(102);
        System.out.println(s.orElse(new Student()));
        System.out.println(repo.findByName("Dhruv"));
    }


}
