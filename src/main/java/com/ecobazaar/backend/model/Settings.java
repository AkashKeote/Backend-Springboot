package com.ecobazaar.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Settings Model Class
 * 
 * Represents user settings data structure for EcoBazaarX
 * Includes notification preferences, privacy settings, and app preferences
 */
public class Settings {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("appPreferences")
    private AppPreferences appPreferences;

    @JsonProperty("notificationSettings")
    private NotificationSettings notificationSettings;

    @JsonProperty("privacySettings")
    private PrivacySettings privacySettings;

    @JsonProperty("syncSettings")
    private SyncSettings syncSettings;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("lastUpdated")
    private String lastUpdated;

    // Default constructor
    public Settings() {}

    // Constructor with all fields
    public Settings(String userId, AppPreferences appPreferences, 
                   NotificationSettings notificationSettings, PrivacySettings privacySettings, 
                   SyncSettings syncSettings) {
        this.userId = userId;
        this.appPreferences = appPreferences;
        this.notificationSettings = notificationSettings;
        this.privacySettings = privacySettings;
        this.syncSettings = syncSettings;
    }

    // Getters and Setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public AppPreferences getAppPreferences() { return appPreferences; }
    public void setAppPreferences(AppPreferences appPreferences) { this.appPreferences = appPreferences; }

    public NotificationSettings getNotificationSettings() { return notificationSettings; }
    public void setNotificationSettings(NotificationSettings notificationSettings) { this.notificationSettings = notificationSettings; }

    public PrivacySettings getPrivacySettings() { return privacySettings; }
    public void setPrivacySettings(PrivacySettings privacySettings) { this.privacySettings = privacySettings; }

    public SyncSettings getSyncSettings() { return syncSettings; }
    public void setSyncSettings(SyncSettings syncSettings) { this.syncSettings = syncSettings; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }

    @Override
    public String toString() {
        return "Settings{" +
                "userId='" + userId + '\'' +
                ", appPreferences=" + appPreferences +
                ", notificationSettings=" + notificationSettings +
                ", privacySettings=" + privacySettings +
                ", syncSettings=" + syncSettings +
                ", createdAt='" + createdAt + '\'' +
                ", lastUpdated='" + lastUpdated + '\'' +
                '}';
    }
}