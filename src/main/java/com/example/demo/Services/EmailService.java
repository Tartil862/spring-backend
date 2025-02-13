package com.example.demo.Services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Send reset password email with the email parameter
    public void sendResetPasswordEmail(String email) {
        // Update the reset URL to include the email as a query parameter
        String resetUrl = "http://localhost:4200/reset-password?email=" + email;  // Direct link with email parameter
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset Request");
        message.setText("To reset your password, click the following link: " + resetUrl);
        mailSender.send(message);
    }
}
