package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.entity.EcoDiscount;
import com.ecobazaar.backend.entity.UserDiscountUsage;
import com.ecobazaar.backend.service.EcoDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST Controller for Eco Discount Management
 */
@RestController
@RequestMapping("/api/eco-discounts")
@CrossOrigin(origins = "*")
public class EcoDiscountController {

    @Autowired
    private EcoDiscountService ecoDiscountService;

    // Discount Management Endpoints

    @PostMapping
    public ResponseEntity<Map<String, Object>> createDiscount(@RequestBody EcoDiscount discount) {
        Map<String, Object> response = new HashMap<>();
        try {
            EcoDiscount createdDiscount = ecoDiscountService.createDiscount(discount);
            response.put("success", true);
            response.put("message", "Discount created successfully");
            response.put("discount", createdDiscount);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create discount: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllActiveDiscounts() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EcoDiscount> discounts = ecoDiscountService.getAllActiveDiscounts();
            response.put("success", true);
            response.put("discounts", discounts);
            response.put("count", discounts.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch discounts: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/available")
    public ResponseEntity<Map<String, Object>> getAvailableDiscounts() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EcoDiscount> discounts = ecoDiscountService.getAvailableDiscounts();
            response.put("success", true);
            response.put("discounts", discounts);
            response.put("count", discounts.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch available discounts: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/valid")
    public ResponseEntity<Map<String, Object>> getCurrentlyValidDiscounts() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EcoDiscount> discounts = ecoDiscountService.getCurrentlyValidDiscounts();
            response.put("success", true);
            response.put("discounts", discounts);
            response.put("count", discounts.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch valid discounts: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/{discountCode}")
    public ResponseEntity<Map<String, Object>> getDiscountByCode(@PathVariable String discountCode) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<EcoDiscount> discountOpt = ecoDiscountService.getDiscountByCode(discountCode);
            if (discountOpt.isPresent()) {
                response.put("success", true);
                response.put("discount", discountOpt.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Discount not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch discount: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/type/{discountType}")
    public ResponseEntity<Map<String, Object>> getDiscountsByType(@PathVariable String discountType) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EcoDiscount> discounts = ecoDiscountService.getDiscountsByType(discountType);
            response.put("success", true);
            response.put("discounts", discounts);
            response.put("count", discounts.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch discounts by type: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<Map<String, Object>> getDiscountsByCategory(@PathVariable String category) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EcoDiscount> discounts = ecoDiscountService.getDiscountsByCategory(category);
            response.put("success", true);
            response.put("discounts", discounts);
            response.put("count", discounts.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch discounts by category: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<Map<String, Object>> getDiscountsByStore(@PathVariable String storeId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EcoDiscount> discounts = ecoDiscountService.getDiscountsByStore(storeId);
            response.put("success", true);
            response.put("discounts", discounts);
            response.put("count", discounts.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch discounts by store: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchDiscounts(@RequestParam String keyword) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<EcoDiscount> discounts = ecoDiscountService.searchDiscounts(keyword);
            response.put("success", true);
            response.put("discounts", discounts);
            response.put("count", discounts.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to search discounts: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/{discountCode}")
    public ResponseEntity<Map<String, Object>> updateDiscount(@PathVariable String discountCode, 
                                                             @RequestBody EcoDiscount discount) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<EcoDiscount> existingDiscountOpt = ecoDiscountService.getDiscountByCode(discountCode);
            if (existingDiscountOpt.isPresent()) {
                EcoDiscount existingDiscount = existingDiscountOpt.get();
                discount.setId(existingDiscount.getId());
                discount.setDiscountCode(discountCode);
                
                EcoDiscount updatedDiscount = ecoDiscountService.updateDiscount(discount);
                response.put("success", true);
                response.put("message", "Discount updated successfully");
                response.put("discount", updatedDiscount);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Discount not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update discount: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/{discountCode}")
    public ResponseEntity<Map<String, Object>> deactivateDiscount(@PathVariable String discountCode) {
        Map<String, Object> response = new HashMap<>();
        try {
            ecoDiscountService.deactivateDiscount(discountCode);
            response.put("success", true);
            response.put("message", "Discount deactivated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to deactivate discount: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Discount Validation and Application Endpoints

    @PostMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateDiscount(@RequestBody Map<String, Object> request) {
        try {
            String discountCode = (String) request.get("discountCode");
            String userId = (String) request.get("userId");
            Double orderAmount = Double.valueOf(request.get("orderAmount").toString());
            String category = (String) request.get("category");
            String storeId = (String) request.get("storeId");
            Integer userEcoPoints = request.get("userEcoPoints") != null ? 
                Integer.valueOf(request.get("userEcoPoints").toString()) : null;

            Map<String, Object> result = ecoDiscountService.validateDiscount(
                discountCode, userId, orderAmount, category, storeId, userEcoPoints
            );
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("message", "Failed to validate discount: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/apply")
    public ResponseEntity<Map<String, Object>> applyDiscount(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String userId = (String) request.get("userId");
            String discountCode = (String) request.get("discountCode");
            String orderId = (String) request.get("orderId");
            Double orderAmount = Double.valueOf(request.get("orderAmount").toString());
            Double discountAmount = Double.valueOf(request.get("discountAmount").toString());

            UserDiscountUsage usage = ecoDiscountService.applyDiscount(
                userId, discountCode, orderId, orderAmount, discountAmount
            );
            
            response.put("success", true);
            response.put("message", "Discount applied successfully");
            response.put("usage", usage);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to apply discount: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/calculate")
    public ResponseEntity<Map<String, Object>> calculateDiscount(@RequestParam String discountCode,
                                                               @RequestParam Double orderAmount) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<EcoDiscount> discountOpt = ecoDiscountService.getDiscountByCode(discountCode);
            if (discountOpt.isPresent()) {
                Double discountAmount = ecoDiscountService.calculateDiscountAmount(discountOpt.get(), orderAmount);
                response.put("success", true);
                response.put("discountAmount", discountAmount);
                response.put("finalAmount", orderAmount - discountAmount);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Discount not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to calculate discount: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // User-specific Endpoints

    @GetMapping("/user/{userId}/applicable")
    public ResponseEntity<Map<String, Object>> getApplicableDiscountsForUser(@PathVariable String userId,
                                                                           @RequestParam(required = false) Integer userEcoPoints,
                                                                           @RequestParam(required = false) Double orderAmount) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (userEcoPoints == null) userEcoPoints = 0;
            if (orderAmount == null) orderAmount = 0.0;
            
            List<EcoDiscount> discounts = ecoDiscountService.getApplicableDiscountsForUser(userId, userEcoPoints, orderAmount);
            response.put("success", true);
            response.put("discounts", discounts);
            response.put("count", discounts.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch applicable discounts: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserDiscountStats(@PathVariable String userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> stats = ecoDiscountService.getUserDiscountStats(userId);
            response.put("success", true);
            response.put("stats", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch user discount stats: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Analytics Endpoints

    @GetMapping("/{discountCode}/analytics")
    public ResponseEntity<Map<String, Object>> getDiscountAnalytics(@PathVariable String discountCode) {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> analytics = ecoDiscountService.getDiscountAnalytics(discountCode);
            response.put("success", true);
            response.put("analytics", analytics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch discount analytics: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/analytics/popular")
    public ResponseEntity<Map<String, Object>> getMostPopularDiscounts() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Map<String, Object>> popularDiscounts = ecoDiscountService.getMostPopularDiscounts();
            response.put("success", true);
            response.put("popularDiscounts", popularDiscounts);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to fetch popular discounts: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Administrative Endpoints

    @PostMapping("/initialize-sample-data")
    public ResponseEntity<Map<String, Object>> initializeSampleData() {
        Map<String, Object> response = new HashMap<>();
        try {
            ecoDiscountService.initializeSampleDiscounts();
            response.put("success", true);
            response.put("message", "Sample discounts initialized successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to initialize sample data: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}