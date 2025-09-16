package com.ecobazaar.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Privacy Settings Model Class
 * 
 * Represents user privacy and security preferences for EcoBazaarX
 */
public class PrivacySettings {

    @JsonProperty("privacyLevel")
    private String privacyLevel;

    @JsonProperty("dataSharing")
    private Boolean dataSharing;

    @JsonProperty("locationTracking")
    private Boolean locationTracking;

    // Default constructor
    public PrivacySettings() {
        // Default values
        this.privacyLevel = "medium";
        this.dataSharing = false;
        this.locationTracking = false;
    }

    // Constructor with all fields
    public PrivacySettings(String privacyLevel, Boolean dataSharing, Boolean locationTracking) {
        this.privacyLevel = privacyLevel;
        this.dataSharing = dataSharing;
        this.locationTracking = locationTracking;
    }

    // Getters and Setters
    public String getPrivacyLevel() { return privacyLevel; }
    public void setPrivacyLevel(String privacyLevel) { this.privacyLevel = privacyLevel; }

    public Boolean getDataSharing() { return dataSharing; }
    public void setDataSharing(Boolean dataSharing) { this.dataSharing = dataSharing; }

    public Boolean getLocationTracking() { return locationTracking; }
    public void setLocationTracking(Boolean locationTracking) { this.locationTracking = locationTracking; }

    @Override
    public String toString() {
        return "PrivacySettings{" +
                "privacyLevel='" + privacyLevel + '\'' +
                ", dataSharing=" + dataSharing +
                ", locationTracking=" + locationTracking +
                '}';
    }
}