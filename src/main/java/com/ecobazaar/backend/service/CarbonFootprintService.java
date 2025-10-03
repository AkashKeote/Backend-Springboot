package com.ecobazaar.backend.service;

import com.ecobazaar.backend.dto.CarbonFootprintRequest;
import com.ecobazaar.backend.dto.CarbonFootprintResponse;
import com.ecobazaar.backend.entity.CarbonFootprintRecord;
import com.ecobazaar.backend.entity.EmissionFactor;
import com.ecobazaar.backend.repository.CarbonFootprintRecordRepository;
import com.ecobazaar.backend.repository.EmissionFactorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service for calculating carbon footprint using real scientific formulas
 * Based on IPCC Guidelines, Ecoinvent Database, EPA Guidelines, and ISO 14040/14044
 */
@Service
@Slf4j
public class CarbonFootprintService {

    @Autowired
    private EmissionFactorRepository emissionFactorRepo;
    
    @Autowired
    private CarbonFootprintRecordRepository carbonRecordRepo;

    /**
     * Calculate complete carbon footprint for a product
     */
    @Transactional
    public CarbonFootprintResponse calculateProductCarbonFootprint(CarbonFootprintRequest request) {
        log.info("Calculating carbon footprint for product: {}", request.getProductName());
        
        CarbonFootprintRecord record = new CarbonFootprintRecord();
        
        // Basic information
        record.setUserId(request.getUserId());
        record.setOrderId(request.getOrderId());
        record.setProductId(request.getProductId());
        record.setProductName(request.getProductName());
        record.setCategory(request.getCategory());
        record.setProductWeight(request.getWeight());
        record.setIsRecycled(request.getIsRecycled());
        record.setIsOrganic(request.getIsOrganic());
        record.setProductLifespan(request.getProductLifespan());
        record.setSourceCountry(request.getSourceCountry());
        record.setNotes(request.getNotes());
        
        // Calculate material emissions
        Double materialEmissions = calculateMaterialEmissions(
            request.getMaterial(), 
            request.getWeight(), 
            request.getIsRecycled()
        );
        record.setMaterialType(request.getMaterial());
        record.setMaterialEmissions(materialEmissions);
        
        // Calculate manufacturing emissions
        Double manufacturingEmissions = calculateManufacturingEmissions(
            request.getCategory(), 
            request.getManufacturingType()
        );
        record.setManufacturingType(request.getManufacturingType());
        record.setManufacturingEmissions(manufacturingEmissions);
        
        // Calculate transportation emissions
        Double transportationEmissions = calculateTransportationEmissions(
            request.getTransportationType(),
            request.getTransportationDistance(),
            request.getWeight()
        );
        record.setTransportationType(request.getTransportationType());
        record.setTransportationDistance(request.getTransportationDistance());
        record.setTransportationEmissions(transportationEmissions);
        
        // Calculate packaging emissions
        Double packagingEmissions = calculatePackagingEmissions(
            request.getPackagingType(),
            request.getWeight()
        );
        record.setPackagingType(request.getPackagingType());
        record.setPackagingEmissions(packagingEmissions);
        
        // Calculate end-of-life emissions
        Double endOfLifeEmissions = calculateEndOfLifeEmissions(
            request.getMaterial(),
            request.getWeight(),
            request.getIsRecycled()
        );
        record.setEndOfLifeEmissions(endOfLifeEmissions);
        
        // Calculate conventional footprint for comparison
        Double conventionalFootprint = calculateConventionalFootprint(
            request.getCategory(),
            request.getWeight()
        );
        record.setConventionalFootprint(conventionalFootprint);
        
        // Save record (this will auto-calculate totals, ratings, and equivalents)
        CarbonFootprintRecord savedRecord = carbonRecordRepo.save(record);
        
        // Build and return response
        return buildResponse(savedRecord);
    }
    
