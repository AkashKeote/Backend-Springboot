package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.service.AdminWishlistAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/wishlist")
@CrossOrigin(origins = "*")
public class AdminWishlistController {

    @Autowired
    private AdminWishlistAnalyticsService analyticsService;

    // Get all wishlists with user information
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllWishlists() {
        try {
            List<Map<String, Object>> wishlists = analyticsService.getAllWishlistsWithUsers();
            return ResponseEntity.ok(wishlists);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Get wishlist analytics summary
    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getWishlistAnalytics() {
        try {
            Map<String, Object> analytics = analyticsService.getWishlistAnalytics();
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    // Get popular products in wishlists
    @GetMapping("/popular-products")
    public ResponseEntity<List<Map<String, Object>>> getPopularProducts() {
        try {
            List<Map<String, Object>> products = analyticsService.getPopularWishlistProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Get users who added specific product to wishlist
    @GetMapping("/product/{productId}/users")
    public ResponseEntity<List<Map<String, Object>>> getUsersWithProduct(@PathVariable String productId) {
        try {
            List<Map<String, Object>> users = analyticsService.getUsersWithProductInWishlist(productId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Get wishlist items by date range
    @GetMapping("/items/date-range")
    public ResponseEntity<List<Map<String, Object>>> getWishlistItemsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            List<Map<String, Object>> items = analyticsService.getWishlistItemsByDateRange(startDate, endDate);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }
}
