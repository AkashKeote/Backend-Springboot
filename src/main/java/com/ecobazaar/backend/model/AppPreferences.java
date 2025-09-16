package com.ecobazaar.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * App Preferences Model Class
 * 
 * Represents user app preferences and customization settings for EcoBazaarX
 */
public class AppPreferences {

    @JsonProperty("theme")
    private String theme;

    @JsonProperty("darkModeEnabled")
    private boolean darkModeEnabled;

    @JsonProperty("autoSaveEnabled")
    private boolean autoSaveEnabled;

    @JsonProperty("hapticFeedbackEnabled")
    private boolean hapticFeedbackEnabled;

    @JsonProperty("language")
    private String language;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("timezone")
    private String timezone;

    // Default constructor
    public AppPreferences() {
        // Default values
        this.theme = "light";
        this.darkModeEnabled = false;
        this.autoSaveEnabled = true;
        this.hapticFeedbackEnabled = true;
        this.language = "en";
        this.currency = "INR";
        this.timezone = "Asia/Kolkata";
    }

    // Constructor with all fields
    public AppPreferences(String theme, boolean darkModeEnabled, boolean autoSaveEnabled, boolean hapticFeedbackEnabled,
                         String language, String currency, String timezone) {
        this.theme = theme;
        this.darkModeEnabled = darkModeEnabled;
        this.autoSaveEnabled = autoSaveEnabled;
        this.hapticFeedbackEnabled = hapticFeedbackEnabled;
        this.language = language;
        this.currency = currency;
        this.timezone = timezone;
    }

    // Getters and Setters
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public boolean isDarkModeEnabled() { return darkModeEnabled; }
    public void setDarkModeEnabled(boolean darkModeEnabled) { this.darkModeEnabled = darkModeEnabled; }

    public boolean isAutoSaveEnabled() { return autoSaveEnabled; }
    public void setAutoSaveEnabled(boolean autoSaveEnabled) { this.autoSaveEnabled = autoSaveEnabled; }

    public boolean isHapticFeedbackEnabled() { return hapticFeedbackEnabled; }
    public void setHapticFeedbackEnabled(boolean hapticFeedbackEnabled) { this.hapticFeedbackEnabled = hapticFeedbackEnabled; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    @Override
    public String toString() {
        return "AppPreferences{" +
                "theme='" + theme + '\'' +
                ", darkModeEnabled=" + darkModeEnabled +
                ", autoSaveEnabled=" + autoSaveEnabled +
                ", hapticFeedbackEnabled=" + hapticFeedbackEnabled +
                ", language='" + language + '\'' +
                ", currency='" + currency + '\'' +
                ", timezone='" + timezone + '\'' +
                '}';
    }
}





