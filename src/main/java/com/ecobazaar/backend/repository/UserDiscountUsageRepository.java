package com.ecobazaar.backend.repository;

import com.ecobazaar.backend.entity.UserDiscountUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserDiscountUsageRepository extends JpaRepository<UserDiscountUsage, Long> {
    
    // Find usage by user ID
    List<UserDiscountUsage> findByUserId(String userId);
    
    // Find usage by discount code
    List<UserDiscountUsage> findByDiscountCode(String discountCode);
    
    // Find usage by user and discount code
    List<UserDiscountUsage> findByUserIdAndDiscountCode(String userId, String discountCode);
    
    // Count how many times a user has used a specific discount
    @Query("SELECT COUNT(u) FROM UserDiscountUsage u WHERE u.userId = :userId AND u.discountCode = :discountCode")
    Long countUserDiscountUsage(@Param("userId") String userId, @Param("discountCode") String discountCode);
    
    // Get total discount amount saved by user
    @Query("SELECT COALESCE(SUM(u.discountAmount), 0) FROM UserDiscountUsage u WHERE u.userId = :userId")
    Double getTotalDiscountSavedByUser(@Param("userId") String userId);
    
    // Get usage statistics for a discount
    @Query("SELECT COUNT(u), AVG(u.discountAmount), SUM(u.discountAmount) FROM UserDiscountUsage u WHERE u.discountCode = :discountCode")
    Object[] getDiscountUsageStats(@Param("discountCode") String discountCode);
    
    // Find usage within date range
    List<UserDiscountUsage> findByUsedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find most popular discounts
    @Query("SELECT u.discountCode, COUNT(u) as usageCount FROM UserDiscountUsage u GROUP BY u.discountCode ORDER BY usageCount DESC")
    List<Object[]> findMostPopularDiscounts();
    
    // Find user's recent discount usage
    List<UserDiscountUsage> findByUserIdOrderByUsedAtDesc(String userId);
    
    // Get total orders with discounts by user
    @Query("SELECT COUNT(DISTINCT u.orderId) FROM UserDiscountUsage u WHERE u.userId = :userId")
    Long countOrdersWithDiscountsByUser(@Param("userId") String userId);
}