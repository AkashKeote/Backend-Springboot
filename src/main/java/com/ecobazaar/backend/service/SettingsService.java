package com.ecobazaar.backend.service;

import com.ecobazaar.backend.model.*;
import com.ecobazaar.backend.entity.UserSettings;
import com.ecobazaar.backend.repository.UserSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

/**
 * Settings Service Class
 * 
 * Handles all settings-related business logic and MySQL operations
 * for the EcoBazaarX backend application.
 */
@Service
public class SettingsService {

    @Autowired
    private UserSettingsRepository userSettingsRepository;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Get user settings from MySQL
     * 
     * @param userId User ID
     * @return Settings object or null if not found
     */
    @Cacheable(value = "settings", key = "#userId")
    public Settings getUserSettings(String userId) {
        try {
            Optional<UserSettings> userSettingsOpt = userSettingsRepository.findByUserId(userId);
            
            if (userSettingsOpt.isPresent()) {
                UserSettings userSettings = userSettingsOpt.get();
                System.out.println("Found existing settings for user " + userId);
                return mapToSettings(userId, userSettings);
            }

            // User doesn't have settings, create default settings in database
            System.out.println("No settings found for user " + userId + ", creating default settings");
            UserSettings defaultUserSettings = new UserSettings(userId);
            defaultUserSettings = userSettingsRepository.save(defaultUserSettings);
            return mapToSettings(userId, defaultUserSettings);

        } catch (Exception e) {
            System.err.println("Error getting user settings: " + e.getMessage());
            e.printStackTrace();
            
            // If database operation fails, return default settings without saving
            System.out.println("Database error, returning default settings without saving for user " + userId);
            return createDefaultSettings(userId);
        }
    }

    /**
     * Update user settings in MySQL
     * 
     * @param userId User ID
     * @param settings Settings object to update
     * @return Updated Settings object
     */
    @CacheEvict(value = "settings", key = "#userId")
    public Settings updateUserSettings(String userId, Settings settings) {
        try {
            Optional<UserSettings> userSettingsOpt = userSettingsRepository.findByUserId(userId);
            UserSettings userSettings;
            
            if (userSettingsOpt.isPresent()) {
                userSettings = userSettingsOpt.get();
            } else {
                userSettings = new UserSettings(userId);
            }
            
            // Update settings
            updateUserSettingsFromModel(userSettings, settings);
            userSettings = userSettingsRepository.save(userSettings);
            
            return mapToSettings(userId, userSettings);
            
        } catch (Exception e) {
            System.err.println("Error updating user settings: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update user settings", e);
        }
    }

    /**
     * Update app preferences
     * 
     * @param userId User ID
     * @param preferences AppPreferences object
     * @return Updated Settings object
     */
    @CacheEvict(value = "settings", key = "#userId")
    public Settings updateAppPreferences(String userId, AppPreferences preferences) {
        try {
            Optional<UserSettings> userSettingsOpt = userSettingsRepository.findByUserId(userId);
            UserSettings userSettings;
            
            if (userSettingsOpt.isPresent()) {
                userSettings = userSettingsOpt.get();
            } else {
                userSettings = new UserSettings(userId);
            }
            
            // Update app preferences
            if (preferences.getTheme() != null) {
                userSettings.setTheme(preferences.getTheme());
            }
            if (preferences.getLanguage() != null) {
                userSettings.setLanguage(preferences.getLanguage());
            }
            
            userSettings = userSettingsRepository.save(userSettings);
            return mapToSettings(userId, userSettings);
            
        } catch (Exception e) {
            System.err.println("Error updating app preferences: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update app preferences", e);
        }
    }