    /**
     * Calculate material emissions based on material type and weight
     */
    private Double calculateMaterialEmissions(String materialType, Double weight, Boolean isRecycled) {
        if (weight == null || weight <= 0) return 0.0;
        
        Optional<EmissionFactor> factorOpt = emissionFactorRepo.findByMaterialTypeAndIsActive(
            materialType, true
        );
        
        if (factorOpt.isPresent()) {
            EmissionFactor factor = factorOpt.get();
            Double emissions = weight * factor.getEmissionFactor();
            
            // Apply recycling benefit if applicable
            if (isRecycled && factor.getRecyclingBenefit() != null) {
                emissions -= (weight * factor.getRecyclingBenefit());
            }
            
            // Apply carbon sequestration if applicable (negative emissions)
            if (factor.getCarbonSequestration() != null) {
                emissions += (weight * factor.getCarbonSequestration());
            }
            
            return Math.max(0.0, emissions); // Ensure non-negative
        }
        
        // Default material emission factor if not found
        log.warn("Material emission factor not found for: {}, using default", materialType);
        return weight * 5.0; // Default: 5 kg CO2e per kg
    }
    
    /**
     * Calculate manufacturing emissions based on category and type
     */
    private Double calculateManufacturingEmissions(String category, String manufacturingType) {
        List<EmissionFactor> factors = emissionFactorRepo.findManufacturingFactorsByType(
            category != null ? category.toLowerCase() : "general"
        );
        
        for (EmissionFactor factor : factors) {
            if (factor.getMaterialType().equalsIgnoreCase(manufacturingType)) {
                return factor.getEmissionFactor();
            }
        }
        
        // Default manufacturing emissions
        if ("eco_friendly".equalsIgnoreCase(manufacturingType)) {
            return 1.5; // Default eco-friendly: 1.5 kg CO2e
        } else {
            return 3.5; // Default conventional: 3.5 kg CO2e
        }
    }
    
    /**
     * Calculate transportation emissions
     * Formula: distance (km) × weight (kg) × emission factor (kg CO2e per km per kg)
     */
    private Double calculateTransportationEmissions(String transportationType, 
                                                   Double distance, Double weight) {
        if (distance == null || distance <= 0 || weight == null || weight <= 0) {
            return 0.0;
        }
        
        Optional<EmissionFactor> factorOpt = emissionFactorRepo.findByCategorySubcategoryAndMaterial(
            "TRANSPORTATION", 
            "freight",
            transportationType
        );
        
        if (factorOpt.isPresent()) {
            return distance * weight * factorOpt.get().getEmissionFactor();
        }
        
        // Default transportation emission factors (kg CO2e per km per kg)
        Map<String, Double> defaultFactors = Map.of(
            "truck_local", 0.000089,
            "truck_long_distance", 0.000062,
            "ship_freight", 0.000011,
            "air_freight", 0.000602,
            "electric_vehicle", 0.000025,
            "rail_freight", 0.000022
        );
        
        Double factor = defaultFactors.getOrDefault(transportationType, 0.00006);
        return distance * weight * factor;
    }
    
    /**
     * Calculate packaging emissions
     */
    private Double calculatePackagingEmissions(String packagingType, Double weight) {
        if (weight == null || weight <= 0) return 0.0;
        
        Optional<EmissionFactor> factorOpt = emissionFactorRepo.findByCategorySubcategoryAndMaterial(
            "PACKAGING",
            "general",
            packagingType
        );
        
        if (factorOpt.isPresent()) {
            // Packaging emissions typically 10-15% of product weight
            return (weight * 0.12) * factorOpt.get().getEmissionFactor();
        }
        
        // Default packaging emissions
        Map<String, Double> defaultFactors = Map.of(
            "biodegradable_packaging", 0.3,
            "recycled_cardboard", 0.5,
            "virgin_plastic", 6.0,
            "recycled_plastic", 2.0,
            "paper", 1.2,
            "no_packaging", 0.0
        );
        
        Double factor = defaultFactors.getOrDefault(packagingType, 1.5);
        return (weight * 0.12) * factor;
    }
    
    /**
     * Calculate end-of-life emissions
     */
    private Double calculateEndOfLifeEmissions(String materialType, Double weight, Boolean isRecycled) {
        if (weight == null || weight <= 0) return 0.0;
        
        Optional<EmissionFactor> factorOpt = emissionFactorRepo.findByCategorySubcategoryAndMaterial(
            "END_OF_LIFE",
            "disposal",
            materialType
        );
        
        if (factorOpt.isPresent()) {
            EmissionFactor factor = factorOpt.get();
            
            // If recycled or biodegradable, reduce end-of-life emissions
            if (isRecycled || (factor.getBiodegradationRate() != null && factor.getBiodegradationRate() > 50)) {
                return weight * factor.getEmissionFactor() * 0.3; // 70% reduction
            }
            
            return weight * factor.getEmissionFactor();
        }
        
        // Default end-of-life emissions (landfill scenario)
        return isRecycled ? (weight * 0.1) : (weight * 0.5);
    }
    
