package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = {
    "http://localhost:8080", 
    "http://127.0.0.1:8080",
    "http://localhost:3000",
    "http://127.0.0.1:3000",
    "http://localhost:5000",
    "http://127.0.0.1:5000",
    "http://localhost:62804",
    "http://127.0.0.1:62804"
})
public class CartController {

    @Autowired
    private CartService cartService;

    // Add item to cart
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            String productId = (String) request.get("productId");
            String productName = (String) request.get("productName");
            Double productPrice = request.get("productPrice") != null ? 
                Double.valueOf(request.get("productPrice").toString()) : 0.0;
            String productImage = (String) request.get("productImage");
            String productCategory = (String) request.get("productCategory");
            Integer quantity = request.get("quantity") != null ? 
                Integer.valueOf(request.get("quantity").toString()) : 1;
            Double carbonFootprint = request.get("carbonFootprint") != null ? 
                Double.valueOf(request.get("carbonFootprint").toString()) : 0.0;
            
            Map<String, Object> result = cartService.addToCart(userId, productId, productName, 
                productPrice, productImage, productCategory, quantity, carbonFootprint);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error adding to cart: " + e.getMessage()
            ));
        }
    }

    // Remove item from cart
    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeFromCart(@RequestParam String userId, @RequestParam String productId) {
        try {
            Map<String, Object> result = cartService.removeFromCart(userId, productId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error removing from cart: " + e.getMessage()
            ));
        }
    }

    // Update item quantity in cart
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateCartItemQuantity(@RequestBody Map<String, Object> request) {
        try {
            String userId = (String) request.get("userId");
            String productId = (String) request.get("productId");
            Integer quantity = Integer.valueOf(request.get("quantity").toString());
            
            Map<String, Object> result = cartService.updateCartItemQuantity(userId, productId, quantity);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error updating cart: " + e.getMessage()
            ));
        }
    }

    // Get user cart
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> getUserCart(@PathVariable String userId) {
        try {
            List<Map<String, Object>> cart = cartService.getUserCart(userId);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            System.err.println("Error getting user cart: " + e.getMessage());
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Clear entire cart
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<Map<String, Object>> clearCart(@PathVariable String userId) {
        try {
            Map<String, Object> result = cartService.clearCart(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error clearing cart: " + e.getMessage()
            ));
        }
    }

    // Get cart summary
    @GetMapping("/summary/{userId}")
    public ResponseEntity<Map<String, Object>> getCartSummary(@PathVariable String userId) {
        try {
            Map<String, Object> summary = cartService.getCartSummary(userId);
            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            System.err.println("Error getting cart summary: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of());
        }
    }

    // Get all carts (admin endpoint)
    @GetMapping("/admin/all")
    public ResponseEntity<List<Map<String, Object>>> getAllCarts() {
        try {
            List<Map<String, Object>> carts = cartService.getAllCarts();
            return ResponseEntity.ok(carts);
        } catch (Exception e) {
            System.err.println("Error getting all carts: " + e.getMessage());
            return ResponseEntity.internalServerError().body(List.of());
        }
    }
}
