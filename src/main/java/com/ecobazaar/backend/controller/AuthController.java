package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.model.AuthRequest;
import com.ecobazaar.backend.model.AuthResponse;
import com.ecobazaar.backend.model.UserRegistration;
import com.ecobazaar.backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Authentication Controller for EcoBazaarX
 * Handles user login, registration, and JWT token management
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {
    "http://localhost:3000", 
    "http://localhost:5173", 
    "http://127.0.0.1:3000",
    "http://127.0.0.1:5173",
    "https://ecobazaar.vercel.app",
    "https://ecobazzarx.web.app", 
    "https://ecobazzarx.firebaseapp.com",
    "https://akashkeote.github.io"
})
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * User login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        try {
            System.out.println("Login attempt for email: " + authRequest.getEmail() + ", role: " + authRequest.getRole());
            
            AuthResponse response = authService.authenticateUser(
                authRequest.getEmail(), 
                authRequest.getPassword(),
                authRequest.getRole()
            );
            
            System.out.println("Authentication result: " + response.isSuccess() + " - " + response.getMessage());
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            System.err.println("Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                .body(new AuthResponse(false, "Login failed: " + e.getMessage(), null, null, null, null));
        }
    }


    /**
     * User registration endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody UserRegistration registration) {
        try {
            AuthResponse response = authService.registerUser(registration);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new AuthResponse(false, "Registration failed: " + e.getMessage(), null, null, null, null)
            );
        }
    }

    /**
     * Validate JWT token
     */
    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            Map<String, Object> result = authService.validateToken(jwtToken);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("valid", false, "message", "Invalid token: " + e.getMessage())
            );
        }
    }

    /**
     * Refresh JWT token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            AuthResponse response = authService.refreshToken(jwtToken);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                new AuthResponse(false, "Token refresh failed: " + e.getMessage(), null, null, null, null)
            );
        }
    }

    /**
     * User logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.replace("Bearer ", "");
            authService.logout(jwtToken);
            return ResponseEntity.ok(Map.of("success", true, "message", "Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "message", "Logout failed: " + e.getMessage())
            );
        }
    }

    /**
     * Request password reset
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> requestPasswordReset(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Email is required")
                );
            }
            
            Map<String, Object> result = authService.requestPasswordReset(email.trim());
            
            if ((Boolean) result.get("success")) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                Map.of("success", false, "message", "Password reset request failed: " + e.getMessage())
            );
        }
    }

    /**
     * Health check for auth service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Authentication Service",
            "timestamp", System.currentTimeMillis()
        ));
    }
}
