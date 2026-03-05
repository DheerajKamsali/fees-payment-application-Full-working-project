package com.dheeraj.fee.controller;

import com.dheeraj.fee.entity.Student;
import com.dheeraj.fee.repository.StudentRepository;
import com.dheeraj.fee.service.EmailService;
import com.dheeraj.fee.service.JwtUtil;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final StudentRepository repository;
    private final BCryptPasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    private Map<String,String> otpStorage = new HashMap<>();
    private Map<String,Boolean> verifiedEmails = new HashMap<>();

    // password reset token storage
    private Map<String,String> resetTokens = new HashMap<>();
    private Map<String, LocalDateTime> tokenExpiry = new HashMap<>();

    public AuthController(StudentRepository repository,
                          BCryptPasswordEncoder encoder,
                          JwtUtil jwtUtil,
                          EmailService emailService) {
        this.repository = repository;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;
    }

    // SEND OTP
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email){

        if(repository.findByEmail(email).isPresent()){
            return ResponseEntity.badRequest().body("Email already registered!");
        }

        String otp = String.valueOf((int)(Math.random()*900000)+100000);

        otpStorage.put(email,otp);

        emailService.sendOtp(email,otp);

        return ResponseEntity.ok("OTP Sent Successfully");
    }

    // VERIFY OTP
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String email,
                                            @RequestParam String otp){

        String storedOtp = otpStorage.get(email);

        if(storedOtp!=null && storedOtp.equals(otp)){

            verifiedEmails.put(email,true);
            otpStorage.remove(email);

            return ResponseEntity.ok("Email Verified Successfully");
        }

        return ResponseEntity.badRequest().body("Invalid OTP");
    }

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Student student){

        if(repository.findByEmail(student.getEmail()).isPresent()){
            return ResponseEntity.badRequest().body("Email already exists!");
        }

        if(!Boolean.TRUE.equals(verifiedEmails.get(student.getEmail()))){
            return ResponseEntity.badRequest().body("Please verify email first!");
        }

        student.setPassword(encoder.encode(student.getPassword()));
        student.setRole("ROLE_STUDENT");
        student.setTotalFee(2000);
        student.setPaidAmount(0);

        repository.save(student);

        verifiedEmails.remove(student.getEmail());

        return ResponseEntity.ok("Registration Successful");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String,String> request){

        String email = request.get("email");
        String password = request.get("password");

        Optional<Student> optionalStudent = repository.findByEmail(email);

        if(optionalStudent.isEmpty()){
            return ResponseEntity.badRequest().body("Invalid Credentials");
        }

        Student student = optionalStudent.get();

        if(!encoder.matches(password, student.getPassword())){
            return ResponseEntity.badRequest().body("Invalid Credentials");
        }

        String token = jwtUtil.generateToken(student.getEmail());

        Map<String,Object> response = new HashMap<>();
        response.put("token", token);
        response.put("role", student.getRole());

        return ResponseEntity.ok(response);
    }
    // FORGOT PASSWORD
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email){

        Optional<Student> optional = repository.findByEmail(email);

        if(optional.isEmpty()){
            return ResponseEntity.badRequest().body("Email not registered");
        }

        String token = UUID.randomUUID().toString();

        resetTokens.put(token,email);
        tokenExpiry.put(token, LocalDateTime.now().plusMinutes(30));

        emailService.sendResetLink(email,token);

        return ResponseEntity.ok("Reset link sent to email");
    }

    // RESET PASSWORD
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token,
                                                @RequestParam String password){

        if(!resetTokens.containsKey(token)){
            return ResponseEntity.badRequest().body("Invalid token");
        }

        if(LocalDateTime.now().isAfter(tokenExpiry.get(token))){
            resetTokens.remove(token);
            tokenExpiry.remove(token);
            return ResponseEntity.badRequest().body("Token expired");
        }

        String email = resetTokens.get(token);

        Student student =
                repository.findByEmail(email).orElseThrow();

        student.setPassword(encoder.encode(password));

        repository.save(student);

        resetTokens.remove(token);
        tokenExpiry.remove(token);

        return ResponseEntity.ok("Password updated successfully");
    }
}