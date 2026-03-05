package com.dheeraj.fee.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOtp(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("your_organisation_mail");
        message.setTo(toEmail);
        message.setSubject("Acharya Bangalore B School - OTP");
        message.setText("Your OTP is: " + otp);

        mailSender.send(message);
    }

    // 🔹 PASSWORD RESET EMAIL
    public void sendResetLink(String toEmail, String token) {

        String link = "http://localhost:8080/reset-password.html?token=" + token;
   

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("your_organisation_mail");
        message.setTo(toEmail);
        message.setSubject("Reset Password - Acharya Bangalore B School");

        message.setText(
                "Click the link below to reset your password:\n\n"
                        + link +
                        "\n\nIf you did not request this, ignore this email."
        );

        mailSender.send(message);
    }
}