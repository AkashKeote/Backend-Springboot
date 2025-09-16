package com.ecobazaar.backend.model;

/**
 * Authentication response model
 */
public class AuthResponse {
    private boolean success;
    private String message;
    private String token;
    private String refreshToken;
    private String userId;
    private String userRole;

    // Default constructor
    public AuthResponse() {}

    // Constructor with parameters
    public AuthResponse(boolean success, String message, String token, String refreshToken, String userId, String userRole) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.userRole = userRole;
    }

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", userId='" + userId + '\'' +
                ", userRole='" + userRole + '\'' +
                '}';
    }
}
