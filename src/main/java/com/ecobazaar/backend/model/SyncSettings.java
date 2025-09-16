package com.ecobazaar.backend.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Sync Settings Model Class
 * 
 * Represents synchronization settings for EcoBazaarX
 */
public class SyncSettings {

    @JsonProperty("autoSync")
    private boolean autoSync;

    @JsonProperty("syncFrequency")
    private String syncFrequency;

    @JsonProperty("lastSync")
    private String lastSync;

    // Default constructor
    public SyncSettings() {
        // Default values
        this.autoSync = true;
        this.syncFrequency = "daily";
        this.lastSync = "";
    }

    // Constructor with all fields
    public SyncSettings(boolean autoSync, String syncFrequency, String lastSync) {
        this.autoSync = autoSync;
        this.syncFrequency = syncFrequency;
        this.lastSync = lastSync;
    }

    // Getters and Setters
    public boolean isAutoSync() { return autoSync; }
    public void setAutoSync(boolean autoSync) { this.autoSync = autoSync; }

    public String getSyncFrequency() { return syncFrequency; }
    public void setSyncFrequency(String syncFrequency) { this.syncFrequency = syncFrequency; }

    public String getLastSync() { return lastSync; }
    public void setLastSync(String lastSync) { this.lastSync = lastSync; }

    @Override
    public String toString() {
        return "SyncSettings{" +
                "autoSync=" + autoSync +
                ", syncFrequency='" + syncFrequency + '\'' +
                ", lastSync='" + lastSync + '\'' +
                '}';
    }
}