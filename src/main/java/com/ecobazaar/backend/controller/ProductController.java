package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.entity.Product;
import com.ecobazaar.backend.repository.ProductRepository;
import com.ecobazaar.backend.service.DataInitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = {"*", "https://ecobazzarx.web.app", "http://localhost:62804", "http://127.0.0.1:62804"})
public class ProductController {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private DataInitializationService dataInitializationService;

    // Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        try {
            // Auto-initialize data if database is empty
            if (dataInitializationService.needsInitialization()) {
                dataInitializationService.initializeSampleData();
            }
            
            List<Product> products = productRepository.findAll();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Get product by ID
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        try {
            Optional<Product> product = productRepository.findById(productId);
            if (product.isPresent()) {
                return ResponseEntity.ok(product.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get products by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        try {
            List<Product> products = productRepository.findByCategory(category);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Get products by store
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<Product>> getProductsByStore(@PathVariable String storeId) {
        try {
            List<Product> products = productRepository.findByStoreId(storeId);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Search products
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query) {
        try {
            List<Product> products = productRepository.findByNameContainingIgnoreCase(query);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Add new product (Admin/Shopkeeper only)
    @PostMapping
    public ResponseEntity<Map<String, Object>> addProduct(@RequestBody Product product) {
        try {
            System.out.println("📦 Received product creation request: " + product.getName());
            
            // Validate required fields
            if (product.getName() == null || product.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Product name is required",
                    "field", "name"
                ));
            }
            
            if (product.getPrice() == null || product.getPrice() <= 0) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Valid price is required (must be greater than 0)",
                    "field", "price"
                ));
            }
            
            if (product.getStoreId() == null || product.getStoreId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Store ID is required",
                    "field", "storeId"
                ));
            }
            
            // Set default values if not provided
            if (product.getDescription() == null) {
                product.setDescription("");
            }
            
            if (product.getQuantity() == null) {
                product.setQuantity(0);
            }
            
            if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
                product.setCategory("General");
            }
            
            if (product.getImageUrl() == null) {
                product.setImageUrl("");
            }
            
            if (product.getStoreName() == null) {
                product.setStoreName("");
            }
            
            if (product.getIcon() == null) {
                product.setIcon("📦");
            }
            
            if (product.getColor() == null) {
                product.setColor("#4CAF50");
            }
            
            if (product.getCarbonFootprint() == null) {
                product.setCarbonFootprint(0.0);
            }
            
            if (product.getEcoPoints() == null) {
                product.setEcoPoints(0);
            }
            
            // Set active by default
            product.setIsActive(true);
            
            Product savedProduct = productRepository.save(product);
            System.out.println("✅ Product saved successfully with ID: " + savedProduct.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Product added successfully",
                "product", savedProduct,
                "productId", savedProduct.getId()
            ));
        } catch (Exception e) {
            System.err.println("❌ Error adding product: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error adding product: " + e.getMessage(),
                "error", e.getClass().getSimpleName()
            ));
        }
    }

    // Update product (Admin/Shopkeeper only)
    @PutMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable Long productId, 
            @RequestBody Product productDetails) {
        try {
            Optional<Product> productOptional = productRepository.findById(productId);
            if (productOptional.isPresent()) {
                Product product = productOptional.get();
                product.setName(productDetails.getName());
                product.setDescription(productDetails.getDescription());
                product.setPrice(productDetails.getPrice());
                product.setQuantity(productDetails.getQuantity());
                product.setCategory(productDetails.getCategory());
                product.setImageUrl(productDetails.getImageUrl());
                product.setStoreId(productDetails.getStoreId());
                product.setStoreName(productDetails.getStoreName());
                
                Product updatedProduct = productRepository.save(product);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Product updated successfully",
                    "product", updatedProduct
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error updating product: " + e.getMessage()
            ));
        }
    }

    // Delete product (Admin/Shopkeeper only)
    @DeleteMapping("/{productId}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable Long productId) {
        try {
            if (productRepository.existsById(productId)) {
                productRepository.deleteById(productId);
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Product deleted successfully"
                ));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error deleting product: " + e.getMessage()
            ));
        }
    }

    // Get product statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getProductStats() {
        try {
            long totalProducts = productRepository.count();
            List<String> categories = productRepository.findDistinctCategories();
            
            return ResponseEntity.ok(Map.of(
                "totalProducts", totalProducts,
                "categories", categories,
                "totalCategories", categories.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "totalProducts", 0,
                "categories", List.of(),
                "totalCategories", 0
            ));
        }
    }
}