    /**
     * Calculate conventional product footprint for comparison
     */
    private Double calculateConventionalFootprint(String category, Double weight) {
        if (weight == null || weight <= 0) return 0.0;
        
        // Conventional footprint multipliers by category
        Map<String, Double> conventionalMultipliers = Map.of(
            "Clothing", 12.0,
            "Electronics", 20.0,
            "Food", 8.0,
            "Home & Garden", 10.0,
            "Beauty & Personal Care", 7.0,
            "Sports & Outdoors", 11.0,
            "Books & Stationery", 5.0
        );
        
        Double multiplier = conventionalMultipliers.getOrDefault(category, 10.0);
        return weight * multiplier;
    }
    
    /**
     * Build response from saved record
     */
    private CarbonFootprintResponse buildResponse(CarbonFootprintRecord record) {
        CarbonFootprintResponse response = CarbonFootprintResponse.builder()
            .recordId(record.getId())
            .productName(record.getProductName())
            .category(record.getCategory())
            .materialEmissions(record.getMaterialEmissions())
            .manufacturingEmissions(record.getManufacturingEmissions())
            .transportationEmissions(record.getTransportationEmissions())
            .packagingEmissions(record.getPackagingEmissions())
            .endOfLifeEmissions(record.getEndOfLifeEmissions())
            .totalCarbonFootprint(record.getTotalCarbonFootprint())
            .conventionalFootprint(record.getConventionalFootprint())
            .carbonSavings(record.getCarbonSavings())
            .savingsPercentage(record.getSavingsPercentage())
            .ecoRating(record.getEcoRating())
            .productWeight(record.getProductWeight())
            .materialType(record.getMaterialType())
            .manufacturingType(record.getManufacturingType())
            .transportationType(record.getTransportationType())
            .transportationDistance(record.getTransportationDistance())
            .packagingType(record.getPackagingType())
            .isRecycled(record.getIsRecycled())
            .isOrganic(record.getIsOrganic())
            .productLifespan(record.getProductLifespan())
            .calculatedAt(record.getCalculatedAt())
            .calculationMethod("ISO 14040/14044 LCA + IPCC Guidelines")
            .dataSource("Ecoinvent v3.8, EPA, IPCC 2021")
            .build();
        
        // Build equivalent impacts
        response.buildEquivalentImpacts(
            record.getTreesEquivalent(),
            record.getCarKmEquivalent(),
            record.getElectricityKwhEquivalent(),
            record.getPlasticBottlesEquivalent()
        );
        
        // Generate rating description
        response.generateRatingDescription();
        
        // Add sustainability tips
        response.setSustainabilityTips(generateSustainabilityTips(record));
        response.setImprovementSuggestions(generateImprovementSuggestions(record));
        
        return response;
    }
    
    /**
     * Generate sustainability tips based on the product
     */
    private String generateSustainabilityTips(CarbonFootprintRecord record) {
        List<String> tips = new ArrayList<>();
        
        if (record.getIsRecycled()) {
            tips.add("Great choice! Recycled materials save significant carbon emissions.");
        }
        
        if (record.getTransportationEmissions() > 5.0) {
            tips.add("Consider buying local products to reduce transportation emissions.");
        }
        
        if ("biodegradable_packaging".equalsIgnoreCase(record.getPackagingType())) {
            tips.add("Excellent! Biodegradable packaging reduces environmental impact.");
        }
        
        if (record.getCarbonSavings() > 0) {
            tips.add(String.format("You saved %.2f kg CO2e compared to conventional alternatives!", 
                                  record.getCarbonSavings()));
        }
        
        return tips.isEmpty() ? "Keep making eco-friendly choices!" : String.join(" ", tips);
    }
    
