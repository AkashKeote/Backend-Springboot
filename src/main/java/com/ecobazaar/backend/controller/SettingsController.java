package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.model.*;
import com.ecobazaar.backend.service.SettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Settings Controller Class
 * 
 * Provides REST API endpoints for user settings management
 * in the EcoBazaarX backend application.
 */
@RestController
@RequestMapping("/settings")
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
public class SettingsController {

    @Autowired
    private SettingsService settingsService;

    /**
     * Get user settings
     * 
     * @param userId User ID
     * @return User settings or error response
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserSettings(@PathVariable String userId) {
        try {
            Settings settings = settingsService.getUserSettings(userId);
            if (settings != null) {
                return ResponseEntity.ok(settings);
            } else {
                return ResponseEntity.status(404).body("Settings not found for user: " + userId);
            }
        } catch (Exception e) {
            System.err.println("❌ Error getting settings: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Error retrieving settings: " + e.getMessage());
        }
    }


    /**
     * Update user settings
     * 
     * @param userId User ID
     * @param settings Settings object to update
     * @return Success or error response
     */
    @PostMapping("/{userId}/update")
    public ResponseEntity<?> updateSettings(
            @PathVariable String userId,
            @RequestBody Settings settings) {
        
        try {
            Settings updatedSettings = settingsService.updateUserSettings(userId, settings);
            return ResponseEntity.ok(updatedSettings);

        } catch (Exception e) {
            System.err.println("❌ Error updating settings: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Error updating settings: " + e.getMessage());
        }
    }

    /**
     * Update app preferences
     * 
     * @param userId User ID
     * @param appPreferences App preferences object
     * @return Success or error response
     */
    @PostMapping("/{userId}/app-preferences")
    public ResponseEntity<?> updateAppPreferences(
            @PathVariable String userId,
            @RequestBody AppPreferences appPreferences) {
        
        try {
            Settings updatedSettings = settingsService.updateAppPreferences(userId, appPreferences);
            return ResponseEntity.ok(updatedSettings);

        } catch (Exception e) {
            System.err.println("❌ Error updating app preferences: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Error updating app preferences: " + e.getMessage());
        }
    }

    /**
     * Update notification settings
     * 
     * @param userId User ID
     * @param notificationSettings Notification settings object
     * @return Success or error response
     */
    @PostMapping("/{userId}/notifications")
    public ResponseEntity<?> updateNotificationSettings(
            @PathVariable String userId,
            @RequestBody NotificationSettings notificationSettings) {
        
        try {
            Settings updatedSettings = settingsService.updateNotificationSettings(userId, notificationSettings);
            return ResponseEntity.ok(updatedSettings);

        } catch (Exception e) {
            System.err.println("❌ Error updating notification settings: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Error updating notification settings: " + e.getMessage());
        }
    }

    /**
     * Update privacy settings
     * 
     * @param userId User ID
     * @param privacySettings Privacy settings object
     * @return Success or error response
     */
    @PostMapping("/{userId}/privacy")
    public ResponseEntity<?> updatePrivacySettings(
            @PathVariable String userId,
            @RequestBody PrivacySettings privacySettings) {
        
        try {
            Settings updatedSettings = settingsService.updatePrivacySettings(userId, privacySettings);
            return ResponseEntity.ok(updatedSettings);

        } catch (Exception e) {
            System.err.println("❌ Error updating privacy settings: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Error updating privacy settings: " + e.getMessage());
        }
    }

    /**
     * Update sync settings
     * 
     * @param userId User ID
     * @param syncSettings Sync settings object
     * @return Success or error response
     */
    @PostMapping("/{userId}/sync")
    public ResponseEntity<?> updateSyncSettings(
            @PathVariable String userId,
            @RequestBody SyncSettings syncSettings) {
        
        try {
            Settings updatedSettings = settingsService.updateSyncSettings(userId, syncSettings);
            return ResponseEntity.ok(updatedSettings);

        } catch (Exception e) {
            System.err.println("❌ Error updating sync settings: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Error updating sync settings: " + e.getMessage());
        }
    }

    /**
     * Initialize user settings for new users
     * 
     * @param userId User ID
     * @param settings Default settings to initialize
     * @return Success or error response
     */
    @PostMapping("/{userId}/initialize")
    public ResponseEntity<?> initializeUserSettings(
            @PathVariable String userId,
            @RequestBody Map<String, Object> settings) {
        
        try {
            Settings initializedSettings = settingsService.initializeUserSettings(userId, settings);
            return ResponseEntity.ok(initializedSettings);

        } catch (Exception e) {
            System.err.println("❌ Error initializing user settings: " + e.getMessage());
            return ResponseEntity.internalServerError()
                .body("Error initializing user settings: " + e.getMessage());
        }
    }

    /**
     * Health check endpoint
     * 
     * @return Health status
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("✅ Settings Service is running!");
    }
}