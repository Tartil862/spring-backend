package com.example.demo.Controllers;

import com.example.demo.Services.EmailService;
import com.example.demo.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class PasswordResetController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;

    @PostMapping("/reset-password-request")
    public ResponseEntity<String> resetPasswordRequest(@RequestParam("email") String email) {
        // Call EmailService to send the reset password email

        emailService.sendResetPasswordEmail(email);
        return ResponseEntity.ok("Password reset link has been sent to your email.");
    }

    // Reset password with email
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam("email") String email,
                                           @RequestParam("newPassword") String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            return ResponseEntity.badRequest().body("New password cannot be empty.");
        }

        boolean resetSuccess = userService.resetPassword(email, newPassword);
        if (resetSuccess) {
            return ResponseEntity.ok("Password has been successfully reset.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Email not found or invalid.");
        }
    }
}