    /**
     * Generate improvement suggestions
     */
    private String generateImprovementSuggestions(CarbonFootprintRecord record) {
        List<String> suggestions = new ArrayList<>();
        
        if (!record.getIsRecycled()) {
            suggestions.add("Look for products made from recycled materials.");
        }
        
        if (record.getTransportationEmissions() > 3.0) {
            suggestions.add("Choose products with lower transportation distances.");
        }
        
        if (!"biodegradable_packaging".equalsIgnoreCase(record.getPackagingType())) {
            suggestions.add("Prefer products with biodegradable or minimal packaging.");
        }
        
        if (record.getSavingsPercentage() < 30) {
            suggestions.add("Try products with higher eco-ratings (A+ or A) for maximum impact.");
        }
        
        return suggestions.isEmpty() ? "You're doing great!" : String.join(" ", suggestions);
    }
    
    /**
     * Get user's carbon footprint history
     */
    public List<CarbonFootprintResponse> getUserCarbonHistory(String userId) {
        List<CarbonFootprintRecord> records = carbonRecordRepo.findByUserIdOrderByCalculatedAtDesc(userId);
        List<CarbonFootprintResponse> responses = new ArrayList<>();
        
        for (CarbonFootprintRecord record : records) {
            responses.add(buildResponse(record));
        }
        
        return responses;
    }
    
    /**
     * Get user's carbon statistics
     */
    public Map<String, Object> getUserCarbonStatistics(String userId) {
        Map<String, Object> stats = new HashMap<>();
        
        Double totalFootprint = carbonRecordRepo.getTotalCarbonFootprintByUser(userId);
        Double totalSavings = carbonRecordRepo.getTotalCarbonSavingsByUser(userId);
        Long totalRecords = carbonRecordRepo.countByUserId(userId);
        
        stats.put("totalCarbonFootprint", totalFootprint);
        stats.put("totalCarbonSavings", totalSavings);
        stats.put("totalPurchases", totalRecords);
        stats.put("averageFootprintPerPurchase", totalRecords > 0 ? totalFootprint / totalRecords : 0.0);
        
        // Get environmental equivalents
        Object[] equivalents = carbonRecordRepo.getTotalEnvironmentalEquivalents(userId);
        if (equivalents != null && equivalents.length == 4) {
            stats.put("treesEquivalent", equivalents[0]);
            stats.put("carKmEquivalent", equivalents[1]);
            stats.put("electricityKwhEquivalent", equivalents[2]);
            stats.put("plasticBottlesEquivalent", equivalents[3]);
        }
        
        // Get eco rating distribution
        List<Object[]> ratingDist = carbonRecordRepo.getEcoRatingDistribution(userId);
        Map<String, Long> ratings = new HashMap<>();
        for (Object[] row : ratingDist) {
            ratings.put((String) row[0], (Long) row[1]);
        }
        stats.put("ecoRatingDistribution", ratings);
        
        // Get category breakdown
        List<Object[]> categoryData = carbonRecordRepo.getCarbonFootprintByCategory(userId);
        Map<String, Double> categories = new HashMap<>();
        for (Object[] row : categoryData) {
            categories.put((String) row[0], (Double) row[1]);
        }
        stats.put("carbonByCategory", categories);
        
        return stats;
    }
    
    /**
     * Initialize sample emission factors (to be called on app startup or via endpoint)
     */
    @Transactional
    public void initializeSampleEmissionFactors() {
        log.info("Initializing sample emission factors...");
        
        // Check if already initialized
        if (emissionFactorRepo.count() > 0) {
            log.info("Emission factors already exist, skipping initialization");
            return;
        }
        
        List<EmissionFactor> factors = createSampleEmissionFactors();
        emissionFactorRepo.saveAll(factors);
        
        log.info("Initialized {} emission factors", factors.size());
    }
    
