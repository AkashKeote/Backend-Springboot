package com.ecobazaar.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * UserEcoProfile Entity
 * 
 * Maintains comprehensive eco profile and statistics for users
 * Used for leaderboard rankings and user achievements
 */
@Entity
@Table(name = "user_eco_profiles", indexes = {
    @Index(name = "idx_eco_points", columnList = "total_eco_points"),
    @Index(name = "idx_carbon_saved", columnList = "total_carbon_saved"),
    @Index(name = "idx_level", columnList = "eco_level")
})
public class UserEcoProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;
    
    @Column(name = "user_name", nullable = false)
    private String userName;
    
    @Column(name = "user_email")
    private String userEmail;
    
    // Eco Points and Rankings
    @Column(name = "total_eco_points", nullable = false)
    private Integer totalEcoPoints = 0;
    
    @Column(name = "current_rank")
    private Integer currentRank;
    
    @Column(name = "previous_rank")
    private Integer previousRank;
    
    @Column(name = "eco_level")
    private Integer ecoLevel = 1;
    
    @Column(name = "level_name")
    private String levelName = "Eco Beginner";
    
    // Environmental Impact
    @Column(name = "total_carbon_saved", nullable = false)
    private Double totalCarbonSaved = 0.0;
    
    @Column(name = "total_water_saved")
    private Double totalWaterSaved = 0.0;
    
    @Column(name = "total_energy_saved")
    private Double totalEnergySaved = 0.0;
    
    @Column(name = "total_waste_reduced")
    private Double totalWasteReduced = 0.0;
    
    @Column(name = "trees_equivalent")
    private Double treesEquivalent = 0.0;
    
    // Challenge Statistics
    @Column(name = "total_challenges_completed")
    private Integer totalChallengesCompleted = 0;
    
    @Column(name = "active_challenges")
    private Integer activeChallenges = 0;
    
    @Column(name = "challenge_completion_rate")
    private Double challengeCompletionRate = 0.0;
    
    @Column(name = "current_streak_days")
    private Integer currentStreakDays = 0;
    
    @Column(name = "longest_streak_days")
    private Integer longestStreakDays = 0;
    
    @Column(name = "last_activity_date")
    private LocalDateTime lastActivityDate;
    
    // Shopping Statistics
    @Column(name = "total_orders")
    private Integer totalOrders = 0;
    
    @Column(name = "total_spent")
    private Double totalSpent = 0.0;
    
    @Column(name = "total_savings_from_discounts")
    private Double totalSavingsFromDiscounts = 0.0;
    
    @Column(name = "eco_products_purchased")
    private Integer ecoProductsPurchased = 0;
    
    // Badges and Achievements
    @Column(name = "total_badges")
    private Integer totalBadges = 0;
    
    @Column(columnDefinition = "TEXT")
    private String badges; // JSON array of badge IDs
    
    @Column(columnDefinition = "TEXT")
    private String achievements; // JSON array of achievement IDs
    
    // Social Features
    @Column(name = "total_referrals")
    private Integer totalReferrals = 0;
    
    @Column(name = "community_contributions")
    private Integer communityContributions = 0;
    
    // Profile Settings
    @Column(name = "is_public_profile")
    private Boolean isPublicProfile = true;
    
    @Column(name = "show_on_leaderboard")
    private Boolean showOnLeaderboard = true;
    
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    
    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserEcoProfile() {}
    
    public UserEcoProfile(String userId, String userName, String userEmail) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.lastActivityDate = LocalDateTime.now();
    }
    
    // Helper methods
    public void addEcoPoints(Integer points) {
        this.totalEcoPoints += points;
        updateLevel();
    }
    
    public void addCarbonSaved(Double carbon) {
        this.totalCarbonSaved += carbon;
        this.treesEquivalent = this.totalCarbonSaved / 20.0; // Rough estimation
    }
    
    public void updateLevel() {
        if (totalEcoPoints >= 10000) {
            this.ecoLevel = 10;
            this.levelName = "Eco Master";
        } else if (totalEcoPoints >= 5000) {
            this.ecoLevel = 9;
            this.levelName = "Eco Champion";
        } else if (totalEcoPoints >= 3000) {
            this.ecoLevel = 8;
            this.levelName = "Eco Hero";
        } else if (totalEcoPoints >= 2000) {
            this.ecoLevel = 7;
            this.levelName = "Eco Expert";
        } else if (totalEcoPoints >= 1500) {
            this.ecoLevel = 6;
            this.levelName = "Eco Professional";
        } else if (totalEcoPoints >= 1000) {
            this.ecoLevel = 5;
            this.levelName = "Eco Enthusiast";
        } else if (totalEcoPoints >= 500) {
            this.ecoLevel = 4;
            this.levelName = "Eco Supporter";
        } else if (totalEcoPoints >= 250) {
            this.ecoLevel = 3;
            this.levelName = "Eco Friend";
        } else if (totalEcoPoints >= 100) {
            this.ecoLevel = 2;
            this.levelName = "Eco Learner";
        } else {
            this.ecoLevel = 1;
            this.levelName = "Eco Beginner";
        }
    }
    
    public void incrementStreak() {
        this.currentStreakDays++;
        if (this.currentStreakDays > this.longestStreakDays) {
            this.longestStreakDays = this.currentStreakDays;
        }
    }
    
    public void resetStreak() {
        this.currentStreakDays = 0;
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
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public Integer getTotalEcoPoints() {
        return totalEcoPoints;
    }
    
    public void setTotalEcoPoints(Integer totalEcoPoints) {
        this.totalEcoPoints = totalEcoPoints;
        updateLevel();
    }
    
    public Integer getCurrentRank() {
        return currentRank;
    }
    
    public void setCurrentRank(Integer currentRank) {
        this.previousRank = this.currentRank;
        this.currentRank = currentRank;
    }
    
    public Integer getPreviousRank() {
        return previousRank;
    }
    
    public void setPreviousRank(Integer previousRank) {
        this.previousRank = previousRank;
    }
    
    public Integer getEcoLevel() {
        return ecoLevel;
    }
    
    public void setEcoLevel(Integer ecoLevel) {
        this.ecoLevel = ecoLevel;
    }
    
    public String getLevelName() {
        return levelName;
    }
    
    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }
    
    public Double getTotalCarbonSaved() {
        return totalCarbonSaved;
    }
    
    public void setTotalCarbonSaved(Double totalCarbonSaved) {
        this.totalCarbonSaved = totalCarbonSaved;
    }
    
    public Double getTotalWaterSaved() {
        return totalWaterSaved;
    }
    
    public void setTotalWaterSaved(Double totalWaterSaved) {
        this.totalWaterSaved = totalWaterSaved;
    }
    
    public Double getTotalEnergySaved() {
        return totalEnergySaved;
    }
    
    public void setTotalEnergySaved(Double totalEnergySaved) {
        this.totalEnergySaved = totalEnergySaved;
    }
    
    public Double getTotalWasteReduced() {
        return totalWasteReduced;
    }
    
    public void setTotalWasteReduced(Double totalWasteReduced) {
        this.totalWasteReduced = totalWasteReduced;
    }
    
    public Double getTreesEquivalent() {
        return treesEquivalent;
    }
    
    public void setTreesEquivalent(Double treesEquivalent) {
        this.treesEquivalent = treesEquivalent;
    }
    
    public Integer getTotalChallengesCompleted() {
        return totalChallengesCompleted;
    }
    
    public void setTotalChallengesCompleted(Integer totalChallengesCompleted) {
        this.totalChallengesCompleted = totalChallengesCompleted;
    }
    
    public Integer getActiveChallenges() {
        return activeChallenges;
    }
    
    public void setActiveChallenges(Integer activeChallenges) {
        this.activeChallenges = activeChallenges;
    }
    
    public Double getChallengeCompletionRate() {
        return challengeCompletionRate;
    }
    
    public void setChallengeCompletionRate(Double challengeCompletionRate) {
        this.challengeCompletionRate = challengeCompletionRate;
    }
    
    public Integer getCurrentStreakDays() {
        return currentStreakDays;
    }
    
    public void setCurrentStreakDays(Integer currentStreakDays) {
        this.currentStreakDays = currentStreakDays;
    }
    
    public Integer getLongestStreakDays() {
        return longestStreakDays;
    }
    
    public void setLongestStreakDays(Integer longestStreakDays) {
        this.longestStreakDays = longestStreakDays;
    }
    
    public LocalDateTime getLastActivityDate() {
        return lastActivityDate;
    }
    
    public void setLastActivityDate(LocalDateTime lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }
    
    public Integer getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }
    
    public Double getTotalSpent() {
        return totalSpent;
    }
    
    public void setTotalSpent(Double totalSpent) {
        this.totalSpent = totalSpent;
    }
    
    public Double getTotalSavingsFromDiscounts() {
        return totalSavingsFromDiscounts;
    }
    
    public void setTotalSavingsFromDiscounts(Double totalSavingsFromDiscounts) {
        this.totalSavingsFromDiscounts = totalSavingsFromDiscounts;
    }
    
    public Integer getEcoProductsPurchased() {
        return ecoProductsPurchased;
    }
    
    public void setEcoProductsPurchased(Integer ecoProductsPurchased) {
        this.ecoProductsPurchased = ecoProductsPurchased;
    }
    
    public Integer getTotalBadges() {
        return totalBadges;
    }
    
    public void setTotalBadges(Integer totalBadges) {
        this.totalBadges = totalBadges;
    }
    
    public String getBadges() {
        return badges;
    }
    
    public void setBadges(String badges) {
        this.badges = badges;
    }
    
    public String getAchievements() {
        return achievements;
    }
    
    public void setAchievements(String achievements) {
        this.achievements = achievements;
    }
    
    public Integer getTotalReferrals() {
        return totalReferrals;
    }
    
    public void setTotalReferrals(Integer totalReferrals) {
        this.totalReferrals = totalReferrals;
    }
    
    public Integer getCommunityContributions() {
        return communityContributions;
    }
    
    public void setCommunityContributions(Integer communityContributions) {
        this.communityContributions = communityContributions;
    }
    
    public Boolean getIsPublicProfile() {
        return isPublicProfile;
    }
    
    public void setIsPublicProfile(Boolean isPublicProfile) {
        this.isPublicProfile = isPublicProfile;
    }
    
    public Boolean getShowOnLeaderboard() {
        return showOnLeaderboard;
    }
    
    public void setShowOnLeaderboard(Boolean showOnLeaderboard) {
        this.showOnLeaderboard = showOnLeaderboard;
    }
    
    public String getProfileImageUrl() {
        return profileImageUrl;
    }
    
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
    
    public String getBio() {
        return bio;
    }
    
    public void setBio(String bio) {
        this.bio = bio;
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