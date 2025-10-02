package com.ecobazaarx.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarbonFootprintResponse {
    
    // Basic information
    private Long recordId;
    private String productName;
    private String category;
    
    // Emission breakdown (in kg CO2e)
    private Double materialEmissions;
    private Double manufacturingEmissions;
    private Double transportationEmissions;
    private Double packagingEmissions;
    private Double endOfLifeEmissions;
    
    // Total footprint and savings
    private Double totalCarbonFootprint;
    private Double conventionalFootprint;
    private Double carbonSavings;
    private Double savingsPercentage;
    
    // Rating
    private String ecoRating; // A+, A, B+, B, C, D, F
    private String ratingDescription;
    
    // Environmental equivalents
    private Map<String, Double> equivalentImpacts;
    
    // Product details
    private Double productWeight;
    private String materialType;
    private String manufacturingType;
    private String transportationType;
    private Double transportationDistance;
    private String packagingType;
    private Boolean isRecycled;
    private Boolean isOrganic;
    private Double productLifespan;
    
    // Calculation metadata
    private LocalDateTime calculatedAt;
    private String calculationMethod;
    private String dataSource;
    
    // Recommendations
    private String sustainabilityTips;
    private String improvementSuggestions;
    
    // Build equivalent impacts map
    public void buildEquivalentImpacts(Double trees, Double carKm, Double electricityKwh, Double plasticBottles) {
        this.equivalentImpacts = Map.of(
            "trees_planted", trees,
            "car_km_avoided", carKm,
            "electricity_kwh_saved", electricityKwh,
            "plastic_bottles_avoided", plasticBottles
        );
    }
    
    // Generate rating description
    public void generateRatingDescription() {
        switch (ecoRating) {
            case "A+":
                ratingDescription = "Excellent! This product saves 70%+ carbon compared to conventional alternatives.";
                break;
            case "A":
                ratingDescription = "Great! This product saves 50-70% carbon compared to conventional alternatives.";
                break;
            case "B+":
                ratingDescription = "Good! This product saves 30-50% carbon compared to conventional alternatives.";
                break;
            case "B":
                ratingDescription = "Fair! This product saves 15-30% carbon compared to conventional alternatives.";
                break;
            case "C":
                ratingDescription = "Average. This product has similar carbon impact to conventional alternatives.";
                break;
            case "D":
                ratingDescription = "Below Average. This product has 0-20% higher carbon emissions.";
                break;
            case "F":
                ratingDescription = "Poor. This product has 20%+ higher carbon emissions than alternatives.";
                break;
            default:
                ratingDescription = "Rating not available.";
        }
    }
}
