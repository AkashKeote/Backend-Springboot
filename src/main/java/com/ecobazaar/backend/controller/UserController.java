package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.entity.User;
import com.ecobazaar.backend.entity.UserRole;
import com.ecobazaar.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:62804", "http://127.0.0.1:62804", "http://localhost:3000"})
public class UserController {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Get all users
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", users);
            response.put("total", users.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch users: " + e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Get users by role
    @GetMapping("/role/{role}")
    public ResponseEntity<Map<String, Object>> getUsersByRole(@PathVariable String role) {
        try {
            UserRole userRole;
            try {
                userRole = UserRole.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid role: " + role);
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            List<User> users = userRepository.findByRole(userRole);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", users);
            response.put("role", role);
            response.put("total", users.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch users by role: " + e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Get active users only
    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActiveUsers() {
        try {
            List<User> users = userRepository.findByIsActiveTrue();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", users);
            response.put("total", users.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch active users: " + e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            
            if (userOpt.isPresent()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("user", userOpt.get());
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "User not found with ID: " + id);
                
                return ResponseEntity.status(404).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch user: " + e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Update user status (activate/deactivate)
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> request) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                Boolean isActive = request.get("isActive");
                
                if (isActive != null) {
                    user.setIsActive(isActive);
                    userRepository.save(user);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("message", "User status updated successfully");
                    response.put("user", user);
                    
                    return ResponseEntity.ok(response);
                } else {
                    Map<String, Object> errorResponse = new HashMap<>();
                    errorResponse.put("success", false);
                    errorResponse.put("message", "isActive field is required");
                    
                    return ResponseEntity.badRequest().body(errorResponse);
                }
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "User not found with ID: " + id);
                
                return ResponseEntity.status(404).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to update user status: " + e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userRepository.findById(id);
            
            if (userOpt.isPresent()) {
                userRepository.deleteById(id);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "User deleted successfully");
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "User not found with ID: " + id);
                
                return ResponseEntity.status(404).body(errorResponse);
            }
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to delete user: " + e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Create new user
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUser(@RequestBody Map<String, Object> userRequest) {
        try {
            // Validate required fields
            String name = (String) userRequest.get("name");
            String email = (String) userRequest.get("email");
            String password = (String) userRequest.get("password");
            String roleString = (String) userRequest.get("role");
            Boolean isActive = (Boolean) userRequest.getOrDefault("isActive", true);
            
            if (name == null || name.trim().isEmpty() || 
                email == null || email.trim().isEmpty() || 
                password == null || password.trim().isEmpty() ||
                roleString == null || roleString.trim().isEmpty()) {
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Name, email, password, and role are required");
                
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Check if user with this email already exists
            Optional<User> existingUser = userRepository.findByEmail(email);
            if (existingUser.isPresent()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "User with this email already exists");
                
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Parse role
            UserRole role;
            try {
                role = UserRole.valueOf(roleString.toUpperCase());
            } catch (IllegalArgumentException e) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Invalid role: " + roleString + ". Valid roles are: ADMIN, SHOPKEEPER, CUSTOMER");
                
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Create new user
            User newUser = new User();
            newUser.setName(name.trim());
            newUser.setEmail(email.trim().toLowerCase());
            newUser.setPassword(passwordEncoder.encode(password)); // Hash password with BCrypt
            newUser.setRole(role);
            newUser.setIsActive(isActive);
            
            User savedUser = userRepository.save(newUser);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User created successfully");
            response.put("user", savedUser);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to create user: " + e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    // Get user statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        try {
            List<User> allUsers = userRepository.findAll();
            List<User> adminUsers = userRepository.findByRole(UserRole.ADMIN);
            List<User> shopkeeperUsers = userRepository.findByRole(UserRole.SHOPKEEPER);
            List<User> customerUsers = userRepository.findByRole(UserRole.CUSTOMER);
            List<User> activeUsers = userRepository.findByIsActiveTrue();
            List<User> inactiveUsers = userRepository.findByIsActiveFalse();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("total", allUsers.size());
            stats.put("admin", adminUsers.size());
            stats.put("shopkeeper", shopkeeperUsers.size());
            stats.put("customer", customerUsers.size());
            stats.put("active", activeUsers.size());
            stats.put("inactive", inactiveUsers.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Failed to fetch user statistics: " + e.getMessage());
            
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
