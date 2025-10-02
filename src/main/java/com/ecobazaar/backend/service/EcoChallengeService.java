package com.ecobazaar.backend.service;

import com.ecobazaar.backend.entity.EcoChallenge;
import com.ecobazaar.backend.entity.UserChallengeProgress;
import com.ecobazaar.backend.repository.EcoChallengeRepository;
import com.ecobazaar.backend.repository.UserChallengeProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service class for managing Eco Challenges
 */
@Service
@Transactional
public class EcoChallengeService {

    @Autowired
    private EcoChallengeRepository ecoChallengeRepository;

    @Autowired
    private UserChallengeProgressRepository progressRepository;

    // Challenge Management
    public EcoChallenge createChallenge(EcoChallenge challenge) {
        // Generate unique challenge ID if not provided
        if (challenge.getChallengeId() == null || challenge.getChallengeId().isEmpty()) {
            challenge.setChallengeId(generateChallengeId());
        }
        
        // Set default values
        if (challenge.getStartDate() == null) {
            challenge.setStartDate(LocalDateTime.now());
        }
        if (challenge.getEndDate() == null) {
            challenge.setEndDate(LocalDateTime.now().plusDays(challenge.getDurationDays()));
        }
        
        return ecoChallengeRepository.save(challenge);
    }

    public Optional<EcoChallenge> getChallengeById(Long id) {
        return ecoChallengeRepository.findById(id);
    }

    public Optional<EcoChallenge> getChallengeByChallengeId(String challengeId) {
        return ecoChallengeRepository.findByChallengeId(challengeId);
    }

    public List<EcoChallenge> getAllActiveChallenges() {
        return ecoChallengeRepository.findByIsActiveTrue();
    }

    public List<EcoChallenge> getCurrentlyActiveChallenges() {
        return ecoChallengeRepository.findCurrentlyActiveChallenges(LocalDateTime.now());
    }

    public List<EcoChallenge> getChallengesByCategory(String category) {
        return ecoChallengeRepository.findByCategoryAndIsActiveTrue(category);
    }

    public List<EcoChallenge> getChallengesByDifficulty(String difficulty) {
        return ecoChallengeRepository.findByDifficultyAndIsActiveTrue(difficulty);
    }

    public List<EcoChallenge> searchChallenges(String keyword) {
        return ecoChallengeRepository.searchChallenges(keyword);
    }

    public EcoChallenge updateChallenge(EcoChallenge challenge) {
        return ecoChallengeRepository.save(challenge);
    }

    public void deleteChallenge(Long id) {
        ecoChallengeRepository.deleteById(id);
    }

    public void deactivateChallenge(String challengeId) {
        ecoChallengeRepository.findByChallengeId(challengeId).ifPresent(challenge -> {
            challenge.setIsActive(false);
            ecoChallengeRepository.save(challenge);
        });
    }

    // User Progress Management
    public UserChallengeProgress startChallenge(String userId, String challengeId) {
        // Check if user already has progress for this challenge
        Optional<UserChallengeProgress> existingProgress = 
            progressRepository.findByUserIdAndChallengeId(userId, challengeId);
        
        if (existingProgress.isPresent()) {
            return existingProgress.get();
        }

        // Create new progress entry
        UserChallengeProgress progress = new UserChallengeProgress(userId, challengeId);
        return progressRepository.save(progress);
    }

    public UserChallengeProgress updateProgress(String userId, String challengeId, Integer newProgress) {
        Optional<UserChallengeProgress> progressOpt = 
            progressRepository.findByUserIdAndChallengeId(userId, challengeId);
        
        if (progressOpt.isEmpty()) {
            // Start the challenge if not already started
            return startChallenge(userId, challengeId);
        }

        UserChallengeProgress progress = progressOpt.get();
        Optional<EcoChallenge> challengeOpt = ecoChallengeRepository.findByChallengeId(challengeId);
        
        if (challengeOpt.isEmpty()) {
            throw new RuntimeException("Challenge not found: " + challengeId);
        }

        EcoChallenge challenge = challengeOpt.get();
        
        // Update progress
        progress.setCurrentProgress(newProgress);
        
        // Calculate progress percentage
        double percentage = Math.min(100.0, (double) newProgress / challenge.getTargetValue() * 100);
        progress.setProgressPercentage(percentage);
        
        // Check if challenge is completed
        if (newProgress >= challenge.getTargetValue() && !progress.getIsCompleted()) {
            progress.setIsCompleted(true);
            progress.setCompletedAt(LocalDateTime.now());
            progress.setPointsEarned(challenge.getRewardPoints());
        }

        return progressRepository.save(progress);
    }

    public List<UserChallengeProgress> getUserProgress(String userId) {
        return progressRepository.findByUserId(userId);
    }

    public List<UserChallengeProgress> getUserCompletedChallenges(String userId) {
        return progressRepository.findByUserIdAndIsCompletedTrue(userId);
    }

    public List<UserChallengeProgress> getUserActiveChallenges(String userId) {
        return progressRepository.findByUserIdAndIsCompletedFalse(userId);
    }

