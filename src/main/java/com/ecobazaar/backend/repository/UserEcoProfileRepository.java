package com.ecobazaar.backend.repository;

import com.ecobazaar.backend.entity.UserEcoProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserEcoProfileRepository extends JpaRepository<UserEcoProfile, Long> {
    
    // Find by user ID
    Optional<UserEcoProfile> findByUserId(String userId);
    
    // Leaderboard Queries - By Eco Points
    @Query("SELECT u FROM UserEcoProfile u WHERE u.showOnLeaderboard = true ORDER BY u.totalEcoPoints DESC")
    Page<UserEcoProfile> findLeaderboardByEcoPoints(Pageable pageable);
    
    @Query("SELECT u FROM UserEcoProfile u WHERE u.showOnLeaderboard = true ORDER BY u.totalEcoPoints DESC")
    List<UserEcoProfile> findTop10ByEcoPoints(Pageable pageable);
    
    @Query("SELECT u FROM UserEcoProfile u WHERE u.showOnLeaderboard = true ORDER BY u.totalEcoPoints DESC")
    List<UserEcoProfile> findTop100ByEcoPoints(Pageable pageable);
    
    // Leaderboard Queries - By Carbon Saved
    @Query("SELECT u FROM UserEcoProfile u WHERE u.showOnLeaderboard = true ORDER BY u.totalCarbonSaved DESC")
    Page<UserEcoProfile> findLeaderboardByCarbonSaved(Pageable pageable);
    
    @Query("SELECT u FROM UserEcoProfile u WHERE u.showOnLeaderboard = true ORDER BY u.totalCarbonSaved DESC")
    List<UserEcoProfile> findTop10ByCarbonSaved(Pageable pageable);
    
    // Leaderboard Queries - By Challenges Completed
    @Query("SELECT u FROM UserEcoProfile u WHERE u.showOnLeaderboard = true ORDER BY u.totalChallengesCompleted DESC")
    Page<UserEcoProfile> findLeaderboardByChallengesCompleted(Pageable pageable);
    
    @Query("SELECT u FROM UserEcoProfile u WHERE u.showOnLeaderboard = true ORDER BY u.totalChallengesCompleted DESC")
    List<UserEcoProfile> findTop10ByChallengesCompleted(Pageable pageable);
    
    // Leaderboard Queries - By Level
    @Query("SELECT u FROM UserEcoProfile u WHERE u.showOnLeaderboard = true ORDER BY u.ecoLevel DESC, u.totalEcoPoints DESC")
    Page<UserEcoProfile> findLeaderboardByLevel(Pageable pageable);
    
    // Leaderboard Queries - By Streak
    @Query("SELECT u FROM UserEcoProfile u WHERE u.showOnLeaderboard = true ORDER BY u.currentStreakDays DESC")
    List<UserEcoProfile> findTop10ByCurrentStreak(Pageable pageable);
    
    @Query("SELECT u FROM UserEcoProfile u WHERE u.showOnLeaderboard = true ORDER BY u.longestStreakDays DESC")
    List<UserEcoProfile> findTop10ByLongestStreak(Pageable pageable);
    
    // Get user's rank by eco points
    @Query("SELECT COUNT(u) + 1 FROM UserEcoProfile u WHERE u.showOnLeaderboard = true AND u.totalEcoPoints > (SELECT p.totalEcoPoints FROM UserEcoProfile p WHERE p.userId = :userId)")
    Long getUserRankByEcoPoints(@Param("userId") String userId);
    
    // Get user's rank by carbon saved
    @Query("SELECT COUNT(u) + 1 FROM UserEcoProfile u WHERE u.showOnLeaderboard = true AND u.totalCarbonSaved > (SELECT p.totalCarbonSaved FROM UserEcoProfile p WHERE p.userId = :userId)")
    Long getUserRankByCarbonSaved(@Param("userId") String userId);
    
    // Find users by level
    List<UserEcoProfile> findByEcoLevel(Integer ecoLevel);
    
    // Find users by level range
    @Query("SELECT u FROM UserEcoProfile u WHERE u.ecoLevel BETWEEN :minLevel AND :maxLevel ORDER BY u.ecoLevel DESC, u.totalEcoPoints DESC")
    List<UserEcoProfile> findByEcoLevelRange(@Param("minLevel") Integer minLevel, @Param("maxLevel") Integer maxLevel);
    
    // Get total eco points across all users
    @Query("SELECT COALESCE(SUM(u.totalEcoPoints), 0) FROM UserEcoProfile u")
    Long getTotalEcoPointsAcrossAllUsers();
    
    // Get total carbon saved across all users
    @Query("SELECT COALESCE(SUM(u.totalCarbonSaved), 0.0) FROM UserEcoProfile u")
    Double getTotalCarbonSavedAcrossAllUsers();
    
    // Get average eco points
    @Query("SELECT COALESCE(AVG(u.totalEcoPoints), 0.0) FROM UserEcoProfile u")
    Double getAverageEcoPoints();
    
    // Find users with active streaks
    @Query("SELECT u FROM UserEcoProfile u WHERE u.currentStreakDays > 0 ORDER BY u.currentStreakDays DESC")
    List<UserEcoProfile> findUsersWithActiveStreaks();
    
    // Find recently active users
    @Query("SELECT u FROM UserEcoProfile u WHERE u.lastActivityDate >= :since ORDER BY u.lastActivityDate DESC")
    List<UserEcoProfile> findRecentlyActiveUsers(@Param("since") LocalDateTime since);
    
    // Find top performers by multiple criteria
    @Query("SELECT u FROM UserEcoProfile u WHERE u.showOnLeaderboard = true ORDER BY (u.totalEcoPoints * 0.4 + u.totalCarbonSaved * 100 * 0.3 + u.totalChallengesCompleted * 50 * 0.3) DESC")
    Page<UserEcoProfile> findTopPerformers(Pageable pageable);
    
    // Find users by eco points range
    @Query("SELECT u FROM UserEcoProfile u WHERE u.totalEcoPoints BETWEEN :minPoints AND :maxPoints ORDER BY u.totalEcoPoints DESC")
    List<UserEcoProfile> findByEcoPointsRange(@Param("minPoints") Integer minPoints, @Param("maxPoints") Integer maxPoints);
    
    // Count users by level
    @Query("SELECT COUNT(u) FROM UserEcoProfile u WHERE u.ecoLevel = :level")
    Long countUsersByLevel(@Param("level") Integer level);
    
    // Get level distribution
    @Query("SELECT u.ecoLevel, u.levelName, COUNT(u) FROM UserEcoProfile u GROUP BY u.ecoLevel, u.levelName ORDER BY u.ecoLevel DESC")
    List<Object[]> getLevelDistribution();
    
    // Find users near a specific user's rank
    @Query(value = "SELECT * FROM user_eco_profiles WHERE show_on_leaderboard = true ORDER BY total_eco_points DESC LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<UserEcoProfile> findUsersNearRank(@Param("offset") int offset, @Param("limit") int limit);
    
    // Search users by name
    @Query("SELECT u FROM UserEcoProfile u WHERE u.showOnLeaderboard = true AND LOWER(u.userName) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY u.totalEcoPoints DESC")
    List<UserEcoProfile> searchUsersByName(@Param("keyword") String keyword);
    
    // Get monthly leaders
    @Query("SELECT u FROM UserEcoProfile u WHERE u.showOnLeaderboard = true AND u.lastActivityDate >= :monthStart ORDER BY u.totalEcoPoints DESC")
    List<UserEcoProfile> findMonthlyLeaders(@Param("monthStart") LocalDateTime monthStart, Pageable pageable);
}