    /**
     * Create sample emission factors based on scientific research
     */
    private List<EmissionFactor> createSampleEmissionFactors() {
        List<EmissionFactor> factors = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        // Material factors - Textiles
        factors.add(createFactor("MATERIAL", "textiles", "organic_cotton", "Organic Cotton",
            "kg CO2e per kg", 3.8, "IPCC 2021", "HIGH", now, 0.0, 0.5, 95.0));
        factors.add(createFactor("MATERIAL", "textiles", "conventional_cotton", "Conventional Cotton",
            "kg CO2e per kg", 8.0, "Ecoinvent v3.8", "HIGH", now, 0.0, 0.3, 90.0));
        factors.add(createFactor("MATERIAL", "textiles", "recycled_polyester", "Recycled Polyester",
            "kg CO2e per kg", 4.2, "EPA Guidelines", "HIGH", now, 0.0, 3.0, 80.0));
        factors.add(createFactor("MATERIAL", "textiles", "virgin_polyester", "Virgin Polyester",
            "kg CO2e per kg", 10.5, "Ecoinvent v3.8", "HIGH", now, 0.0, 2.5, 70.0));
        
        // Material factors - Plastics
        factors.add(createFactor("MATERIAL", "plastics", "virgin_plastic", "Virgin Plastic",
            "kg CO2e per kg", 6.15, "EPA Guidelines", "HIGH", now, 0.0, 1.5, 50.0));
        factors.add(createFactor("MATERIAL", "plastics", "recycled_plastic", "Recycled Plastic",
            "kg CO2e per kg", 2.1, "EPA Guidelines", "HIGH", now, 0.0, 4.0, 85.0));
        factors.add(createFactor("MATERIAL", "plastics", "pla_plastic", "PLA Bioplastic",
            "kg CO2e per kg", 3.2, "IPCC 2021", "MEDIUM", now, 0.0, 0.5, 100.0));
        
        // Material factors - Metals
        factors.add(createFactor("MATERIAL", "metals", "aluminum", "Aluminum",
            "kg CO2e per kg", 11.5, "Ecoinvent v3.8", "HIGH", now, 0.0, 0.5, 20.0));
        factors.add(createFactor("MATERIAL", "metals", "recycled_aluminum", "Recycled Aluminum",
            "kg CO2e per kg", 0.5, "EPA Guidelines", "HIGH", now, 0.0, 10.0, 95.0));
        factors.add(createFactor("MATERIAL", "metals", "steel", "Steel",
            "kg CO2e per kg", 2.9, "Ecoinvent v3.8", "HIGH", now, 0.0, 0.4, 30.0));
        
        // Material factors - Natural Materials
        factors.add(createFactor("MATERIAL", "natural", "bamboo", "Bamboo",
            "kg CO2e per kg", 1.2, "IPCC 2021", "MEDIUM", now, -0.5, 0.3, 100.0));
        factors.add(createFactor("MATERIAL", "natural", "hemp", "Hemp",
            "kg CO2e per kg", 1.8, "IPCC 2021", "MEDIUM", now, -0.3, 0.4, 100.0));
        factors.add(createFactor("MATERIAL", "natural", "cork", "Cork",
            "kg CO2e per kg", 0.8, "Ecoinvent v3.8", "MEDIUM", now, -0.9, 0.2, 100.0));
        
        // Transportation factors
        factors.add(createFactor("TRANSPORTATION", "freight", "truck_local", "Local Truck",
            "kg CO2e per km per kg", 0.000089, "EPA Guidelines", "HIGH", now, 0.0, 0.0, 0.0));
        factors.add(createFactor("TRANSPORTATION", "freight", "truck_long_distance", "Long Distance Truck",
            "kg CO2e per km per kg", 0.000062, "EPA Guidelines", "HIGH", now, 0.0, 0.0, 0.0));
        factors.add(createFactor("TRANSPORTATION", "freight", "ship_freight", "Ship Freight",
            "kg CO2e per km per kg", 0.000011, "IMO Guidelines", "HIGH", now, 0.0, 0.0, 0.0));
        factors.add(createFactor("TRANSPORTATION", "freight", "air_freight", "Air Freight",
            "kg CO2e per km per kg", 0.000602, "ICAO Guidelines", "HIGH", now, 0.0, 0.0, 0.0));
        factors.add(createFactor("TRANSPORTATION", "freight", "electric_vehicle", "Electric Vehicle",
            "kg CO2e per km per kg", 0.000025, "EPA Guidelines", "HIGH", now, 0.0, 0.0, 0.0));
        factors.add(createFactor("TRANSPORTATION", "freight", "rail_freight", "Rail Freight",
            "kg CO2e per km per kg", 0.000022, "EPA Guidelines", "HIGH", now, 0.0, 0.0, 0.0));
        
        // Manufacturing factors
        factors.add(createFactor("MANUFACTURING", "clothing", "eco_friendly", "Eco-Friendly Textile Manufacturing",
            "kg CO2e per unit", 1.2, "ISO 14040", "MEDIUM", now, 0.0, 0.0, 0.0));
        factors.add(createFactor("MANUFACTURING", "clothing", "conventional", "Conventional Textile Manufacturing",
            "kg CO2e per unit", 2.5, "ISO 14040", "MEDIUM", now, 0.0, 0.0, 0.0));
        factors.add(createFactor("MANUFACTURING", "electronics", "eco_friendly", "Eco-Friendly Electronics Manufacturing",
            "kg CO2e per unit", 4.2, "EPA Guidelines", "MEDIUM", now, 0.0, 0.0, 0.0));
        factors.add(createFactor("MANUFACTURING", "electronics", "conventional", "Conventional Electronics Manufacturing",
            "kg CO2e per unit", 8.5, "EPA Guidelines", "MEDIUM", now, 0.0, 0.0, 0.0));
        
        // Packaging factors
        factors.add(createFactor("PACKAGING", "general", "biodegradable_packaging", "Biodegradable Packaging",
            "kg CO2e per kg", 0.3, "EPA Guidelines", "MEDIUM", now, 0.0, 0.0, 100.0));
        factors.add(createFactor("PACKAGING", "general", "recycled_cardboard", "Recycled Cardboard",
            "kg CO2e per kg", 0.5, "EPA Guidelines", "HIGH", now, 0.0, 0.8, 90.0));
        factors.add(createFactor("PACKAGING", "general", "virgin_plastic", "Virgin Plastic Packaging",
            "kg CO2e per kg", 6.0, "Ecoinvent v3.8", "HIGH", now, 0.0, 1.5, 50.0));
        factors.add(createFactor("PACKAGING", "general", "recycled_plastic", "Recycled Plastic Packaging",
            "kg CO2e per kg", 2.0, "EPA Guidelines", "HIGH", now, 0.0, 3.0, 80.0));
        factors.add(createFactor("PACKAGING", "general", "paper", "Paper Packaging",
            "kg CO2e per kg", 1.2, "Ecoinvent v3.8", "HIGH", now, 0.0, 0.5, 95.0));
        factors.add(createFactor("PACKAGING", "general", "no_packaging", "No Packaging",
            "kg CO2e per kg", 0.0, "Direct Calculation", "HIGH", now, 0.0, 0.0, 100.0));
        
        // End-of-life factors
        factors.add(createFactor("END_OF_LIFE", "disposal", "organic_cotton", "Organic Cotton Disposal",
            "kg CO2e per kg", 0.1, "EPA Guidelines", "MEDIUM", now, 0.0, 0.0, 95.0));
        factors.add(createFactor("END_OF_LIFE", "disposal", "virgin_plastic", "Plastic Disposal",
            "kg CO2e per kg", 0.8, "EPA Guidelines", "MEDIUM", now, 0.0, 0.0, 10.0));
        factors.add(createFactor("END_OF_LIFE", "disposal", "recycled_plastic", "Recycled Plastic Disposal",
            "kg CO2e per kg", 0.2, "EPA Guidelines", "MEDIUM", now, 0.0, 0.0, 85.0));
        factors.add(createFactor("END_OF_LIFE", "disposal", "bamboo", "Bamboo Disposal",
            "kg CO2e per kg", 0.05, "IPCC 2021", "MEDIUM", now, 0.0, 0.0, 100.0));
        
        return factors;
    }
    
