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
}
