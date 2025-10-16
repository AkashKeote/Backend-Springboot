package com.ecobazaar.backend.service;

import com.ecobazaar.backend.entity.UserEcoProfile;
import com.ecobazaar.backend.repository.UserEcoProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service class for managing Leaderboard and User Eco Profiles
 */
@Service
@Transactional
public class LeaderboardService {

    @Autowired
    private UserEcoProfileRepository profileRepository;

    // Profile Management
    public UserEcoProfile getOrCreateProfile(String userId, String userName, String userEmail) {
        Optional<UserEcoProfile> profileOpt = profileRepository.findByUserId(userId);
        if (profileOpt.isPresent()) {
            return profileOpt.get();
        }
        
        UserEcoProfile profile = new UserEcoProfile(userId, userName, userEmail);
        return profileRepository.save(profile);
    }

    public Optional<UserEcoProfile> getProfile(String userId) {
        return profileRepository.findByUserId(userId);
    }

    public UserEcoProfile updateProfile(UserEcoProfile profile) {
        profile.setLastActivityDate(LocalDateTime.now());
        return profileRepository.save(profile);
    }

    // Eco Points Management
    public UserEcoProfile addEcoPoints(String userId, Integer points, String reason) {
        UserEcoProfile profile = getOrCreateProfile(userId, "User " + userId, null);
        profile.addEcoPoints(points);
        profile.setLastActivityDate(LocalDateTime.now());
        
        // Update rank
        Long rank = profileRepository.getUserRankByEcoPoints(userId);
        if (rank != null) {
            profile.setCurrentRank(rank.intValue());
        }
        
        return profileRepository.save(profile);
    }

    public UserEcoProfile addCarbonSaved(String userId, Double carbon) {
        UserEcoProfile profile = getOrCreateProfile(userId, "User " + userId, null);
        profile.addCarbonSaved(carbon);
        profile.setLastActivityDate(LocalDateTime.now());
        return profileRepository.save(profile);
    }

    public UserEcoProfile updateEnvironmentalImpact(String userId, Double carbonSaved, 
                                                   Double waterSaved, Double energySaved, Double wasteReduced) {
        UserEcoProfile profile = getOrCreateProfile(userId, "User " + userId, null);
        
        if (carbonSaved != null) profile.setTotalCarbonSaved(profile.getTotalCarbonSaved() + carbonSaved);
        if (waterSaved != null) profile.setTotalWaterSaved(profile.getTotalWaterSaved() + waterSaved);
        if (energySaved != null) profile.setTotalEnergySaved(profile.getTotalEnergySaved() + energySaved);
        if (wasteReduced != null) profile.setTotalWasteReduced(profile.getTotalWasteReduced() + wasteReduced);
        
        profile.addCarbonSaved(0.0); // Update trees equivalent
        profile.setLastActivityDate(LocalDateTime.now());
        
        return profileRepository.save(profile);
    }

    // Challenge Updates
    public UserEcoProfile updateChallengeStats(String userId, boolean challengeCompleted, Integer pointsEarned) {
        UserEcoProfile profile = getOrCreateProfile(userId, "User " + userId, null);
        
        if (challengeCompleted) {
            profile.setTotalChallengesCompleted(profile.getTotalChallengesCompleted() + 1);
            if (pointsEarned != null) {
                profile.addEcoPoints(pointsEarned);
            }
        }
        
        // Update completion rate
        int total = profile.getTotalChallengesCompleted() + profile.getActiveChallenges();
        if (total > 0) {
            double rate = (double) profile.getTotalChallengesCompleted() / total * 100;
            profile.setChallengeCompletionRate(rate);
        }
        
        profile.setLastActivityDate(LocalDateTime.now());
        return profileRepository.save(profile);
    }

    // Streak Management
    public UserEcoProfile updateStreak(String userId, boolean activityToday) {
        UserEcoProfile profile = getOrCreateProfile(userId, "User " + userId, null);
        
        LocalDateTime lastActivity = profile.getLastActivityDate();
        LocalDateTime now = LocalDateTime.now();
        
        if (lastActivity != null) {
            long daysDiff = java.time.Duration.between(lastActivity, now).toDays();
            
            if (daysDiff == 1 && activityToday) {
                // Continue streak
                profile.incrementStreak();
            } else if (daysDiff > 1) {
                // Streak broken
                profile.resetStreak();
                if (activityToday) {
                    profile.incrementStreak();
                }
            }
        } else if (activityToday) {
            profile.incrementStreak();
        }
        
        profile.setLastActivityDate(now);
        return profileRepository.save(profile);
    }

