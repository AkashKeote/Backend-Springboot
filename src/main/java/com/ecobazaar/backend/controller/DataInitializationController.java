package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.service.DataInitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/init")
@CrossOrigin(origins = "*")
public class DataInitializationController {

    @Autowired
    private DataInitializationService dataInitializationService;

    /**
     * Initialize database with sample data
     */
    @PostMapping("/sample-data")
    public ResponseEntity<Map<String, Object>> initializeSampleData() {
        try {
            String result = dataInitializationService.initializeSampleData();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Sample data initialized successfully",
                "details", result
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error initializing sample data: " + e.getMessage()
            ));
        }
    }

    /**
     * Check if database needs initialization
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getInitializationStatus() {
        try {
            boolean needsInit = dataInitializationService.needsInitialization();
            String stats = dataInitializationService.getInitializationStats();
            
            return ResponseEntity.ok(Map.of(
                "needsInitialization", needsInit,
                "stats", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "needsInitialization", true,
                "error", "Error checking status: " + e.getMessage()
            ));
        }
    }

    /**
     * Get initialization statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        try {
            String stats = dataInitializationService.getInitializationStats();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "stats", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Error getting stats: " + e.getMessage()
            ));
        }
    }


}


