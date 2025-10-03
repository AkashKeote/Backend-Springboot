package com.ecobazaar.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity representing emission factors for carbon footprint calculation
 * Based on IPCC Guidelines, Ecoinvent Database, and EPA Guidelines
 */
@Entity
@Table(name = "emission_factors", indexes = {
    @Index(name = "idx_category", columnList = "category"),
    @Index(name = "idx_subcategory", columnList = "subcategory"),
    @Index(name = "idx_material_type", columnList = "material_type"),
    @Index(name = "idx_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmissionFactor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String category; // MATERIAL, TRANSPORTATION, MANUFACTURING, PACKAGING, END_OF_LIFE
    
    @Column(nullable = false)
    private String subcategory; // e.g., textiles, plastics, metals
    
    @Column(name = "material_type", nullable = false)
    private String materialType; // e.g., organic_cotton, recycled_plastic
    
    @Column(nullable = false)
    private String name; // Human-readable name
    
    @Column(nullable = false)
    private String unit; // e.g., kg CO2e per kg, kg CO2e per km/kg
    
    @Column(name = "emission_factor", nullable = false)
    private Double emissionFactor; // The actual emission value
    
    private String description;
    
    @Column(name = "data_source")
    private String dataSource; // e.g., "IPCC 2021", "Ecoinvent v3.8"
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    // Additional metadata
    @Column(name = "carbon_sequestration")
    private Double carbonSequestration; // For materials that absorb CO2 (negative values)
    
    @Column(name = "recycling_benefit")
    private Double recyclingBenefit; // CO2 saved when recycled
    
    @Column(name = "biodegradation_rate")
    private Double biodegradationRate; // Percentage that biodegrades
    
    private String region; // Geographic region if applicable
    
    @Column(name = "confidence_level")
    private String confidenceLevel; // HIGH, MEDIUM, LOW
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
