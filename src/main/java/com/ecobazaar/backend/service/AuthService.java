package com.ecobazaar.backend.service;

import com.ecobazaar.backend.entity.User;
import com.ecobazaar.backend.entity.UserRole;
import com.ecobazaar.backend.model.AuthResponse;
import com.ecobazaar.backend.model.UserRegistration;
import com.ecobazaar.backend.repository.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Authentication Service for EcoBazaarX
 * Handles user authentication, JWT token generation, and MySQL database integration
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    // JWT Secret Key (in production, use environment variable)
    private static final String JWT_SECRET = "EcoBazaarXSecretKeyForJWTTokenGeneration2024";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    
    // Token expiration times
    private static final long ACCESS_TOKEN_EXPIRATION = TimeUnit.HOURS.toMillis(24); // 24 hours
    private static final long REFRESH_TOKEN_EXPIRATION = TimeUnit.DAYS.toMillis(7); // 7 days

    /**
     * Authenticate user with email, password, and role
     */
    public AuthResponse authenticateUser(String email, String password, String role) {
        try {
            // Find user by email
            Optional<User> userOpt = userRepository.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                return new AuthResponse(false, "User not found", null, null, null, null);
            }
            
            User user = userOpt.get();
            
            // Check if user is active
            if (!user.getIsActive()) {
                return new AuthResponse(false, "User account is deactivated", null, null, null, null);
            }
            
            // Check password
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return new AuthResponse(false, "Invalid password", null, null, null, null);
            }
            
            // Check role
            if (!user.getRole().toString().equalsIgnoreCase(role)) {
                return new AuthResponse(false, "Invalid role", null, null, null, null);
            }
            
            // Generate JWT tokens
            String accessToken = generateAccessToken(user.getId().toString(), email, role);
            String refreshToken = generateRefreshToken(user.getId().toString(), email);
            
            return new AuthResponse(
                true,
                "Login successful",
                accessToken,
                refreshToken,
                user.getId().toString(),
                role
            );

        } catch (Exception e) {
            return new AuthResponse(false, "Authentication failed: " + e.getMessage(), null, null, null, null);
        }
    }

    /**
     * Register new user
     */
    public AuthResponse registerUser(UserRegistration registration) {
        try {
            // Validate input
            if (registration.getName() == null || registration.getName().trim().isEmpty()) {
                return new AuthResponse(false, "Name is required", null, null, null, null);
            }
            if (registration.getEmail() == null || registration.getEmail().trim().isEmpty()) {
                return new AuthResponse(false, "Email is required", null, null, null, null);
            }
            if (registration.getPassword() == null || registration.getPassword().length() < 8) {
                return new AuthResponse(false, "Password must be at least 8 characters", null, null, null, null);
            }
            if (!registration.getPassword().equals(registration.getConfirmPassword())) {
                return new AuthResponse(false, "Passwords do not match", null, null, null, null);
            }

            // Check if user already exists
            if (userRepository.existsByEmail(registration.getEmail())) {
                return new AuthResponse(false, "User with this email already exists", null, null, null, null);
            }

            // Create new user
            User user = new User();
            user.setName(registration.getName());
            user.setEmail(registration.getEmail());
            user.setPassword(passwordEncoder.encode(registration.getPassword()));
            user.setPhone(registration.getPhone() != null ? registration.getPhone() : "");
            user.setRole(UserRole.valueOf(registration.getRole().toUpperCase()));
            user.setIsActive(true);

            // Save to MySQL
            User savedUser = userRepository.save(user);

            return new AuthResponse(
                true,
                "User registered successfully",
                null,
                null,
                savedUser.getId().toString(),
                savedUser.getRole().toString()
            );

        } catch (Exception e) {
            System.err.println("❌ Registration error: " + e.getMessage());
            return new AuthResponse(false, "Registration failed: " + e.getMessage(), null, null, null, null);
        }
    }

    /**
     * Generate access token
     */
    private String generateAccessToken(String userId, String email, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("role", role);
        claims.put("type", "access");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Generate refresh token
     */
    private String generateRefreshToken(String userId, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("email", email);
        claims.put("type", "refresh");

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate JWT token
     */
    public Map<String, Object> validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(SECRET_KEY)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            // Check if token is expired
            Date expiration = claims.getExpiration();
            Date now = new Date();
            
            if (expiration.before(now)) {
                Map<String, Object> result = new HashMap<>();
                result.put("valid", false);
                result.put("error", "JWT expired " + (now.getTime() - expiration.getTime()) + " milliseconds ago at " + expiration.toInstant() + ". Current time: " + now.toInstant() + ". Allowed clock skew: 0 milliseconds.");
                return result;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("valid", true);
            result.put("userId", claims.get("userId"));
            result.put("email", claims.get("email"));
            result.put("role", claims.get("role"));
            result.put("type", claims.get("type"));
            result.put("expiration", claims.getExpiration());

            return result;

        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("valid", false);
            result.put("error", e.getMessage());
            return result;
        }
    }

    /**
     * Refresh JWT token
     */
    public AuthResponse refreshToken(String refreshToken) {
        try {
            Map<String, Object> validation = validateToken(refreshToken);
            
            if (!(Boolean) validation.get("valid")) {
                return new AuthResponse(false, "Invalid refresh token", null, null, null, null);
            }

            String tokenType = (String) validation.get("type");
            if (!"refresh".equals(tokenType)) {
                return new AuthResponse(false, "Invalid token type", null, null, null, null);
            }

            String userId = (String) validation.get("userId");
            String email = (String) validation.get("email");
            String role = (String) validation.get("role");

            // Generate new tokens
            String newAccessToken = generateAccessToken(userId, email, role);
            String newRefreshToken = generateRefreshToken(userId, email);

            return new AuthResponse(
                true,
                "Token refreshed successfully",
                newAccessToken,
                newRefreshToken,
                userId,
                role
            );

        } catch (Exception e) {
            return new AuthResponse(false, "Token refresh failed: " + e.getMessage(), null, null, null, null);
        }
    }

    /**
     * Logout user (invalidate token)
     */
    public void logout(String token) {
        // In a real application, you would add the token to a blacklist
        // For now, we'll just log the logout
        System.out.println("User logged out with token: " + token.substring(0, Math.min(20, token.length())) + "...");
    }

    /**
     * Request password reset
     */
    public Map<String, Object> requestPasswordReset(String email) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(email);
            
            if (userOpt.isEmpty()) {
                return Map.of("success", false, "message", "User not found");
            }

            // In a real application, you would:
            // 1. Generate a reset token
            // 2. Send email with reset link
            // 3. Store reset token in database with expiration

            return Map.of(
                "success", true,
                "message", "Password reset email sent (simulated)",
                "email", email
            );

        } catch (Exception e) {
            return Map.of("success", false, "message", "Password reset request failed: " + e.getMessage());
        }
    }
}