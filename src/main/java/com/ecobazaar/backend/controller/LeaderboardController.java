package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.entity.UserEcoProfile;
import com.ecobazaar.backend.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Leaderboard and User Eco Profiles
 */
@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "*")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    // Leaderboard Endpoints

    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> getGlobalLeaderboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> leaderboard = leaderboardService.getGlobalLeaderboard(page, size);
            response.put("success", true);
            response.putAll(leaderboard);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch leaderboard: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/top10/eco-points")
    public ResponseEntity<Map<String, Object>> getTop10ByEcoPoints() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> top10 = leaderboardService.getTop10ByEcoPoints();
            response.put("success", true);
            response.put("leaderboard", top10);
            response.put("category", "Eco Points");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch top 10: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/top10/carbon-saved")
    public ResponseEntity<Map<String, Object>> getTop10ByCarbonSaved() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> top10 = leaderboardService.getTop10ByCarbonSaved();
            response.put("success", true);
            response.put("leaderboard", top10);
            response.put("category", "Carbon Saved");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch top 10: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/top10/challenges")
    public ResponseEntity<Map<String, Object>> getTop10ByChallenges() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> top10 = leaderboardService.getTop10ByChallenges();
            response.put("success", true);
            response.put("leaderboard", top10);
            response.put("category", "Challenges Completed");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch top 10: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/top10/streak")
    public ResponseEntity<Map<String, Object>> getTop10ByStreak() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> top10 = leaderboardService.getTop10ByStreak();
            response.put("success", true);
            response.put("leaderboard", top10);
            response.put("category", "Current Streak");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch top 10: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // User Profile Endpoints

    @GetMapping("/profile/{userId}")
    public ResponseEntity<Map<String, Object>> getUserProfile(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<UserEcoProfile> profileOpt = leaderboardService.getProfile(userId);
            if (profileOpt.isPresent()) {
                response.put("success", true);
                response.put("profile", profileOpt.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Profile not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/profile/{userId}/rank")
    public ResponseEntity<Map<String, Object>> getUserRankingInfo(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> rankInfo = leaderboardService.getUserRankingInfo(userId);
            response.put("success", true);
            response.putAll(rankInfo);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch ranking info: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/profile/{userId}/nearby")
    public ResponseEntity<Map<String, Object>> getUsersNearRank(
            @PathVariable String userId,
            @RequestParam(defaultValue = "5") int range) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> nearbyUsers = leaderboardService.getUsersNearRank(userId, range);
            response.put("success", true);
            response.put("nearbyUsers", nearbyUsers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch nearby users: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/profile/{userId}/update")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @PathVariable String userId,
            @RequestBody UserEcoProfile updatedProfile) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<UserEcoProfile> existingProfileOpt = leaderboardService.getProfile(userId);
            if (existingProfileOpt.isPresent()) {
                UserEcoProfile existingProfile = existingProfileOpt.get();
                
                // Update allowed fields
                if (updatedProfile.getUserName() != null) {
                    existingProfile.setUserName(updatedProfile.getUserName());
                }
                if (updatedProfile.getBio() != null) {
                    existingProfile.setBio(updatedProfile.getBio());
                }
                if (updatedProfile.getProfileImageUrl() != null) {
                    existingProfile.setProfileImageUrl(updatedProfile.getProfileImageUrl());
                }
                if (updatedProfile.getIsPublicProfile() != null) {
                    existingProfile.setIsPublicProfile(updatedProfile.getIsPublicProfile());
                }
                if (updatedProfile.getShowOnLeaderboard() != null) {
                    existingProfile.setShowOnLeaderboard(updatedProfile.getShowOnLeaderboard());
                }
                
                UserEcoProfile saved = leaderboardService.updateProfile(existingProfile);
                response.put("success", true);
                response.put("message", "Profile updated successfully");
                response.put("profile", saved);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Profile not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update profile: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Points and Stats Update Endpoints

    @PostMapping("/profile/{userId}/add-points")
    public ResponseEntity<Map<String, Object>> addEcoPoints(
            @PathVariable String userId,
            @RequestParam Integer points,
            @RequestParam(required = false) String reason) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserEcoProfile profile = leaderboardService.addEcoPoints(userId, points, reason);
            response.put("success", true);
            response.put("message", points + " eco points added successfully");
            response.put("profile", profile);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to add points: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/profile/{userId}/update-environmental-impact")
    public ResponseEntity<Map<String, Object>> updateEnvironmentalImpact(
            @PathVariable String userId,
            @RequestBody Map<String, Double> impact) {
        Map<String, Object> response = new HashMap<>();
        try {
            Double carbonSaved = impact.get("carbonSaved");
            Double waterSaved = impact.get("waterSaved");
            Double energySaved = impact.get("energySaved");
            Double wasteReduced = impact.get("wasteReduced");
            
            UserEcoProfile profile = leaderboardService.updateEnvironmentalImpact(
                userId, carbonSaved, waterSaved, energySaved, wasteReduced
            );
            
            response.put("success", true);
            response.put("message", "Environmental impact updated successfully");
            response.put("profile", profile);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update impact: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/profile/{userId}/update-challenge-stats")
    public ResponseEntity<Map<String, Object>> updateChallengeStats(
            @PathVariable String userId,
            @RequestParam boolean challengeCompleted,
            @RequestParam(required = false) Integer pointsEarned) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserEcoProfile profile = leaderboardService.updateChallengeStats(userId, challengeCompleted, pointsEarned);
            response.put("success", true);
            response.put("message", "Challenge stats updated successfully");
            response.put("profile", profile);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/profile/{userId}/update-streak")
    public ResponseEntity<Map<String, Object>> updateStreak(
            @PathVariable String userId,
            @RequestParam boolean activityToday) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserEcoProfile profile = leaderboardService.updateStreak(userId, activityToday);
            response.put("success", true);
            response.put("message", "Streak updated successfully");
            response.put("profile", profile);
            response.put("currentStreak", profile.getCurrentStreakDays());
            response.put("longestStreak", profile.getLongestStreakDays());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update streak: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/profile/{userId}/update-shopping-stats")
    public ResponseEntity<Map<String, Object>> updateShoppingStats(
            @PathVariable String userId,
            @RequestBody Map<String, Object> shoppingData) {
        Map<String, Object> response = new HashMap<>();
        try {
            Double orderAmount = shoppingData.get("orderAmount") != null ? 
                Double.valueOf(shoppingData.get("orderAmount").toString()) : null;
            Double discountSaved = shoppingData.get("discountSaved") != null ? 
                Double.valueOf(shoppingData.get("discountSaved").toString()) : null;
            Integer ecoProductsCount = shoppingData.get("ecoProductsCount") != null ? 
                Integer.valueOf(shoppingData.get("ecoProductsCount").toString()) : null;
            
            UserEcoProfile profile = leaderboardService.updateShoppingStats(
                userId, orderAmount, discountSaved, ecoProductsCount
            );
            
            response.put("success", true);
            response.put("message", "Shopping stats updated successfully");
            response.put("profile", profile);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update shopping stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Statistics Endpoints

    @GetMapping("/statistics/global")
    public ResponseEntity<Map<String, Object>> getGlobalStatistics() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = leaderboardService.getGlobalStatistics();
            response.put("success", true);
            response.putAll(stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch statistics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Search Endpoint

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUsers(@RequestParam String keyword) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> users = leaderboardService.searchUsers(keyword);
            response.put("success", true);
            response.put("users", users);
            response.put("count", users.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to search users: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Admin Endpoint - Update All Ranks

    @PostMapping("/admin/update-ranks")
    public ResponseEntity<Map<String, Object>> updateAllRanks() {
        Map<String, Object> response = new HashMap<>();
        try {
            leaderboardService.updateAllRanks();
            response.put("success", true);
            response.put("message", "All ranks updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update ranks: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}