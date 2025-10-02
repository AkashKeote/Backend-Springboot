package com.ecobazaar.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * App Settings Model Class
 * 
 * Represents app-specific settings for EcoBazaarX
 */
public class AppSettings {

    @JsonProperty("theme")
    private String theme;

    @JsonProperty("fontSize")
    private String fontSize;

    // Default constructor
    public AppSettings() {
        // Default values
        this.theme = "system";
        this.fontSize = "medium";
    }

    // Constructor with all fields
    public AppSettings(String theme, String fontSize) {
        this.theme = theme;
        this.fontSize = fontSize;
    }

    // Getters and Setters
    public String getTheme() { return theme; }
    public void setTheme(String theme) { this.theme = theme; }

    public String getFontSize() { return fontSize; }
    public void setFontSize(String fontSize) { this.fontSize = fontSize; }

    @Override
    public String toString() {
        return "AppSettings{" +
                "theme='" + theme + '\'' +
                ", fontSize='" + fontSize + '\'' +
                '}';
    }
}





