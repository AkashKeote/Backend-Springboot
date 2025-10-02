package com.ecobazaar.backend.repository;

import com.ecobazaar.backend.entity.EcoChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EcoChallengeRepository extends JpaRepository<EcoChallenge, Long> {
    
    // Find by challenge ID
    Optional<EcoChallenge> findByChallengeId(String challengeId);
    
    // Find all active challenges
    List<EcoChallenge> findByIsActiveTrue();
    
    // Find challenges by category
    List<EcoChallenge> findByCategoryAndIsActiveTrue(String category);
    
    // Find challenges by creator
    List<EcoChallenge> findByCreatedByAndIsActiveTrue(String createdBy);
    
    // Find challenges by difficulty
    List<EcoChallenge> findByDifficultyAndIsActiveTrue(String difficulty);
    
    // Find challenges that are currently valid (within date range)
    @Query("SELECT c FROM EcoChallenge c WHERE c.isActive = true AND c.startDate <= :now AND c.endDate >= :now")
    List<EcoChallenge> findCurrentlyActiveChallenges(@Param("now") LocalDateTime now);
    
    // Find challenges by reward points range
    @Query("SELECT c FROM EcoChallenge c WHERE c.isActive = true AND c.rewardPoints BETWEEN :minPoints AND :maxPoints")
    List<EcoChallenge> findByRewardPointsRange(@Param("minPoints") Integer minPoints, @Param("maxPoints") Integer maxPoints);
    
    // Find challenges ending soon (within specified days)
    @Query("SELECT c FROM EcoChallenge c WHERE c.isActive = true AND c.endDate BETWEEN :now AND :endDate")
    List<EcoChallenge> findChallengesEndingSoon(@Param("now") LocalDateTime now, @Param("endDate") LocalDateTime endDate);
    
    // Get challenges sorted by reward points
    List<EcoChallenge> findByIsActiveTrueOrderByRewardPointsDesc();
    
    // Search challenges by title or description
    @Query("SELECT c FROM EcoChallenge c WHERE c.isActive = true AND (LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<EcoChallenge> searchChallenges(@Param("keyword") String keyword);
    
    // Count active challenges by category
    @Query("SELECT COUNT(c) FROM EcoChallenge c WHERE c.isActive = true AND c.category = :category")
    Long countActiveChallengesByCategory(@Param("category") String category);
}