    // Shopping Stats Update
    public UserEcoProfile updateShoppingStats(String userId, Double orderAmount, 
                                             Double discountSaved, Integer ecoProductsCount) {
        UserEcoProfile profile = getOrCreateProfile(userId, "User " + userId, null);
        
        profile.setTotalOrders(profile.getTotalOrders() + 1);
        if (orderAmount != null) {
            profile.setTotalSpent(profile.getTotalSpent() + orderAmount);
        }
        if (discountSaved != null) {
            profile.setTotalSavingsFromDiscounts(profile.getTotalSavingsFromDiscounts() + discountSaved);
        }
        if (ecoProductsCount != null) {
            profile.setEcoProductsPurchased(profile.getEcoProductsPurchased() + ecoProductsCount);
        }
        
        profile.setLastActivityDate(LocalDateTime.now());
        return profileRepository.save(profile);
    }

    // Leaderboard Queries
    public Map<String, Object> getGlobalLeaderboard(int page, int size) {
        Map<String, Object> result = new HashMap<>();
        
        Pageable pageable = PageRequest.of(page, size);
        Page<UserEcoProfile> leaderboardPage = profileRepository.findLeaderboardByEcoPoints(pageable);
        
        List<Map<String, Object>> leaderboard = new ArrayList<>();
        int rank = page * size + 1;
        
        for (UserEcoProfile profile : leaderboardPage.getContent()) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("rank", rank++);
            entry.put("userId", profile.getUserId());
            entry.put("userName", profile.getUserName());
            entry.put("totalEcoPoints", profile.getTotalEcoPoints());
            entry.put("ecoLevel", profile.getEcoLevel());
            entry.put("levelName", profile.getLevelName());
            entry.put("totalCarbonSaved", profile.getTotalCarbonSaved());
            entry.put("totalChallengesCompleted", profile.getTotalChallengesCompleted());
            entry.put("currentStreakDays", profile.getCurrentStreakDays());
            entry.put("profileImageUrl", profile.getProfileImageUrl());
            
            // Calculate rank change
            if (profile.getPreviousRank() != null && profile.getCurrentRank() != null) {
                entry.put("rankChange", profile.getPreviousRank() - profile.getCurrentRank());
            } else {
                entry.put("rankChange", 0);
            }
            
            leaderboard.add(entry);
        }
        
        result.put("leaderboard", leaderboard);
        result.put("currentPage", page);
        result.put("totalPages", leaderboardPage.getTotalPages());
        result.put("totalUsers", leaderboardPage.getTotalElements());
        result.put("pageSize", size);
        
