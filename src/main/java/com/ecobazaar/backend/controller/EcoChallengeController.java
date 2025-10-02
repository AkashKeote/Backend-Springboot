package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.entity.EcoChallenge;
import com.ecobazaar.backend.entity.UserChallengeProgress;
import com.ecobazaar.backend.service.EcoChallengeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Eco Challenge Management
 */
@RestController
@RequestMapping("/api/eco-challenges")
@CrossOrigin(origins = "*")
public class EcoChallengeController {

    @Autowired
    private EcoChallengeService ecoChallengeService;

    // Challenge Management Endpoints

    @PostMapping
    public ResponseEntity<Map<String, Object>> createChallenge(@RequestBody EcoChallenge challenge) {
        Map<String, Object> response = new HashMap<>();
        try {
            EcoChallenge createdChallenge = ecoChallengeService.createChallenge(challenge);
            response.put("success", true);
            response.put("message", "Challenge created successfully");
            response.put("challenge", createdChallenge);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create challenge: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllActiveChallenges() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EcoChallenge> challenges = ecoChallengeService.getAllActiveChallenges();
            response.put("success", true);
            response.put("challenges", challenges);
            response.put("count", challenges.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch challenges: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getCurrentlyActiveChallenges() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EcoChallenge> challenges = ecoChallengeService.getCurrentlyActiveChallenges();
            response.put("success", true);
            response.put("challenges", challenges);
            response.put("count", challenges.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch active challenges: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{challengeId}")
    public ResponseEntity<Map<String, Object>> getChallengeById(@PathVariable String challengeId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<EcoChallenge> challengeOpt = ecoChallengeService.getChallengeByChallengeId(challengeId);
            if (challengeOpt.isPresent()) {
                response.put("success", true);
                response.put("challenge", challengeOpt.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Challenge not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch challenge: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getChallengesByCategory(@PathVariable String category) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EcoChallenge> challenges = ecoChallengeService.getChallengesByCategory(category);
            response.put("success", true);
            response.put("challenges", challenges);
            response.put("count", challenges.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch challenges by category: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<Map<String, Object>> getChallengesByDifficulty(@PathVariable String difficulty) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EcoChallenge> challenges = ecoChallengeService.getChallengesByDifficulty(difficulty);
            response.put("success", true);
            response.put("challenges", challenges);
            response.put("count", challenges.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch challenges by difficulty: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchChallenges(@RequestParam String keyword) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EcoChallenge> challenges = ecoChallengeService.searchChallenges(keyword);
            response.put("success", true);
            response.put("challenges", challenges);
            response.put("count", challenges.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to search challenges: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{challengeId}")
    public ResponseEntity<Map<String, Object>> updateChallenge(@PathVariable String challengeId, 
                                                              @RequestBody EcoChallenge challenge) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<EcoChallenge> existingChallengeOpt = ecoChallengeService.getChallengeByChallengeId(challengeId);
            if (existingChallengeOpt.isPresent()) {
                EcoChallenge existingChallenge = existingChallengeOpt.get();
                challenge.setId(existingChallenge.getId());
                challenge.setChallengeId(challengeId);
                
                EcoChallenge updatedChallenge = ecoChallengeService.updateChallenge(challenge);
                response.put("success", true);
                response.put("message", "Challenge updated successfully");
                response.put("challenge", updatedChallenge);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Challenge not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update challenge: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{challengeId}")
    public ResponseEntity<Map<String, Object>> deactivateChallenge(@PathVariable String challengeId) {
        Map<String, Object> response = new HashMap<>();
        try {
            ecoChallengeService.deactivateChallenge(challengeId);
            response.put("success", true);
            response.put("message", "Challenge deactivated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to deactivate challenge: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // User Progress Endpoints

    @PostMapping("/{challengeId}/start")
    public ResponseEntity<Map<String, Object>> startChallenge(@PathVariable String challengeId, 
                                                             @RequestParam String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserChallengeProgress progress = ecoChallengeService.startChallenge(userId, challengeId);
            response.put("success", true);
            response.put("message", "Challenge started successfully");
            response.put("progress", progress);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to start challenge: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{challengeId}/progress")
    public ResponseEntity<Map<String, Object>> updateProgress(@PathVariable String challengeId,
                                                             @RequestParam String userId,
                                                             @RequestParam Integer progress) {
        Map<String, Object> response = new HashMap<>();
        try {
            UserChallengeProgress updatedProgress = ecoChallengeService.updateProgress(userId, challengeId, progress);
            response.put("success", true);
            response.put("message", "Progress updated successfully");
            response.put("progress", updatedProgress);
            
            if (updatedProgress.getIsCompleted()) {
                response.put("completed", true);
                response.put("pointsEarned", updatedProgress.getPointsEarned());
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update progress: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user/{userId}/progress")
    public ResponseEntity<Map<String, Object>> getUserProgress(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<UserChallengeProgress> progress = ecoChallengeService.getUserProgress(userId);
            response.put("success", true);
            response.put("progress", progress);
            response.put("count", progress.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch user progress: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<Map<String, Object>> getUserCompletedChallenges(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<UserChallengeProgress> completed = ecoChallengeService.getUserCompletedChallenges(userId);
            response.put("success", true);
            response.put("completed", completed);
            response.put("count", completed.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch completed challenges: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<Map<String, Object>> getUserActiveChallenges(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<UserChallengeProgress> active = ecoChallengeService.getUserActiveChallenges(userId);
            response.put("success", true);
            response.put("active", active);
            response.put("count", active.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch active challenges: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Statistics and Analytics Endpoints

    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserChallengeStats(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = ecoChallengeService.getUserChallengeStats(userId);
            response.put("success", true);
            response.put("stats", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch user stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{challengeId}/analytics")
    public ResponseEntity<Map<String, Object>> getChallengeAnalytics(@PathVariable String challengeId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> analytics = ecoChallengeService.getChallengeAnalytics(challengeId);
            response.put("success", true);
            response.put("analytics", analytics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch challenge analytics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<Map<String, Object>> getLeaderboard() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> topUsers = ecoChallengeService.getTopUsersByPoints();
            response.put("success", true);
            response.put("leaderboard", topUsers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch leaderboard: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Administrative Endpoints

    @PostMapping("/initialize-sample-data")
    public ResponseEntity<Map<String, Object>> initializeSampleData() {
        Map<String, Object> response = new HashMap<>();
        try {
            ecoChallengeService.initializeSampleChallenges();
            response.put("success", true);
            response.put("message", "Sample challenges initialized successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to initialize sample data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}