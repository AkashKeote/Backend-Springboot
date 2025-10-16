package com.ecobazaar.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_settings")
public class UserSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;
    
    @Column(name = "theme")
    private String theme = "light";
    
    @Column(name = "language")
    private String language = "en";
    
    @Column(name = "notifications_enabled")
    private Boolean notificationsEnabled = true;
    
    @Column(name = "email_notifications")
    private Boolean emailNotifications = true;
    
    @Column(name = "push_notifications")
    private Boolean pushNotifications = true;
    
    @Column(name = "sms_notifications")
    private Boolean smsNotifications = false;
    
    @Column(name = "eco_tips_enabled")
    private Boolean ecoTipsEnabled = true;
    
    @Column(name = "challenge_reminders")
    private Boolean challengeReminders = true;
    
    @Column(name = "order_updates")
    private Boolean orderUpdates = true;
    
    @Column(name = "promotional_emails")
    private Boolean promotionalEmails = false;
    
    @Column(name = "privacy_level")
    private String privacyLevel = "medium";
    
    @Column(name = "data_sharing")
    private Boolean dataSharing = false;
    
    @Column(name = "location_tracking")
    private Boolean locationTracking = false;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserSettings() {}
    
    public UserSettings(String userId) {
        this.userId = userId;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getTheme() {
        return theme;
    }
    
    public void setTheme(String theme) {
        this.theme = theme;
    }
    
    public String getLanguage() {
        return language;
    }
    
    public void setLanguage(String language) {
        this.language = language;
    }
    
    public Boolean getNotificationsEnabled() {
        return notificationsEnabled;
    }
    
    public void setNotificationsEnabled(Boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
    
    public Boolean getEmailNotifications() {
        return emailNotifications;
    }
    
    public void setEmailNotifications(Boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }
    
    public Boolean getPushNotifications() {
        return pushNotifications;
    }
    
    public void setPushNotifications(Boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }
    
    public Boolean getSmsNotifications() {
        return smsNotifications;
    }
    
    public void setSmsNotifications(Boolean smsNotifications) {
        this.smsNotifications = smsNotifications;
    }
    
    public Boolean getEcoTipsEnabled() {
        return ecoTipsEnabled;
    }
    
    public void setEcoTipsEnabled(Boolean ecoTipsEnabled) {
        this.ecoTipsEnabled = ecoTipsEnabled;
    }
    
    public Boolean getChallengeReminders() {
        return challengeReminders;
    }
    
    public void setChallengeReminders(Boolean challengeReminders) {
        this.challengeReminders = challengeReminders;
    }
    
    public Boolean getOrderUpdates() {
        return orderUpdates;
    }
    
    public void setOrderUpdates(Boolean orderUpdates) {
        this.orderUpdates = orderUpdates;
    }
    
    public Boolean getPromotionalEmails() {
        return promotionalEmails;
    }
    
    public void setPromotionalEmails(Boolean promotionalEmails) {
        this.promotionalEmails = promotionalEmails;
    }
    
    public String getPrivacyLevel() {
        return privacyLevel;
    }
    
    public void setPrivacyLevel(String privacyLevel) {
        this.privacyLevel = privacyLevel;
    }
    
    public Boolean getDataSharing() {
        return dataSharing;
    }
    
    public void setDataSharing(Boolean dataSharing) {
        this.dataSharing = dataSharing;
    }
    
    public Boolean getLocationTracking() {
        return locationTracking;
    }
    
    public void setLocationTracking(Boolean locationTracking) {
        this.locationTracking = locationTracking;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
