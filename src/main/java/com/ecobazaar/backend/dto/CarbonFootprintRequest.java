package com.ecobazaar.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonFootprintRequest {
    
    // Basic product information
    private String productId;
    private String productName;
    private String category;
    private Double weight; // in kg
    
    // Material information
    private String material; // e.g., "organic_cotton", "recycled_plastic"
    private Boolean isRecycled = false;
    private Boolean isOrganic = false;
    
    // Manufacturing
    private String manufacturingType; // e.g., "eco_friendly", "conventional"
    
    // Transportation
    private Double transportationDistance; // in km
    private String transportationType; // e.g., "truck_local", "ship_freight", "air_freight"
    private String sourceCountry;
    
    // Packaging
    private String packagingType; // e.g., "biodegradable_packaging", "recycled_cardboard"
    
    // Product lifecycle
    private Double productLifespan; // in years
    
    // User information (optional)
    private String userId;
    private String orderId;
    
    // Additional notes
    private String notes;
}
