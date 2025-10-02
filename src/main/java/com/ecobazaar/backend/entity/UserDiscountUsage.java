package com.ecobazaar.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * UserDiscountUsage Entity
 * 
 * Tracks which discounts have been used by which users
 */
@Entity
@Table(name = "user_discount_usage")
public class UserDiscountUsage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String userId;
    
    @Column(nullable = false)
    private String discountCode;
    
    @Column(nullable = false)
    private String orderId;
    
    @Column(nullable = false)
    private Double discountAmount;
    
    @Column(nullable = false)
    private Double orderAmount;
    
    @CreationTimestamp
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    // Many-to-One relationship with EcoDiscount
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eco_discount_id", referencedColumnName = "id")
    private EcoDiscount ecoDiscount;
    
    // Constructors
    public UserDiscountUsage() {}
    
    public UserDiscountUsage(String userId, String discountCode, String orderId, 
                           Double discountAmount, Double orderAmount) {
        this.userId = userId;
        this.discountCode = discountCode;
        this.orderId = orderId;
        this.discountAmount = discountAmount;
        this.orderAmount = orderAmount;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getDiscountCode() {
        return discountCode;
    }
    
    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public Double getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public Double getOrderAmount() {
        return orderAmount;
    }
    
    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }
    
    public LocalDateTime getUsedAt() {
        return usedAt;
    }
    
    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }
    
    public EcoDiscount getEcoDiscount() {
        return ecoDiscount;
    }
    
    public void setEcoDiscount(EcoDiscount ecoDiscount) {
        this.ecoDiscount = ecoDiscount;
    }
}