package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins = "*")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    // Add item to wishlist
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToWishlist(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            String productId = (String) request.get("productId");
            String productName = (String) request.get("productName");
            Double price = ((Number) request.get("price")).doubleValue();
            String imageUrl = (String) request.get("imageUrl");
            String category = (String) request.get("category");

            if (userId == null || productId == null || productName == null || price == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Missing required fields: userId, productId, productName, price"
                ));
            }

            Map<String, Object> result = wishlistService.addToWishlist(
                userId, productId, productName, price, imageUrl, category
            );
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error adding to wishlist: " + e.getMessage()
            ));
        }
    }

    // Remove item from wishlist
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeFromWishlist(@RequestParam String userId, @RequestParam String productId) {
        try {
            Map<String, Object> result = wishlistService.removeFromWishlist(userId, productId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error removing from wishlist: " + e.getMessage()
            ));
        }
    }

    // Get user wishlist
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserWishlist(@PathVariable String userId) {
        try {
            List<Map<String, Object>> wishlist = wishlistService.getUserWishlist(userId);
            return ResponseEntity.ok(wishlist);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Check if item is in wishlist
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> isInWishlist(@RequestParam String userId, @RequestParam String productId) {
        try {
            boolean isInWishlist = wishlistService.isInWishlist(userId, productId);
            return ResponseEntity.ok(Map.of(
                "isInWishlist", isInWishlist
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "isInWishlist", false,
                "error", e.getMessage()
            ));
        }
    }

    // Get wishlist count
    @GetMapping("/count/{userId}")
    public ResponseEntity<Map<String, Object>> getWishlistCount(@PathVariable String userId) {
        try {
            int count = wishlistService.getWishlistCount(userId);
            return ResponseEntity.ok(Map.of(
                "count", count
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "count", 0,
                "error", e.getMessage()
            ));
        }
    }

    // Clear wishlist
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Map<String, Object>> clearWishlist(@PathVariable String userId) {
        try {
            Map<String, Object> result = wishlistService.clearWishlist(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error clearing wishlist: " + e.getMessage()
            ));
        }
    }

    // Get wishlist statistics
    @GetMapping("/stats/{userId}")
    public ResponseEntity<Map<String, Object>> getWishlistStatistics(@PathVariable String userId) {
        try {
            Map<String, Object> stats = wishlistService.getWishlistStatistics(userId);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "totalItems", 0,
                "totalValue", 0.0,
                "categories", List.of(),
                "error", e.getMessage()
            ));
        }
    }

    // Move item to cart
    @PostMapping("/move-to-cart")
    public ResponseEntity<Map<String, Object>> moveToCart(@RequestParam String userId, @RequestParam String productId) {
        try {
            Map<String, Object> result = wishlistService.moveToCart(userId, productId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error moving to cart: " + e.getMessage()
            ));
        }
    }

    // Get wishlist by category
    @GetMapping("/category/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getWishlistByCategory(
            @PathVariable String userId, 
            @RequestParam String category) {
        try {
            List<Map<String, Object>> items = wishlistService.getWishlistByCategory(userId, category);
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Get wishlist recommendations
    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getWishlistRecommendations(@PathVariable String userId) {
        try {
            List<Map<String, Object>> recommendations = wishlistService.getWishlistRecommendations(userId);
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Get wishlist analytics
    @GetMapping("/analytics/{userId}")
    public ResponseEntity<Map<String, Object>> getWishlistAnalytics(@PathVariable String userId) {
        try {
            Map<String, Object> analytics = wishlistService.getWishlistAnalytics(userId);
            return ResponseEntity.ok(analytics);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "totalItems", 0,
                "totalValue", 0.0,
                "averagePrice", 0.0,
                "mostAddedCategory", "",
                "itemsAddedThisMonth", 0,
                "error", e.getMessage()
            ));
        }
    }

    // Admin endpoints
    @GetMapping("/admin/all")
    public ResponseEntity<List<Map<String, Object>>> getAllWishlistsForAdmin() {
        try {
            List<Map<String, Object>> wishlists = wishlistService.getAllWishlistsForAdmin();
            return ResponseEntity.ok(wishlists);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    @GetMapping("/admin/product/{productId}/users")
    public ResponseEntity<List<Map<String, Object>>> getUsersWithProduct(@PathVariable String productId) {
        try {
            List<Map<String, Object>> users = wishlistService.getUsersWithProductInWishlist(productId);
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    @GetMapping("/admin/popular-products")
    public ResponseEntity<List<Map<String, Object>>> getPopularProducts() {
        try {
            List<Map<String, Object>> products = wishlistService.getPopularProductsAnalytics();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }
}
