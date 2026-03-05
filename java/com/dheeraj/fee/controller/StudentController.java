package com.dheeraj.fee.controller;

import com.dheeraj.fee.entity.Student;
import com.dheeraj.fee.repository.StudentRepository;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/student")
public class StudentController {

    private final StudentRepository repository;

    public StudentController(StudentRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/me")
    public Student getStudent(Authentication authentication) {

        String email = authentication.getName();

        return repository.findByEmail(email).orElse(null);
    }
}