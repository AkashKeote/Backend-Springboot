package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.entity.Store;
import com.ecobazaar.backend.repository.StoreRepository;
import com.ecobazaar.backend.service.DataInitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/stores")
@CrossOrigin(origins = {"*", "https://ecobazzarx.web.app", "http://localhost:62804", "http://127.0.0.1:62804"})
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private DataInitializationService dataInitializationService;

    // Get all stores
    @GetMapping
    public ResponseEntity<List<Store>> getAllStores() {
        try {
            // Auto-initialize data if database is empty
            if (dataInitializationService.needsInitialization()) {
                dataInitializationService.initializeSampleData();
            }
            
            List<Store> stores = storeRepository.findAll();
            return ResponseEntity.ok(stores);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Get store by ID
    @GetMapping("/{storeId}")
    public ResponseEntity<Store> getStoreById(@PathVariable Long storeId) {
        try {
            Optional<Store> store = storeRepository.findById(storeId);
            if (store.isPresent()) {
                return ResponseEntity.ok(store.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get stores by owner
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<Store>> getStoresByOwner(@PathVariable String ownerId) {
        try {
            List<Store> stores = storeRepository.findByOwnerId(ownerId);
            return ResponseEntity.ok(stores);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Search stores
    @GetMapping("/search")
    public ResponseEntity<List<Store>> searchStores(@RequestParam String query) {
        try {
            List<Store> stores = storeRepository.findByStoreNameContainingIgnoreCase(query);
            return ResponseEntity.ok(stores);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Add new store
    @PostMapping
    public ResponseEntity<Map<String, Object>> addStore(@RequestBody Store store) {
        try {
            // Validate required fields
            if (store.getStoreName() == null || store.getStoreName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Store name is required"
                ));
            }
            
            if (store.getOwnerId() == null || store.getOwnerId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Owner ID is required"
                ));
            }
            
            // Generate unique storeId if not provided
            if (store.getStoreId() == null || store.getStoreId().trim().isEmpty()) {
                store.setStoreId("STORE_" + System.currentTimeMillis());
            }
            
            // Set default values if not provided
            if (store.getDescription() == null) {
                store.setDescription("");
            }
            
            if (store.getOwnerEmail() == null) {
                store.setOwnerEmail("");
            }
            
            if (store.getContactPhone() == null) {
                store.setContactPhone("");
            }
            
            if (store.getAddress() == null) {
                store.setAddress("");
            }
            
            if (store.getCity() == null) {
                store.setCity("");
            }
            
            if (store.getState() == null) {
                store.setState("");
            }
            
            if (store.getPincode() == null) {
                store.setPincode("");
            }
            
            Store savedStore = storeRepository.save(store);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Store added successfully",
                "store", savedStore
            ));
        } catch (Exception e) {
            System.err.println("Error adding store: " + e.getMessage());
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error adding store: " + e.getMessage()
            ));
        }
    }

    // Update store
    @PutMapping("/{storeId}")
    public ResponseEntity<Map<String, Object>> updateStore(
            @PathVariable Long storeId, 
            @RequestBody Store storeDetails) {
        try {
            Optional<Store> storeOptional = storeRepository.findById(storeId);
            if (storeOptional.isPresent()) {
                Store store = storeOptional.get();
                
                // Update only non-null fields to avoid overwriting with null values
                if (storeDetails.getStoreName() != null) {
                    store.setStoreName(storeDetails.getStoreName());
                }
                if (storeDetails.getDescription() != null) {
                    store.setDescription(storeDetails.getDescription());
                }
                // Don't update ownerId if not provided (keep existing)
                if (storeDetails.getOwnerId() != null) {
                    store.setOwnerId(storeDetails.getOwnerId());
                }
                if (storeDetails.getOwnerEmail() != null) {
                    store.setOwnerEmail(storeDetails.getOwnerEmail());
                }
                if (storeDetails.getContactPhone() != null) {
                    store.setContactPhone(storeDetails.getContactPhone());
                }
                if (storeDetails.getAddress() != null) {
                    store.setAddress(storeDetails.getAddress());
                }
                
                Store updatedStore = storeRepository.save(store);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Store updated successfully",
                    "store", updatedStore
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                    "success", false,
                    "message", "Store with ID " + storeId + " not found"
                ));
            }
        } catch (Exception e) {
            e.printStackTrace(); // Add logging to see the actual error
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error updating store: " + e.getMessage()
            ));
        }
    }

    // Delete store
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Map<String, Object>> deleteStore(@PathVariable Long storeId) {
        try {
            if (storeRepository.existsById(storeId)) {
                storeRepository.deleteById(storeId);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Store deleted successfully"
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error deleting store: " + e.getMessage()
            ));
        }
    }

    // Get store statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStoreStats() {
        try {
            long totalStores = storeRepository.count();
            
            return ResponseEntity.ok(Map.of(
                "totalStores", totalStores
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "totalStores", 0
            ));
        }
    }
}
