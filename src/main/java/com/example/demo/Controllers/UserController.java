package com.example.demo.Controllers;

import com.example.demo.Entities.User;
import com.example.demo.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signUp(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        // Check first and last name length
        if (user.getFirstName().length() < 3) {
            response.put("message", "First name must be at least 3 characters.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (user.getLastName().length() < 3) {
            response.put("message", "Last name must be at least 3 characters.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            response.put("message", "Invalid email format.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        if (user.getPassword().length() < 8 || !user.getPassword().matches(".*[A-Za-z].*") ||
                !user.getPassword().matches(".*\\d.*") || !user.getPassword().matches(".*[@$!%*?&].*")) {
            response.put("message", "Password must contain letters, numbers, and special characters.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            userService.registerUser(user);
            response.put("message", "User registered successfully!");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            response.put("message", "Error during registration.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody User user) {
        Optional<User> authenticatedUser = userService.authenticateUser(user.getEmail(), user.getPassword());
        Map<String, Object> response = new HashMap<>();
        if (authenticatedUser.isPresent()) {
            response.put("message", "Login successful!");
            response.put("user", authenticatedUser.get()); // Include the user object
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Invalid credentials.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }



    @GetMapping("/getAll")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, String>> getUserProfile() {
        Map<String, String> userDetails = new HashMap<>();

        // Get the current authenticated user
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof OAuth2User) {
            // OAuth2 User (e.g., authenticated via Google/Facebook)
            OAuth2User oauth2User = (OAuth2User) principal;
            userDetails.put("firstName", oauth2User.getAttribute("given_name"));
            userDetails.put("lastName", oauth2User.getAttribute("family_name"));
            userDetails.put("email", oauth2User.getAttribute("email"));
            userDetails.put("photo", oauth2User.getAttribute("picture"));
            userDetails.put("role", "user"); // Default role for OAuth2 users
            return ResponseEntity.ok(userDetails);
        }

        if (principal instanceof org.springframework.security.core.userdetails.User) {
            // Traditional User (e.g., authenticated via username and password)
            org.springframework.security.core.userdetails.User authenticatedUser = (org.springframework.security.core.userdetails.User) principal;
            Optional<User> user = userService.getUserByEmail(authenticatedUser.getUsername()); // Use email as the username
            if (user.isPresent()) {
                User userEntity = user.get();
                userDetails.put("firstName", userEntity.getFirstName());
                userDetails.put("lastName", userEntity.getLastName());
                userDetails.put("email", userEntity.getEmail());
                userDetails.put("photo", userEntity.getPhotoUrl());
                userDetails.put("role", userEntity.getRole());
                return ResponseEntity.ok(userDetails);
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "User not authenticated"));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable String  id) {
        Map<String, String> response = new HashMap<>();
        boolean deleted = userService.deleteUserById(id);  // Assuming a method to delete user by ID
        if (deleted) {
            response.put("message", "User deleted successfully.");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "User not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/allusers")
    public ResponseEntity<List<User>> getUsersWithRoleUser() {
        List<User> users = userService.getUsersByRole("user");  // Assuming a method in the service to filter by role
        return ResponseEntity.ok(users);
    }
    @PutMapping("/update")
    public ResponseEntity<Map<String, String>> updateProfile(@RequestBody User user) {
        Map<String, String> response = new HashMap<>();

        Optional<User> existingUser = userService.getUserByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            User updatedUser = existingUser.get();
            updatedUser.setFirstName(user.getFirstName());
            updatedUser.setLastName(user.getLastName());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setPhotoUrl(user.getPhotoUrl());  // Assuming you have a photo URL field
            userService.saveUser(updatedUser);  // Save the updated user

            response.put("message", "Profile updated successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }




}
