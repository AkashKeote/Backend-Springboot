package com.ecobazaar.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Notification Settings Model Class
 * 
 * Represents user notification preferences for EcoBazaarX
 */
public class NotificationSettings {

    @JsonProperty("notificationsEnabled")
    private Boolean notificationsEnabled;

    @JsonProperty("emailNotifications")
    private Boolean emailNotifications;

    @JsonProperty("pushNotifications")
    private Boolean pushNotifications;

    @JsonProperty("smsNotifications")
    private Boolean smsNotifications;

    @JsonProperty("ecoTipsEnabled")
    private Boolean ecoTipsEnabled;

    @JsonProperty("challengeReminders")
    private Boolean challengeReminders;

    @JsonProperty("orderUpdates")
    private Boolean orderUpdates;

    @JsonProperty("promotionalEmails")
    private Boolean promotionalEmails;

    // Default constructor
    public NotificationSettings() {
        // Default values
        this.notificationsEnabled = true;
        this.emailNotifications = true;
        this.pushNotifications = true;
        this.smsNotifications = false;
        this.ecoTipsEnabled = true;
        this.challengeReminders = true;
        this.orderUpdates = true;
        this.promotionalEmails = false;
    }

    // Constructor with all fields
    public NotificationSettings(Boolean notificationsEnabled, Boolean emailNotifications,
                              Boolean pushNotifications, Boolean smsNotifications,
                              Boolean ecoTipsEnabled, Boolean challengeReminders,
                              Boolean orderUpdates, Boolean promotionalEmails) {
        this.notificationsEnabled = notificationsEnabled;
        this.emailNotifications = emailNotifications;
        this.pushNotifications = pushNotifications;
        this.smsNotifications = smsNotifications;
        this.ecoTipsEnabled = ecoTipsEnabled;
        this.challengeReminders = challengeReminders;
        this.orderUpdates = orderUpdates;
        this.promotionalEmails = promotionalEmails;
    }

    // Getters and Setters
    public Boolean getNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(Boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }

    public Boolean getEmailNotifications() { return emailNotifications; }
    public void setEmailNotifications(Boolean emailNotifications) { this.emailNotifications = emailNotifications; }

    public Boolean getPushNotifications() { return pushNotifications; }
    public void setPushNotifications(Boolean pushNotifications) { this.pushNotifications = pushNotifications; }

    public Boolean getSmsNotifications() { return smsNotifications; }
    public void setSmsNotifications(Boolean smsNotifications) { this.smsNotifications = smsNotifications; }

    public Boolean getEcoTipsEnabled() { return ecoTipsEnabled; }
    public void setEcoTipsEnabled(Boolean ecoTipsEnabled) { this.ecoTipsEnabled = ecoTipsEnabled; }

    public Boolean getChallengeReminders() { return challengeReminders; }
    public void setChallengeReminders(Boolean challengeReminders) { this.challengeReminders = challengeReminders; }

    public Boolean getOrderUpdates() { return orderUpdates; }
    public void setOrderUpdates(Boolean orderUpdates) { this.orderUpdates = orderUpdates; }

    public Boolean getPromotionalEmails() { return promotionalEmails; }
    public void setPromotionalEmails(Boolean promotionalEmails) { this.promotionalEmails = promotionalEmails; }

    @Override
    public String toString() {
        return "NotificationSettings{" +
                "notificationsEnabled=" + notificationsEnabled +
                ", emailNotifications=" + emailNotifications +
                ", pushNotifications=" + pushNotifications +
                ", smsNotifications=" + smsNotifications +
                ", ecoTipsEnabled=" + ecoTipsEnabled +
                ", challengeReminders=" + challengeReminders +
                ", orderUpdates=" + orderUpdates +
                ", promotionalEmails=" + promotionalEmails +
                '}';
    }
}