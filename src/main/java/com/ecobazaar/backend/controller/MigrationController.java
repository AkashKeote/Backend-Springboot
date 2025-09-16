package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.service.DataMigrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Migration Controller for handling data migration from Firestore to MySQL
 * This controller provides endpoints to migrate data and check migration status
 */
@RestController
@RequestMapping("/api/migration")
@CrossOrigin(origins = "*")
public class MigrationController {

    @Autowired
    private DataMigrationService migrationService;

    /**
     * Test endpoint to check if controller is working
     */
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Migration controller is working!");
    }

    /**
     * Start complete data migration from Firestore exports
     */
    @PostMapping("/start")
    public ResponseEntity<String> startMigration() {
        try {
            String result = migrationService.migrateAllData();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error during migration: " + e.getMessage());
        }
    }

    /**
     * Get migration statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<String> getMigrationStats() {
        try {
            String stats = migrationService.getMigrationStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error getting migration stats: " + e.getMessage());
        }
    }

    /**
     * Migrate user data
     */
    @PostMapping("/user")
    public ResponseEntity<String> migrateUser(@RequestBody Map<String, Object> userData) {
        try {
            String firestoreUserId = (String) userData.get("firestoreUserId");
            String email = (String) userData.get("email");
            String name = (String) userData.get("name");
            String phone = (String) userData.get("phone");
            String role = (String) userData.get("role");

            migrationService.migrateUser(firestoreUserId, email, name, phone, role);
            return ResponseEntity.ok("User migrated successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error migrating user: " + e.getMessage());
        }
    }

    /**
     * Migrate product data
     */
    @PostMapping("/product")
    public ResponseEntity<String> migrateProduct(@RequestBody Map<String, Object> productData) {
        try {
            String firestoreProductId = (String) productData.get("firestoreProductId");
            String name = (String) productData.get("name");
            String description = (String) productData.get("description");
            Double price = Double.valueOf(productData.get("price").toString());
            Integer quantity = Integer.valueOf(productData.get("quantity").toString());
            String category = (String) productData.get("category");
            String storeId = (String) productData.get("storeId");
            String storeName = (String) productData.get("storeName");

            migrationService.migrateProduct(firestoreProductId, name, description, price, quantity, category, storeId, storeName);
            return ResponseEntity.ok("Product migrated successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error migrating product: " + e.getMessage());
        }
    }

    /**
     * Migrate wishlist data
     */
    @PostMapping("/wishlist")
    public ResponseEntity<String> migrateWishlist(@RequestBody Map<String, Object> wishlistData) {
        try {
            String firestoreWishlistId = (String) wishlistData.get("firestoreWishlistId");
            String userId = (String) wishlistData.get("userId");
            Integer totalItems = (Integer) wishlistData.get("totalItems");

            migrationService.migrateWishlist(firestoreWishlistId, userId, totalItems);
            return ResponseEntity.ok("Wishlist migrated successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error migrating wishlist: " + e.getMessage());
        }
    }

    /**
     * Migrate store data
     */
    @PostMapping("/store")
    public ResponseEntity<String> migrateStore(@RequestBody Map<String, Object> storeData) {
        try {
            String firestoreStoreId = (String) storeData.get("firestoreStoreId");
            String storeName = (String) storeData.get("storeName");
            String description = (String) storeData.get("description");
            String ownerId = (String) storeData.get("ownerId");
            String ownerEmail = (String) storeData.get("ownerEmail");
            String contactPhone = (String) storeData.get("contactPhone");
            String address = (String) storeData.get("address");

            migrationService.migrateStore(firestoreStoreId, storeName, description, ownerId, ownerEmail, contactPhone, address);
            return ResponseEntity.ok("Store migrated successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error migrating store: " + e.getMessage());
        }
    }

    /**
     * Create default user settings
     */
    @PostMapping("/user-settings")
    public ResponseEntity<String> createUserSettings(@RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            migrationService.createDefaultUserSettings(userId);
            return ResponseEntity.ok("User settings created successfully");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error creating user settings: " + e.getMessage());
        }
    }
}
