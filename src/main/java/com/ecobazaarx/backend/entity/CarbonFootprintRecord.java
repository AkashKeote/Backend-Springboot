package com.ecobazaarx.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity to store calculated carbon footprint records
 */
@Entity
@Table(name = "carbon_footprint_records", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_product_id", columnList = "product_id"),
    @Index(name = "idx_calculated_at", columnList = "calculated_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonFootprintRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "order_id")
    private String orderId;
    
    @Column(name = "product_id")
    private String productId;
    
    @Column(name = "product_name", nullable = false)
    private String productName;
    
    private String category;
    
    // Material emissions
    @Column(name = "material_type")
    private String materialType;
    
    @Column(name = "material_emissions")
    private Double materialEmissions = 0.0;
    
    // Manufacturing emissions
    @Column(name = "manufacturing_type")
    private String manufacturingType;
    
    @Column(name = "manufacturing_emissions")
    private Double manufacturingEmissions = 0.0;
    
    // Transportation emissions
    @Column(name = "transportation_type")
    private String transportationType;
    
    @Column(name = "transportation_distance")
    private Double transportationDistance;
    
    @Column(name = "transportation_emissions")
    private Double transportationEmissions = 0.0;
    
    // Packaging emissions
    @Column(name = "packaging_type")
    private String packagingType;
    
    @Column(name = "packaging_emissions")
    private Double packagingEmissions = 0.0;
    
    // End of life
    @Column(name = "end_of_life_emissions")
    private Double endOfLifeEmissions = 0.0;
    
    // Total and savings
    @Column(name = "total_carbon_footprint", nullable = false)
    private Double totalCarbonFootprint;
    
    @Column(name = "conventional_footprint")
    private Double conventionalFootprint;
    
    @Column(name = "carbon_savings")
    private Double carbonSavings = 0.0;
    
    @Column(name = "savings_percentage")
    private Double savingsPercentage = 0.0;
    
    @Column(name = "eco_rating")
    private String ecoRating; // A+, A, B+, B, C, D, F
    
    // Product details
    @Column(name = "product_weight")
    private Double productWeight;
    
    @Column(name = "is_recycled")
    private Boolean isRecycled = false;
    
    @Column(name = "is_organic")
    private Boolean isOrganic = false;
    
    @Column(name = "product_lifespan")
    private Double productLifespan;
    
    @Column(name = "source_country")
    private String sourceCountry;
    
    // Environmental equivalents
    @Column(name = "trees_equivalent")
    private Double treesEquivalent;
    
    @Column(name = "car_km_equivalent")
    private Double carKmEquivalent;
    
    @Column(name = "electricity_kwh_equivalent")
    private Double electricityKwhEquivalent;
    
    @Column(name = "plastic_bottles_equivalent")
    private Double plasticBottlesEquivalent;
    
    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @PrePersist
    protected void onCreate() {
        calculatedAt = LocalDateTime.now();
        calculateTotalsAndRating();
    }
    
    @PreUpdate
    protected void onUpdate() {
        calculateTotalsAndRating();
    }
    
    private void calculateTotalsAndRating() {
        // Calculate total footprint
        totalCarbonFootprint = materialEmissions + manufacturingEmissions + 
                              transportationEmissions + packagingEmissions + 
                              endOfLifeEmissions;
        
        // Calculate savings and percentage
        if (conventionalFootprint != null && conventionalFootprint > 0) {
            carbonSavings = conventionalFootprint - totalCarbonFootprint;
            savingsPercentage = (carbonSavings / conventionalFootprint) * 100;
            
            // Determine eco rating
            if (savingsPercentage >= 70) {
                ecoRating = "A+";
            } else if (savingsPercentage >= 50) {
                ecoRating = "A";
            } else if (savingsPercentage >= 30) {
                ecoRating = "B+";
            } else if (savingsPercentage >= 15) {
                ecoRating = "B";
            } else if (savingsPercentage >= 0) {
                ecoRating = "C";
            } else if (savingsPercentage >= -20) {
                ecoRating = "D";
            } else {
                ecoRating = "F";
            }
        }
        
        // Calculate environmental equivalents
        if (totalCarbonFootprint != null) {
            treesEquivalent = totalCarbonFootprint * 0.05; // 1 tree absorbs ~20kg CO2/year
            carKmEquivalent = totalCarbonFootprint * 4.6; // Average car emits ~0.22kg CO2/km
            electricityKwhEquivalent = totalCarbonFootprint * 1.9; // Grid average ~0.53kg CO2/kWh
            plasticBottlesEquivalent = totalCarbonFootprint * 50; // 1 bottle ~0.02kg CO2
        }
    }
}
