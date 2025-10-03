package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.dto.CarbonFootprintRequest;
import com.ecobazaar.backend.dto.CarbonFootprintResponse;
import com.ecobazaar.backend.service.CarbonFootprintService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for Carbon Footprint Calculation
 * Provides real scientific carbon footprint calculations
 */
@RestController
@RequestMapping("/api/carbon-footprint")
@CrossOrigin(origins = "*")
@Slf4j
public class CarbonFootprintController {

    @Autowired
    private CarbonFootprintService carbonFootprintService;

    /**
     * Calculate carbon footprint for a product
     * POST /api/carbon-footprint/calculate
     */
    @PostMapping("/calculate")
    public ResponseEntity<CarbonFootprintResponse> calculateFootprint(
            @RequestBody CarbonFootprintRequest request) {
        log.info("Calculate carbon footprint request for product: {}", request.getProductName());
        
        CarbonFootprintResponse response = carbonFootprintService.calculateProductCarbonFootprint(request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get user's carbon footprint history
     * GET /api/carbon-footprint/user/{userId}/history
     */
    @GetMapping("/user/{userId}/history")
    public ResponseEntity<List<CarbonFootprintResponse>> getUserHistory(
            @PathVariable String userId) {
        log.info("Get carbon history for user: {}", userId);
        
        List<CarbonFootprintResponse> history = carbonFootprintService.getUserCarbonHistory(userId);
        
        return ResponseEntity.ok(history);
    }

    /**
     * Get user's carbon statistics
     * GET /api/carbon-footprint/user/{userId}/statistics
     */
    @GetMapping("/user/{userId}/statistics")
    public ResponseEntity<Map<String, Object>> getUserStatistics(
            @PathVariable String userId) {
        log.info("Get carbon statistics for user: {}", userId);
        
        Map<String, Object> stats = carbonFootprintService.getUserCarbonStatistics(userId);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Initialize sample emission factors (Admin only - should be secured)
     * POST /api/carbon-footprint/initialize-emission-factors
     */
    @PostMapping("/initialize-emission-factors")
    public ResponseEntity<Map<String, String>> initializeEmissionFactors() {
        log.info("Initialize emission factors request");
        
        carbonFootprintService.initializeSampleEmissionFactors();
        
        return ResponseEntity.ok(Map.of(
            "message", "Emission factors initialized successfully",
            "status", "success"
        ));
    }

    /**
     * Health check endpoint
     * GET /api/carbon-footprint/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "service", "Carbon Footprint Calculation Service",
            "version", "1.0.0",
            "methodology", "ISO 14040/14044 LCA + IPCC Guidelines"
        ));
    }

    /**
     * Compare carbon footprint of multiple products
     * POST /api/carbon-footprint/compare
     */
    @PostMapping("/compare")
    public ResponseEntity<Map<String, Object>> compareProducts(@RequestBody Map<String, Object> request) {
        log.info("Compare products request");
        
        try {
            @SuppressWarnings("unchecked")
            List<String> productIds = (List<String>) request.get("productIds");
            
            if (productIds == null || productIds.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Product IDs are required"
                ));
            }
            
            Map<String, Object> comparison = carbonFootprintService.compareProducts(productIds);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "comparison", comparison
            ));
        } catch (Exception e) {
            log.error("Error comparing products: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error comparing products: " + e.getMessage()
            ));
        }
    }

    /**
     * Get category benchmarks
     * GET /api/carbon-footprint/category/{category}/benchmark
     */
    @GetMapping("/category/{category}/benchmark")
    public ResponseEntity<Map<String, Object>> getCategoryBenchmark(@PathVariable String category) {
        log.info("Get benchmark for category: {}", category);
        
        try {
            Map<String, Object> benchmark = carbonFootprintService.getCategoryBenchmark(category);
            
            return ResponseEntity.ok(benchmark);
        } catch (Exception e) {
            log.error("Error getting category benchmark: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error getting benchmark: " + e.getMessage()
            ));
        }
    }

    /**
     * Get leaderboard of users with lowest carbon footprint
     * GET /api/carbon-footprint/leaderboard
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<List<Map<String, Object>>> getCarbonLeaderboard(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Get carbon footprint leaderboard, limit: {}", limit);
        
        try {
            List<Map<String, Object>> leaderboard = carbonFootprintService.getCarbonLeaderboard(limit);
            
            return ResponseEntity.ok(leaderboard);
        } catch (Exception e) {
            log.error("Error getting leaderboard: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    /**
     * Get all categories with carbon data
     * GET /api/carbon-footprint/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, Object>>> getAllCategories() {
        log.info("Get all categories with carbon data");
        
        try {
            List<Map<String, Object>> categories = carbonFootprintService.getAllCategoriesWithStats();
            
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            log.error("Error getting categories: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    /**
     * Get sustainability tips by category
     * GET /api/carbon-footprint/tips/{category}
     */
    @GetMapping("/tips/{category}")
    public ResponseEntity<Map<String, Object>> getSustainabilityTips(@PathVariable String category) {
        log.info("Get sustainability tips for category: {}", category);
        
        try {
            List<String> tips = carbonFootprintService.getSustainabilityTipsByCategory(category);
            
            return ResponseEntity.ok(Map.of(
                "category", category,
                "tips", tips
            ));
        } catch (Exception e) {
            log.error("Error getting tips: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "category", category,
                "tips", List.of()
            ));
        }
    }
}
