package com.ecobazaar.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * EcoDiscount Entity
 * 
 * Represents discount offers for eco-friendly products and sustainable shopping
 */
@Entity
@Table(name = "eco_discounts")
public class EcoDiscount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String discountCode;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String discountType; // PERCENTAGE, FIXED_AMOUNT, FREE_SHIPPING
    
    @Column(nullable = false)
    private Double discountValue; // percentage or fixed amount
    
    @Column
    private Double minimumOrderAmount;
    
    @Column
    private Double maximumDiscountAmount;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column
    private String applicableCategory; // specific category or null for all
    
    @Column
    private String applicableStoreId; // specific store or null for all
    
    @Column
    private Integer usageLimit; // null for unlimited
    
    @Column
    private Integer currentUsageCount = 0;
    
    @Column
    private Integer userUsageLimit = 1; // how many times one user can use this
    
    @Column
    private Boolean requiresEcoPoints = false;
    
    @Column
    private Integer requiredEcoPoints = 0;
    
    @Column
    private LocalDateTime validFrom;
    
    @Column
    private LocalDateTime validUntil;
    
    @Column
    private String createdBy; // admin user ID
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public EcoDiscount() {}
    
    public EcoDiscount(String discountCode, String title, String description, 
                      String discountType, Double discountValue) {
        this.discountCode = discountCode;
        this.title = title;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.validFrom = LocalDateTime.now();
        this.validUntil = LocalDateTime.now().plusDays(30); // Default 30 days validity
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDiscountCode() {
        return discountCode;
    }
    
    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getDiscountType() {
        return discountType;
    }
    
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }
    
    public Double getDiscountValue() {
        return discountValue;
    }
    
    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }
    
    public Double getMinimumOrderAmount() {
        return minimumOrderAmount;
    }
    
    public void setMinimumOrderAmount(Double minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }
    
    public Double getMaximumDiscountAmount() {
        return maximumDiscountAmount;
    }
    
    public void setMaximumDiscountAmount(Double maximumDiscountAmount) {
        this.maximumDiscountAmount = maximumDiscountAmount;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getApplicableCategory() {
        return applicableCategory;
    }
    
    public void setApplicableCategory(String applicableCategory) {
        this.applicableCategory = applicableCategory;
    }
    
    public String getApplicableStoreId() {
        return applicableStoreId;
    }
    
    public void setApplicableStoreId(String applicableStoreId) {
        this.applicableStoreId = applicableStoreId;
    }
    
    public Integer getUsageLimit() {
        return usageLimit;
    }
    
    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }
    
    public Integer getCurrentUsageCount() {
        return currentUsageCount;
    }
    
    public void setCurrentUsageCount(Integer currentUsageCount) {
        this.currentUsageCount = currentUsageCount;
    }
    
    public Integer getUserUsageLimit() {
        return userUsageLimit;
    }
    
    public void setUserUsageLimit(Integer userUsageLimit) {
        this.userUsageLimit = userUsageLimit;
    }
    
    public Boolean getRequiresEcoPoints() {
        return requiresEcoPoints;
    }
    
    public void setRequiresEcoPoints(Boolean requiresEcoPoints) {
        this.requiresEcoPoints = requiresEcoPoints;
    }
    
    public Integer getRequiredEcoPoints() {
        return requiredEcoPoints;
    }
    
    public void setRequiredEcoPoints(Integer requiredEcoPoints) {
        this.requiredEcoPoints = requiredEcoPoints;
    }
    
    public LocalDateTime getValidFrom() {
        return validFrom;
    }
    
    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }
    
    public LocalDateTime getValidUntil() {
        return validUntil;
    }
    
    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
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