    private EmissionFactor createFactor(String category, String subcategory, String materialType,
                                       String name, String unit, Double emissionFactor,
                                       String dataSource, String confidenceLevel, LocalDateTime now,
                                       Double carbonSeq, Double recyclingBenefit, Double biodegradationRate) {
        EmissionFactor factor = new EmissionFactor();
        factor.setCategory(category);
        factor.setSubcategory(subcategory);
        factor.setMaterialType(materialType);
        factor.setName(name);
        factor.setUnit(unit);
        factor.setEmissionFactor(emissionFactor);
        factor.setDataSource(dataSource);
        factor.setConfidenceLevel(confidenceLevel);
        factor.setIsActive(true);
        factor.setCarbonSequestration(carbonSeq);
        factor.setRecyclingBenefit(recyclingBenefit);
        factor.setBiodegradationRate(biodegradationRate);
        factor.setCreatedAt(now);
        factor.setUpdatedAt(now);
        factor.setDescription("Scientific emission factor for " + name);
        return factor;
    }

    /**
     * Compare carbon footprint of multiple products
     */
    public Map<String, Object> compareProducts(List<String> productIds) {
        log.info("Comparing {} products", productIds.size());
        
        List<Map<String, Object>> products = new ArrayList<>();
        double lowestFootprint = Double.MAX_VALUE;
        String bestProduct = "";
        
        for (String productId : productIds) {
            List<CarbonFootprintRecord> records = carbonRecordRepo.findByProductId(productId);
            
            if (!records.isEmpty()) {
                CarbonFootprintRecord latestRecord = records.get(0);
                
                Map<String, Object> productData = new HashMap<>();
                productData.put("productId", latestRecord.getProductId());
                productData.put("productName", latestRecord.getProductName());
                productData.put("totalFootprint", latestRecord.getTotalCarbonFootprint());
                productData.put("ecoRating", latestRecord.getEcoRating());
                productData.put("carbonSavings", latestRecord.getCarbonSavings());
                // Note: Breakdown fields not in entity, calculate from total
                productData.put("totalFootprint", latestRecord.getTotalCarbonFootprint());
                
                products.add(productData);
                
                if (latestRecord.getTotalCarbonFootprint() < lowestFootprint) {
                    lowestFootprint = latestRecord.getTotalCarbonFootprint();
                    bestProduct = latestRecord.getProductName();
                }
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("products", products);
        result.put("bestProduct", bestProduct);
        result.put("lowestFootprint", lowestFootprint);
        result.put("comparedAt", LocalDateTime.now());
        
        return result;
    }

    /**
     * Get category benchmark statistics
     */
    public Map<String, Object> getCategoryBenchmark(String category) {
        log.info("Getting benchmark for category: {}", category);
        
        List<CarbonFootprintRecord> records = carbonRecordRepo.findByCategory(category);
        
        if (records.isEmpty()) {
            return Map.of(
                "category", category,
                "averageFootprint", 0.0,
                "lowestFootprint", 0.0,
                "highestFootprint", 0.0,
                "totalProducts", 0,
                "message", "No data available for this category"
            );
        }
        
        DoubleSummaryStatistics stats = records.stream()
            .mapToDouble(CarbonFootprintRecord::getTotalCarbonFootprint)
            .summaryStatistics();
        
        Map<String, Integer> ecoRatingDistribution = new HashMap<>();
        records.forEach(r -> {
            String rating = r.getEcoRating();
            ecoRatingDistribution.put(rating, ecoRatingDistribution.getOrDefault(rating, 0) + 1);
        });
        
        return Map.of(
            "category", category,
            "averageFootprint", stats.getAverage(),
            "lowestFootprint", stats.getMin(),
            "highestFootprint", stats.getMax(),
            "totalProducts", records.size(),
            "ecoRatingDistribution", ecoRatingDistribution
        );
    }

    /**
     * Get leaderboard of users with lowest carbon footprint
     */
    public List<Map<String, Object>> getCarbonLeaderboard(int limit) {
        log.info("Getting carbon footprint leaderboard, limit: {}", limit);
        
        // Get all users and their total carbon footprint
        List<CarbonFootprintRecord> allRecords = carbonRecordRepo.findAll();
        
        Map<String, Double> userTotals = new HashMap<>();
        Map<String, Integer> userCounts = new HashMap<>();
        
        for (CarbonFootprintRecord record : allRecords) {
            String userId = record.getUserId();
            userTotals.put(userId, 
                userTotals.getOrDefault(userId, 0.0) + record.getTotalCarbonFootprint());
            userCounts.put(userId, 
                userCounts.getOrDefault(userId, 0) + 1);
        }
        
        // Create leaderboard entries
        List<Map<String, Object>> leaderboard = new ArrayList<>();
        
        userTotals.entrySet().stream()
            .sorted(Map.Entry.comparingByValue())
            .limit(limit)
            .forEach(entry -> {
                String userId = entry.getKey();
                double totalFootprint = entry.getValue();
                int calculationCount = userCounts.get(userId);
                
                leaderboard.add(Map.of(
                    "userId", userId,
                    "totalCarbonFootprint", totalFootprint,
                    "calculationCount", calculationCount,
                    "averagePerProduct", totalFootprint / calculationCount,
                    "rank", leaderboard.size() + 1
                ));
            });
        
        return leaderboard;
    }

    /**
     * Get all categories with statistics
     */
    public List<Map<String, Object>> getAllCategoriesWithStats() {
        log.info("Getting all categories with statistics");
        
        List<CarbonFootprintRecord> allRecords = carbonRecordRepo.findAll();
        
        Map<String, List<CarbonFootprintRecord>> categoryRecords = new HashMap<>();
        
        for (CarbonFootprintRecord record : allRecords) {
            String category = record.getCategory();
            categoryRecords.computeIfAbsent(category, k -> new ArrayList<>()).add(record);
        }
        
        List<Map<String, Object>> categories = new ArrayList<>();
        
        for (Map.Entry<String, List<CarbonFootprintRecord>> entry : categoryRecords.entrySet()) {
            String category = entry.getKey();
            List<CarbonFootprintRecord> records = entry.getValue();
            
            DoubleSummaryStatistics stats = records.stream()
                .mapToDouble(CarbonFootprintRecord::getTotalCarbonFootprint)
                .summaryStatistics();
            
            categories.add(Map.of(
                "category", category,
                "productCount", records.size(),
                "averageFootprint", stats.getAverage(),
                "lowestFootprint", stats.getMin(),
                "highestFootprint", stats.getMax(),
                "totalFootprint", stats.getSum()
            ));
        }
        
        // Sort by product count descending
        categories.sort((a, b) -> 
            ((Integer) b.get("productCount")).compareTo((Integer) a.get("productCount"))
        );
        
        return categories;
    }

    /**
     * Get sustainability tips by category
     */
    public List<String> getSustainabilityTipsByCategory(String category) {
        log.info("Getting sustainability tips for category: {}", category);
        
        List<String> tips = new ArrayList<>();
        
        switch (category.toLowerCase()) {
            case "electronics":
                tips.add("Choose energy-efficient electronics with Energy Star certification");
                tips.add("Buy refurbished or recycled electronics when possible");
                tips.add("Properly recycle old electronics to recover valuable materials");
                tips.add("Use devices in power-saving mode to reduce energy consumption");
                break;
                
            case "clothing":
            case "fashion":
                tips.add("Choose organic or recycled fabrics like organic cotton or recycled polyester");
                tips.add("Buy from local brands to reduce transportation emissions");
                tips.add("Consider second-hand or vintage clothing");
                tips.add("Care for clothes properly to extend their lifespan");
                break;
                
            case "food":
            case "groceries":
                tips.add("Choose locally sourced and seasonal produce");
                tips.add("Reduce meat consumption - plant-based alternatives have lower carbon footprint");
                tips.add("Buy organic products to support sustainable farming");
                tips.add("Minimize food waste through proper storage and meal planning");
                break;
                
            case "furniture":
            case "home":
                tips.add("Choose furniture made from sustainable or recycled materials");
                tips.add("Buy durable, high-quality items that last longer");
                tips.add("Consider second-hand furniture or upcycling");
                tips.add("Look for certifications like FSC for wood products");
                break;
                
            case "beauty":
            case "cosmetics":
                tips.add("Choose products with minimal and recyclable packaging");
                tips.add("Look for organic and cruelty-free certifications");
                tips.add("Buy refillable products to reduce packaging waste");
                tips.add("Support brands with sustainable sourcing practices");
                break;
                
            default:
                tips.add("Choose products with minimal packaging");
                tips.add("Buy locally made products to reduce transportation emissions");
                tips.add("Look for recycled or sustainable materials");
                tips.add("Consider the product's full lifecycle before purchasing");
                tips.add("Properly dispose of products through recycling or composting");
        }
        
        return tips;
    }
}
