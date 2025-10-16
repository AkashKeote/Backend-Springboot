package com.ecobazaar.backend.repository;

import com.ecobazaar.backend.entity.EcoDiscount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EcoDiscountRepository extends JpaRepository<EcoDiscount, Long> {
    
    // Find by discount code
    Optional<EcoDiscount> findByDiscountCode(String discountCode);
    
    // Find all active discounts
    List<EcoDiscount> findByIsActiveTrue();
    
    // Find currently valid discounts
    @Query("SELECT d FROM EcoDiscount d WHERE d.isActive = true AND d.validFrom <= :now AND d.validUntil >= :now")
    List<EcoDiscount> findCurrentlyValidDiscounts(@Param("now") LocalDateTime now);
    
    // Find discounts by type
    List<EcoDiscount> findByDiscountTypeAndIsActiveTrue(String discountType);
    
    // Find discounts for specific category
    List<EcoDiscount> findByApplicableCategoryAndIsActiveTrue(String category);
    
    // Find discounts for specific store
    List<EcoDiscount> findByApplicableStoreIdAndIsActiveTrue(String storeId);
    
    // Find discounts that don't require eco points
    List<EcoDiscount> findByIsActiveTrueAndRequiresEcoPointsFalse();
    
    // Find discounts by eco points requirement
    List<EcoDiscount> findByIsActiveTrueAndRequiresEcoPointsTrueAndRequiredEcoPointsLessThanEqual(Integer userEcoPoints);
    
    // Find discounts by minimum order amount
    @Query("SELECT d FROM EcoDiscount d WHERE d.isActive = true AND (d.minimumOrderAmount IS NULL OR d.minimumOrderAmount <= :orderAmount)")
    List<EcoDiscount> findApplicableDiscountsForOrderAmount(@Param("orderAmount") Double orderAmount);
    
    // Find available discounts for user (considering usage limits)
    @Query("SELECT d FROM EcoDiscount d WHERE d.isActive = true AND d.validFrom <= :now AND d.validUntil >= :now AND (d.usageLimit IS NULL OR d.currentUsageCount < d.usageLimit)")
    List<EcoDiscount> findAvailableDiscounts(@Param("now") LocalDateTime now);
    
    // Find discounts expiring soon
    @Query("SELECT d FROM EcoDiscount d WHERE d.isActive = true AND d.validUntil BETWEEN :now AND :expiryDate")
    List<EcoDiscount> findDiscountsExpiringSoon(@Param("now") LocalDateTime now, @Param("expiryDate") LocalDateTime expiryDate);
    
    // Find most used discounts
    List<EcoDiscount> findByIsActiveTrueOrderByCurrentUsageCountDesc();
    
    // Find discounts by creator
    List<EcoDiscount> findByCreatedByAndIsActiveTrue(String createdBy);
    
    // Search discounts by title or description
    @Query("SELECT d FROM EcoDiscount d WHERE d.isActive = true AND (LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<EcoDiscount> searchDiscounts(@Param("keyword") String keyword);
}