    /**
     * Update notification settings
     * 
     * @param userId User ID
     * @param notificationSettings NotificationSettings object
     * @return Updated Settings object
     */
    @CacheEvict(value = "settings", key = "#userId")
    public Settings updateNotificationSettings(String userId, NotificationSettings notificationSettings) {
        try {
            Optional<UserSettings> userSettingsOpt = userSettingsRepository.findByUserId(userId);
            UserSettings userSettings;
            
            if (userSettingsOpt.isPresent()) {
                userSettings = userSettingsOpt.get();
            } else {
                userSettings = new UserSettings(userId);
            }
            
            // Update notification settings
            if (notificationSettings.getNotificationsEnabled() != null) {
                userSettings.setNotificationsEnabled(notificationSettings.getNotificationsEnabled());
            }
            if (notificationSettings.getEmailNotifications() != null) {
                userSettings.setEmailNotifications(notificationSettings.getEmailNotifications());
            }
            if (notificationSettings.getPushNotifications() != null) {
                userSettings.setPushNotifications(notificationSettings.getPushNotifications());
            }
            if (notificationSettings.getSmsNotifications() != null) {
                userSettings.setSmsNotifications(notificationSettings.getSmsNotifications());
            }
            if (notificationSettings.getEcoTipsEnabled() != null) {
                userSettings.setEcoTipsEnabled(notificationSettings.getEcoTipsEnabled());
            }
            if (notificationSettings.getChallengeReminders() != null) {
                userSettings.setChallengeReminders(notificationSettings.getChallengeReminders());
            }
            if (notificationSettings.getOrderUpdates() != null) {
                userSettings.setOrderUpdates(notificationSettings.getOrderUpdates());
            }
            if (notificationSettings.getPromotionalEmails() != null) {
                userSettings.setPromotionalEmails(notificationSettings.getPromotionalEmails());
            }
            
            userSettings = userSettingsRepository.save(userSettings);
            return mapToSettings(userId, userSettings);
            
        } catch (Exception e) {
            System.err.println("Error updating notification settings: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update notification settings", e);
        }
    }

    /**
     * Update privacy settings
     * 
     * @param userId User ID
     * @param privacySettings PrivacySettings object
     * @return Updated Settings object
     */
    @CacheEvict(value = "settings", key = "#userId")
    public Settings updatePrivacySettings(String userId, PrivacySettings privacySettings) {
        try {
            Optional<UserSettings> userSettingsOpt = userSettingsRepository.findByUserId(userId);
            UserSettings userSettings;
            
            if (userSettingsOpt.isPresent()) {
                userSettings = userSettingsOpt.get();
            } else {
                userSettings = new UserSettings(userId);
            }
            
            // Update privacy settings
            if (privacySettings.getPrivacyLevel() != null) {
                userSettings.setPrivacyLevel(privacySettings.getPrivacyLevel());
            }
            if (privacySettings.getDataSharing() != null) {
                userSettings.setDataSharing(privacySettings.getDataSharing());
            }
            if (privacySettings.getLocationTracking() != null) {
                userSettings.setLocationTracking(privacySettings.getLocationTracking());
            }
            
            userSettings = userSettingsRepository.save(userSettings);
            return mapToSettings(userId, userSettings);
            
        } catch (Exception e) {
            System.err.println("Error updating privacy settings: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update privacy settings", e);
        }
    }

    /**
     * Update sync settings
     * 
     * @param userId User ID
     * @param syncSettings SyncSettings object
     * @return Updated Settings object
     */
    @CacheEvict(value = "settings", key = "#userId")
    public Settings updateSyncSettings(String userId, SyncSettings syncSettings) {
        try {
            // For now, sync settings are handled at the app level
            // This method can be extended based on specific sync requirements
            return getUserSettings(userId);
            
        } catch (Exception e) {
            System.err.println("Error updating sync settings: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to update sync settings", e);
        }
    }

    /**
     * Map UserSettings entity to Settings model
     */
    private Settings mapToSettings(String userId, UserSettings userSettings) {
        Settings settings = new Settings();
        settings.setUserId(userId);
        settings.setLastUpdated(LocalDateTime.now().format(formatter));
        
        // App Preferences
        AppPreferences appPreferences = new AppPreferences();
        appPreferences.setTheme(userSettings.getTheme());
        appPreferences.setLanguage(userSettings.getLanguage());
        settings.setAppPreferences(appPreferences);
        
        // Notification Settings
        NotificationSettings notificationSettings = new NotificationSettings();
        notificationSettings.setNotificationsEnabled(userSettings.getNotificationsEnabled());
        notificationSettings.setEmailNotifications(userSettings.getEmailNotifications());
        notificationSettings.setPushNotifications(userSettings.getPushNotifications());
        notificationSettings.setSmsNotifications(userSettings.getSmsNotifications());
        notificationSettings.setEcoTipsEnabled(userSettings.getEcoTipsEnabled());
        notificationSettings.setChallengeReminders(userSettings.getChallengeReminders());
        notificationSettings.setOrderUpdates(userSettings.getOrderUpdates());
        notificationSettings.setPromotionalEmails(userSettings.getPromotionalEmails());
        settings.setNotificationSettings(notificationSettings);
        
        // Privacy Settings
        PrivacySettings privacySettings = new PrivacySettings();
        privacySettings.setPrivacyLevel(userSettings.getPrivacyLevel());
        privacySettings.setDataSharing(userSettings.getDataSharing());
        privacySettings.setLocationTracking(userSettings.getLocationTracking());
        settings.setPrivacySettings(privacySettings);
        
        // Sync Settings (default values)
        SyncSettings syncSettings = new SyncSettings();
        syncSettings.setAutoSync(true);
        syncSettings.setSyncFrequency("daily");
        syncSettings.setLastSync(LocalDateTime.now().format(formatter));
        settings.setSyncSettings(syncSettings);
        
        return settings;
    }

