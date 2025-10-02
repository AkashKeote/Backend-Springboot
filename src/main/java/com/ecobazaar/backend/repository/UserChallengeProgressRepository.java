package com.ecobazaar.backend.repository;

import com.ecobazaar.backend.entity.UserChallengeProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChallengeProgressRepository extends JpaRepository<UserChallengeProgress, Long> {
    
    // Find progress by user and challenge
    Optional<UserChallengeProgress> findByUserIdAndChallengeId(String userId, String challengeId);
    
    // Find all progress for a user
    List<UserChallengeProgress> findByUserId(String userId);
    
    // Find completed challenges for a user
    List<UserChallengeProgress> findByUserIdAndIsCompletedTrue(String userId);
    
    // Find active (incomplete) challenges for a user
    List<UserChallengeProgress> findByUserIdAndIsCompletedFalse(String userId);
    
    // Find progress by challenge ID
    List<UserChallengeProgress> findByChallengeId(String challengeId);
    
    // Get user's total earned points
    @Query("SELECT COALESCE(SUM(p.pointsEarned), 0) FROM UserChallengeProgress p WHERE p.userId = :userId AND p.isCompleted = true")
    Integer getTotalPointsEarnedByUser(@Param("userId") String userId);
    
    // Count completed challenges for a user
    @Query("SELECT COUNT(p) FROM UserChallengeProgress p WHERE p.userId = :userId AND p.isCompleted = true")
    Long countCompletedChallengesByUser(@Param("userId") String userId);
    
    // Count active challenges for a user
    @Query("SELECT COUNT(p) FROM UserChallengeProgress p WHERE p.userId = :userId AND p.isCompleted = false")
    Long countActiveChallengesByUser(@Param("userId") String userId);
    
    // Get average progress percentage for a user
    @Query("SELECT AVG(p.progressPercentage) FROM UserChallengeProgress p WHERE p.userId = :userId")
    Double getAverageProgressByUser(@Param("userId") String userId);
    
    // Find users who completed a specific challenge
    @Query("SELECT p.userId FROM UserChallengeProgress p WHERE p.challengeId = :challengeId AND p.isCompleted = true")
    List<String> findUsersWhoCompletedChallenge(@Param("challengeId") String challengeId);
    
    // Get challenge completion rate
    @Query("SELECT (COUNT(CASE WHEN p.isCompleted = true THEN 1 END) * 100.0 / COUNT(*)) FROM UserChallengeProgress p WHERE p.challengeId = :challengeId")
    Double getChallengeCompletionRate(@Param("challengeId") String challengeId);
    
    // Find top performing users by total points
    @Query("SELECT p.userId, SUM(p.pointsEarned) as totalPoints FROM UserChallengeProgress p WHERE p.isCompleted = true GROUP BY p.userId ORDER BY totalPoints DESC")
    List<Object[]> findTopUsersByPoints();
    
    // Find user's progress in specific categories
    @Query("SELECT p FROM UserChallengeProgress p JOIN p.ecoChallenge c WHERE p.userId = :userId AND c.category = :category")
    List<UserChallengeProgress> findUserProgressByCategory(@Param("userId") String userId, @Param("category") String category);
}