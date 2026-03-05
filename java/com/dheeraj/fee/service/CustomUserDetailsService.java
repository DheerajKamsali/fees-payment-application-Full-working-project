package com.dheeraj.fee.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dheeraj.fee.entity.Student;
import com.dheeraj.fee.repository.StudentRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	
	 private final StudentRepository repository;

	    public CustomUserDetailsService(
	            StudentRepository repository) {
	        this.repository = repository;
	    }

	    @Override
	    public UserDetails loadUserByUsername(String email)
	            throws UsernameNotFoundException {

	        Student student =
	                repository.findByEmail(email)
	                        .orElseThrow(
	                                () -> new UsernameNotFoundException("User not found"));

	        return org.springframework.security.core.userdetails.User
	                .withUsername(student.getEmail())
	                .password(student.getPassword())
	                .roles(student.getRole().replace("ROLE_", ""))
	                .build();
	    }
	}


