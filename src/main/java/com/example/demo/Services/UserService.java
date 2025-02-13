package com.example.demo.Services;

import com.example.demo.Entities.User;
import com.example.demo.Repositories.UserRepository;
import com.google.api.pathtemplate.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;

    private final PasswordEncoder passwordEncoder;

    // Initialize passwordEncoder in the constructor
    public UserService() {
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Method to find user by email
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerUser(User user) {
        // Set default role to 'user' if not specified
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("user");
        }

        // Check if email already exists in MongoDB
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ValidationException("Email is already in use.");
        }

        // Hash the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Save the user to MongoDB
        return userRepository.save(user);
    }

    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent() && passwordEncoder.matches(password, user.get().getPassword())) {
            return user;
        }
        return Optional.empty();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Request password reset email
    public boolean resetPasswordRequest(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            emailService.sendResetPasswordEmail(email);
            return true;
        }
        return false;
    }

    // Reset password without token
    public boolean resetPassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // Prevent resetting to an empty password
            if (newPassword == null || newPassword.isEmpty()) {
                throw new ValidationException("Password cannot be empty.");
            }

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // Add this method to fetch user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email); // Fetch user by email from the repository
    }

    // Get all users with role 'user'
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);  // Assuming a method in the repository that filters users by role
    }

    // Delete user by ID
    public boolean deleteUserById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return true;
        }
        return false;
    }

    public User saveUser(User user) {
        return userRepository.save(user);  // save() is a method provided by Spring Data JPA
    }


}
