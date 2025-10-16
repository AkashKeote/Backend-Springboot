package com.ecobazaar.backend.model;

/**
 * Authentication request model for login
 */
public class AuthRequest {
    private String email;
    private String password;
    private String role;

    // Default constructor
    public AuthRequest() {}

    // Constructor with parameters
    public AuthRequest(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "AuthRequest{" +
                "email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