        return result;
    }

    public List<Map<String, Object>> getTop10ByEcoPoints() {
        Pageable pageable = PageRequest.of(0, 10);
        List<UserEcoProfile> topUsers = profileRepository.findTop10ByEcoPoints(pageable);
        return convertToLeaderboardEntries(topUsers, 1);
    }

    public List<Map<String, Object>> getTop10ByCarbonSaved() {
        Pageable pageable = PageRequest.of(0, 10);
        List<UserEcoProfile> topUsers = profileRepository.findTop10ByCarbonSaved(pageable);
        return convertToLeaderboardEntries(topUsers, 1);
    }

    public List<Map<String, Object>> getTop10ByChallenges() {
        Pageable pageable = PageRequest.of(0, 10);
        List<UserEcoProfile> topUsers = profileRepository.findTop10ByChallengesCompleted(pageable);
        return convertToLeaderboardEntries(topUsers, 1);
    }

    public List<Map<String, Object>> getTop10ByStreak() {
        Pageable pageable = PageRequest.of(0, 10);
        List<UserEcoProfile> topUsers = profileRepository.findTop10ByCurrentStreak(pageable);
        return convertToLeaderboardEntries(topUsers, 1);
    }

    // User Specific
    public Map<String, Object> getUserRankingInfo(String userId) {
        Map<String, Object> info = new HashMap<>();
        
        Optional<UserEcoProfile> profileOpt = profileRepository.findByUserId(userId);
        if (profileOpt.isEmpty()) {
            info.put("found", false);
            return info;
        }
        
        UserEcoProfile profile = profileOpt.get();
        info.put("found", true);
        info.put("profile", profile);
        
        // Get ranks
        Long rankByPoints = profileRepository.getUserRankByEcoPoints(userId);
        Long rankByCarbon = profileRepository.getUserRankByCarbonSaved(userId);
        
        info.put("rankByEcoPoints", rankByPoints != null ? rankByPoints : 0);
        info.put("rankByCarbonSaved", rankByCarbon != null ? rankByCarbon : 0);
        
        // Get percentile
        Long totalUsers = profileRepository.count();
        if (totalUsers > 0 && rankByPoints != null) {
            double percentile = (1.0 - (double) rankByPoints / totalUsers) * 100;
            info.put("percentile", percentile);
        }
        
        // Update current rank in profile
        if (rankByPoints != null) {
            profile.setCurrentRank(rankByPoints.intValue());
            profileRepository.save(profile);
        }
        
        return info;
    }

    public List<Map<String, Object>> getUsersNearRank(String userId, int range) {
        Long userRank = profileRepository.getUserRankByEcoPoints(userId);
        if (userRank == null) {
            return new ArrayList<>();
        }
        
        int offset = Math.max(0, userRank.intValue() - range - 1);
        int limit = range * 2 + 1;
        
        List<UserEcoProfile> nearbyUsers = profileRepository.findUsersNearRank(offset, limit);
        return convertToLeaderboardEntries(nearbyUsers, offset + 1);
    }

    // Statistics
    public Map<String, Object> getGlobalStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        Long totalEcoPoints = profileRepository.getTotalEcoPointsAcrossAllUsers();
        Double totalCarbonSaved = profileRepository.getTotalCarbonSavedAcrossAllUsers();
        Double avgEcoPoints = profileRepository.getAverageEcoPoints();
        Long totalUsers = profileRepository.count();
        
        stats.put("totalEcoPoints", totalEcoPoints != null ? totalEcoPoints : 0);
        stats.put("totalCarbonSaved", totalCarbonSaved != null ? totalCarbonSaved : 0.0);
        stats.put("averageEcoPoints", avgEcoPoints != null ? avgEcoPoints : 0.0);
        stats.put("totalUsers", totalUsers);
        
        // Calculate trees equivalent
        if (totalCarbonSaved != null) {
            stats.put("treesEquivalent", totalCarbonSaved / 20.0);
        }
        
        // Get level distribution
        List<Object[]> levelDistribution = profileRepository.getLevelDistribution();
        List<Map<String, Object>> levelStats = new ArrayList<>();
        for (Object[] row : levelDistribution) {
            Map<String, Object> levelStat = new HashMap<>();
            levelStat.put("level", row[0]);
            levelStat.put("levelName", row[1]);
            levelStat.put("userCount", row[2]);
            levelStats.add(levelStat);
        }
        stats.put("levelDistribution", levelStats);
        
        return stats;
    }

    // Helper Methods
    private List<Map<String, Object>> convertToLeaderboardEntries(List<UserEcoProfile> profiles, int startRank) {
        List<Map<String, Object>> entries = new ArrayList<>();
        int rank = startRank;
        
        for (UserEcoProfile profile : profiles) {
            Map<String, Object> entry = new HashMap<>();
            entry.put("rank", rank++);
            entry.put("userId", profile.getUserId());
            entry.put("userName", profile.getUserName());
            entry.put("totalEcoPoints", profile.getTotalEcoPoints());
            entry.put("ecoLevel", profile.getEcoLevel());
            entry.put("levelName", profile.getLevelName());
            entry.put("totalCarbonSaved", profile.getTotalCarbonSaved());
            entry.put("totalChallengesCompleted", profile.getTotalChallengesCompleted());
            entry.put("currentStreakDays", profile.getCurrentStreakDays());
            entry.put("profileImageUrl", profile.getProfileImageUrl());
            
            if (profile.getPreviousRank() != null && profile.getCurrentRank() != null) {
                entry.put("rankChange", profile.getPreviousRank() - profile.getCurrentRank());
            } else {
                entry.put("rankChange", 0);
            }
            
            entries.add(entry);
        }
        
        return entries;
    }

    // Batch Rank Update (to be called periodically)
    public void updateAllRanks() {
        List<UserEcoProfile> allProfiles = profileRepository.findAll();
        
        for (UserEcoProfile profile : allProfiles) {
            Long rank = profileRepository.getUserRankByEcoPoints(profile.getUserId());
            if (rank != null) {
                profile.setCurrentRank(rank.intValue());
            }
        }
        
        profileRepository.saveAll(allProfiles);
    }

    // Search
    public List<Map<String, Object>> searchUsers(String keyword) {
        List<UserEcoProfile> users = profileRepository.searchUsersByName(keyword);
        return convertToLeaderboardEntries(users, 1);
    }
}