    /**
     * Update UserSettings entity from Settings model
     */
    private void updateUserSettingsFromModel(UserSettings userSettings, Settings settings) {
        if (settings.getAppPreferences() != null) {
            AppPreferences appPrefs = settings.getAppPreferences();
            if (appPrefs.getTheme() != null) {
                userSettings.setTheme(appPrefs.getTheme());
            }
            if (appPrefs.getLanguage() != null) {
                userSettings.setLanguage(appPrefs.getLanguage());
            }
        }
        
        if (settings.getNotificationSettings() != null) {
            NotificationSettings notifSettings = settings.getNotificationSettings();
            if (notifSettings.getNotificationsEnabled() != null) {
                userSettings.setNotificationsEnabled(notifSettings.getNotificationsEnabled());
            }
            if (notifSettings.getEmailNotifications() != null) {
                userSettings.setEmailNotifications(notifSettings.getEmailNotifications());
            }
            if (notifSettings.getPushNotifications() != null) {
                userSettings.setPushNotifications(notifSettings.getPushNotifications());
            }
            if (notifSettings.getSmsNotifications() != null) {
                userSettings.setSmsNotifications(notifSettings.getSmsNotifications());
            }
            if (notifSettings.getEcoTipsEnabled() != null) {
                userSettings.setEcoTipsEnabled(notifSettings.getEcoTipsEnabled());
            }
            if (notifSettings.getChallengeReminders() != null) {
                userSettings.setChallengeReminders(notifSettings.getChallengeReminders());
            }
            if (notifSettings.getOrderUpdates() != null) {
                userSettings.setOrderUpdates(notifSettings.getOrderUpdates());
            }
            if (notifSettings.getPromotionalEmails() != null) {
                userSettings.setPromotionalEmails(notifSettings.getPromotionalEmails());
            }
        }
        
        if (settings.getPrivacySettings() != null) {
            PrivacySettings privacySettings = settings.getPrivacySettings();
            if (privacySettings.getPrivacyLevel() != null) {
                userSettings.setPrivacyLevel(privacySettings.getPrivacyLevel());
            }
            if (privacySettings.getDataSharing() != null) {
                userSettings.setDataSharing(privacySettings.getDataSharing());
            }
            if (privacySettings.getLocationTracking() != null) {
                userSettings.setLocationTracking(privacySettings.getLocationTracking());
            }
        }
    }

