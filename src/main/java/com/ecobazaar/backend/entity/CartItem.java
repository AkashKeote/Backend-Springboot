package com.ecobazaar.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "customer_username")
    private String customerUsername;
    
    @Column(name = "product_id", nullable = false)
    private String productId;
    
    @Column(name = "product_name")
    private String productName;
    
    @Column(name = "product_price", nullable = false)
    private Double productPrice = 0.0;
    
    // Additional price column to handle database schema
    @Column(name = "price")
    private Double price = 0.0;
    
    @Column(name = "product_image")
    private String productImage;
    
    @Column(name = "product_category")
    private String productCategory;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;
    
    @Column(name = "total_price")
    private Double totalPrice = 0.0;
    
    @Column(name = "carbon_footprint")
    private Double carbonFootprint = 0.0;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public CartItem() {}
    
    public CartItem(String userId, String customerUsername, String productId, String productName, Double productPrice, 
                   String productImage, String productCategory, Integer quantity, Double carbonFootprint) {
        this.userId = userId;
        this.customerUsername = customerUsername;
        this.productId = productId;
        this.productName = productName;
        this.productPrice = productPrice;
        this.price = productPrice; // Keep price field in sync
        this.productImage = productImage;
        this.productCategory = productCategory;
        this.quantity = quantity;
        this.carbonFootprint = carbonFootprint;
        this.totalPrice = productPrice * quantity;
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
    
    public String getCustomerUsername() {
        return customerUsername;
    }
    
    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public Double getProductPrice() {
        return productPrice;
    }
    
    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
        this.price = productPrice; // Keep price in sync with productPrice
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public String getProductImage() {
        return productImage;
    }
    
    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }
    
    public String getProductCategory() {
        return productCategory;
    }
    
    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Double getCarbonFootprint() {
        return carbonFootprint;
    }
    
    public void setCarbonFootprint(Double carbonFootprint) {
        this.carbonFootprint = carbonFootprint;
    }
    
    public Double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
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
    
    // Helper methods
    public void calculateTotalPrice() {
        this.totalPrice = (productPrice != null && quantity != null) ? productPrice * quantity : 0.0;
    }
    
    public Double getTotalCarbonFootprint() {
        return (carbonFootprint != null && quantity != null) ? carbonFootprint * quantity : 0.0;
    }
}