    // Statistics and Analytics
    public Map<String, Object> getUserChallengeStats(String userId) {
        Map<String, Object> stats = new HashMap<>();
        
        Integer totalPoints = progressRepository.getTotalPointsEarnedByUser(userId);
        Long completedCount = progressRepository.countCompletedChallengesByUser(userId);
        Long activeCount = progressRepository.countActiveChallengesByUser(userId);
        Double averageProgress = progressRepository.getAverageProgressByUser(userId);
        
        int totalPointsValue = totalPoints != null ? totalPoints : 0;
        long completedCountValue = completedCount != null ? completedCount : 0;
        long activeCountValue = activeCount != null ? activeCount : 0;
        double averageProgressValue = averageProgress != null ? averageProgress : 0.0;
        
        stats.put("totalPoints", totalPointsValue);
        stats.put("completedChallenges", completedCountValue);
        stats.put("activeChallenges", activeCountValue);
        stats.put("averageProgress", averageProgressValue);
        
        long totalChallenges = completedCountValue + activeCountValue;
        double completionRate = totalChallenges > 0 ? (double) completedCountValue / totalChallenges : 0.0;
        stats.put("completionRate", completionRate);
        
        return stats;
    }

    public Map<String, Object> getChallengeAnalytics(String challengeId) {
        Map<String, Object> analytics = new HashMap<>();
        
        List<String> completedUsers = progressRepository.findUsersWhoCompletedChallenge(challengeId);
        Double completionRate = progressRepository.getChallengeCompletionRate(challengeId);
        List<UserChallengeProgress> allProgress = progressRepository.findByChallengeId(challengeId);
        
        analytics.put("totalParticipants", allProgress.size());
        analytics.put("completedUsers", completedUsers.size());
        analytics.put("completionRate", completionRate != null ? completionRate : 0.0);
        analytics.put("activeParticipants", allProgress.size() - completedUsers.size());
        
        return analytics;
    }

    public List<Map<String, Object>> getTopUsersByPoints() {
        List<Object[]> results = progressRepository.findTopUsersByPoints();
        List<Map<String, Object>> topUsers = new ArrayList<>();
        
        for (Object[] result : results) {
            Map<String, Object> user = new HashMap<>();
            user.put("userId", result[0]);
            user.put("totalPoints", result[1]);
            topUsers.add(user);
        }
        
        return topUsers;
    }

    // Sample Data Initialization
    public void initializeSampleChallenges() {
        if (ecoChallengeRepository.count() > 0) {
            return; // Already initialized
        }

        List<EcoChallenge> sampleChallenges = Arrays.asList(
            createSampleChallenge(
                "Plastic-Free Week",
                "Avoid single-use plastic for 7 consecutive days. Bring your own bags, bottles, and containers.",
                "Waste Reduction",
                7, "days", 7, 500,
                "EASY", "#B5C7F7", "recycling_rounded"
            ),
            createSampleChallenge(
                "Carbon Footprint Reduction",
                "Reduce your daily carbon footprint by 20%. Walk or cycle instead of driving, use public transport.",
                "Carbon Reduction",
                20, "% reduction", 30, 300,
                "MEDIUM", "#F9E79F", "eco_rounded"
            ),
            createSampleChallenge(
                "Local Shopping Spree",
                "Buy from 5 local eco-friendly stores. Support local businesses and reduce transportation emissions.",
                "Local Support",
                5, "stores", 14, 200,
                "EASY", "#E8D5C4", "store_rounded"
            ),
            createSampleChallenge(
                "Water Conservation",
                "Save 100 liters of water through mindful usage. Fix leaks, shorter showers, efficient appliances.",
                "Resource Conservation",
                100, "liters", 21, 250,
                "MEDIUM", "#D6EAF8", "water_drop_rounded"
            ),
            createSampleChallenge(
                "Energy Saver",
                "Reduce energy consumption by 50 kWh. Use LED bulbs, unplug devices, optimize heating/cooling.",
                "Energy Conservation",
                50, "kWh", 30, 400,
                "HARD", "#E8F5E8", "electric_bolt_rounded"
            )
        );

        ecoChallengeRepository.saveAll(sampleChallenges);
    }

    private EcoChallenge createSampleChallenge(String title, String description, String category,
                                              Integer targetValue, String targetUnit, Integer durationDays,
                                              Integer rewardPoints, String difficulty, String colorHex, String iconName) {
        EcoChallenge challenge = new EcoChallenge();
        challenge.setChallengeId(generateChallengeId());
        challenge.setTitle(title);
        challenge.setDescription(description);
        challenge.setCategory(category);
        challenge.setTargetValue(targetValue);
        challenge.setTargetUnit(targetUnit);
        challenge.setDurationDays(durationDays);
        challenge.setReward(rewardPoints + " Eco Points");
        challenge.setRewardPoints(rewardPoints);
        challenge.setDifficulty(difficulty);
        challenge.setColorHex(colorHex);
        challenge.setIconName(iconName);
        challenge.setCreatedBy("system");
        challenge.setStartDate(LocalDateTime.now());
        challenge.setEndDate(LocalDateTime.now().plusDays(durationDays));
        
        return challenge;
    }

    private String generateChallengeId() {
        LocalDateTime now = LocalDateTime.now();
        Random random = new Random();
        return String.format("CHALLENGE_%d%02d%02d_%d",
            now.getYear(), now.getMonthValue(), now.getDayOfMonth(),
            random.nextInt(1000));
    }
}