    /**
     * Initialize user settings for new users
     * 
     * @param userId User ID
     * @param settingsMap Map of settings to initialize
     * @return Initialized Settings object
     */
    @CacheEvict(value = "settings", key = "#userId")
    public Settings initializeUserSettings(String userId, Map<String, Object> settingsMap) {
        try {
            // Check if user already has settings
            Optional<UserSettings> existingSettings = userSettingsRepository.findByUserId(userId);
            if (existingSettings.isPresent()) {
                // User already has settings, return them
                System.out.println("User " + userId + " already has settings, returning existing settings");
                return mapToSettings(userId, existingSettings.get());
            }
            
            // Create new user settings with provided values
            UserSettings userSettings = new UserSettings(userId);
            
            // Set default values from the provided map
            if (settingsMap.containsKey("notifications")) {
                userSettings.setNotificationsEnabled((Boolean) settingsMap.get("notifications"));
            }
            if (settingsMap.containsKey("darkMode")) {
                userSettings.setTheme((Boolean) settingsMap.get("darkMode") ? "dark" : "light");
            }
            if (settingsMap.containsKey("language")) {
                userSettings.setLanguage((String) settingsMap.get("language"));
            }
            if (settingsMap.containsKey("pushNotifications")) {
                userSettings.setPushNotifications((Boolean) settingsMap.get("pushNotifications"));
            }
            if (settingsMap.containsKey("emailNotifications")) {
                userSettings.setEmailNotifications((Boolean) settingsMap.get("emailNotifications"));
            }
            if (settingsMap.containsKey("orderUpdates")) {
                userSettings.setOrderUpdates((Boolean) settingsMap.get("orderUpdates"));
            }
            if (settingsMap.containsKey("promotionalOffers")) {
                userSettings.setPromotionalEmails((Boolean) settingsMap.get("promotionalOffers"));
            }
            if (settingsMap.containsKey("priceAlerts")) {
                userSettings.setEcoTipsEnabled((Boolean) settingsMap.get("priceAlerts"));
            }
            
            // Try to save the initialized settings
            try {
                userSettings = userSettingsRepository.save(userSettings);
                System.out.println("Successfully initialized settings for user " + userId);
                return mapToSettings(userId, userSettings);
            } catch (Exception saveException) {
                // If save fails due to duplicate entry, try to fetch existing settings
                if (saveException.getMessage().contains("Duplicate entry") || 
                    saveException.getMessage().contains("UK_4bos7satl9xeqd18frfeqg6tt")) {
                    System.out.println("Duplicate entry detected for user " + userId + ", fetching existing settings");
                    Optional<UserSettings> existingSettingsRetry = userSettingsRepository.findByUserId(userId);
                    if (existingSettingsRetry.isPresent()) {
                        return mapToSettings(userId, existingSettingsRetry.get());
                    }
                }
                throw saveException;
            }
            
        } catch (Exception e) {
            System.err.println("Error initializing user settings: " + e.getMessage());
            e.printStackTrace();
            
            // If initialization fails, try to return existing settings or default settings
            try {
                Optional<UserSettings> fallbackSettings = userSettingsRepository.findByUserId(userId);
                if (fallbackSettings.isPresent()) {
                    System.out.println("Returning existing settings as fallback for user " + userId);
                    return mapToSettings(userId, fallbackSettings.get());
                } else {
                    System.out.println("Returning default settings as fallback for user " + userId);
                    return createDefaultSettings(userId);
                }
            } catch (Exception fallbackException) {
                System.err.println("Fallback also failed: " + fallbackException.getMessage());
                throw new RuntimeException("Failed to initialize user settings", e);
            }
        }
    }

    /**
     * Create default settings for a new user
     */
    private Settings createDefaultSettings(String userId) {
        Settings settings = new Settings();
        settings.setUserId(userId);
        settings.setLastUpdated(LocalDateTime.now().format(formatter));
        
        // Default App Preferences
        AppPreferences appPreferences = new AppPreferences();
        appPreferences.setTheme("light");
        appPreferences.setLanguage("en");
        settings.setAppPreferences(appPreferences);
        
        // Default Notification Settings
        NotificationSettings notificationSettings = new NotificationSettings();
        notificationSettings.setNotificationsEnabled(true);
        notificationSettings.setEmailNotifications(true);
        notificationSettings.setPushNotifications(true);
        notificationSettings.setSmsNotifications(false);
        notificationSettings.setEcoTipsEnabled(true);
        notificationSettings.setChallengeReminders(true);
        notificationSettings.setOrderUpdates(true);
        notificationSettings.setPromotionalEmails(false);
        settings.setNotificationSettings(notificationSettings);
        
        // Default Privacy Settings
        PrivacySettings privacySettings = new PrivacySettings();
        privacySettings.setPrivacyLevel("medium");
        privacySettings.setDataSharing(false);
        privacySettings.setLocationTracking(false);
        settings.setPrivacySettings(privacySettings);
        
        // Default Sync Settings
        SyncSettings syncSettings = new SyncSettings();
        syncSettings.setAutoSync(true);
        syncSettings.setSyncFrequency("daily");
        syncSettings.setLastSync(LocalDateTime.now().format(formatter));
        settings.setSyncSettings(syncSettings);
        
        return settings;
    }
}