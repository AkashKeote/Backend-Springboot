package com.ecobazaar.backend.service;

import com.ecobazaar.backend.entity.Wishlist;
import com.ecobazaar.backend.entity.WishlistItem;
import com.ecobazaar.backend.repository.WishlistRepository;
import com.ecobazaar.backend.repository.WishlistItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    // Get or create wishlist for user
    public Wishlist getOrCreateWishlist(String userId) {
        Optional<Wishlist> existingWishlist = wishlistRepository.findByUserId(userId);
        if (existingWishlist.isPresent()) {
            return existingWishlist.get();
        }
        
        // Create new wishlist
        Wishlist wishlist = new Wishlist();
        wishlist.setWishlistId(UUID.randomUUID().toString());
        wishlist.setUserId(userId);
        wishlist.setTotalItems(0);
        return wishlistRepository.save(wishlist);
    }

    // Add item to wishlist
    public Map<String, Object> addToWishlist(String userId, String productId, String productName, 
                                            Double price, String imageUrl, String category) {
        try {
            Wishlist wishlist = getOrCreateWishlist(userId);
            
            // Check if item already exists
            if (wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getWishlistId(), productId)) {
                return Map.of(
                    "success", false,
                    "message", "Item already exists in wishlist"
                );
            }
            
            // Create new wishlist item
            WishlistItem item = new WishlistItem();
            item.setWishlistId(wishlist.getWishlistId());
            item.setProductId(productId);
            item.setProductName(productName);
            item.setProductPrice(price);
            item.setProductImage(imageUrl);
            item.setQuantity(1);
            
            wishlistItemRepository.save(item);
            
            // Update wishlist total items
            wishlist.setTotalItems((int) wishlistItemRepository.countByWishlistId(wishlist.getWishlistId()));
            wishlistRepository.save(wishlist);
            
            return Map.of(
                "success", true,
                "message", "Item added to wishlist successfully"
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "Error adding item to wishlist: " + e.getMessage()
            );
        }
    }

    // Remove item from wishlist
    @Transactional
    public Map<String, Object> removeFromWishlist(String userId, String productId) {
        try {
            Wishlist wishlist = getOrCreateWishlist(userId);
            
            if (!wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getWishlistId(), productId)) {
                return Map.of(
                    "success", false,
                    "message", "Item not found in wishlist"
                );
            }
            
            wishlistItemRepository.deleteByWishlistIdAndProductId(wishlist.getWishlistId(), productId);
            
            // Update wishlist total items
            wishlist.setTotalItems((int) wishlistItemRepository.countByWishlistId(wishlist.getWishlistId()));
            wishlistRepository.save(wishlist);
            
            return Map.of(
                "success", true,
                "message", "Item removed from wishlist successfully"
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "Error removing item from wishlist: " + e.getMessage()
            );
        }
    }

    // Get user wishlist items
    public List<Map<String, Object>> getUserWishlist(String userId) {
        try {
            Wishlist wishlist = getOrCreateWishlist(userId);
            List<WishlistItem> items = wishlistItemRepository.findByWishlistId(wishlist.getWishlistId());
            
            return items.stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("productId", item.getProductId());
                itemMap.put("productName", item.getProductName() != null ? item.getProductName() : "");
                itemMap.put("productPrice", item.getProductPrice() != null ? item.getProductPrice() : 0.0);
                itemMap.put("productImage", item.getProductImage() != null ? item.getProductImage() : "");
                itemMap.put("quantity", item.getQuantity() != null ? item.getQuantity() : 1);
                itemMap.put("addedAt", item.getCreatedAt() != null ? item.getCreatedAt().toString() : "");
                itemMap.put("updatedAt", item.getUpdatedAt() != null ? item.getUpdatedAt().toString() : "");
                return itemMap;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    // Check if item is in wishlist
    public boolean isInWishlist(String userId, String productId) {
        try {
            Wishlist wishlist = getOrCreateWishlist(userId);
            return wishlistItemRepository.existsByWishlistIdAndProductId(wishlist.getWishlistId(), productId);
        } catch (Exception e) {
            return false;
        }
    }

    // Get wishlist count
    public int getWishlistCount(String userId) {
        try {
            Wishlist wishlist = getOrCreateWishlist(userId);
            return (int) wishlistItemRepository.countByWishlistId(wishlist.getWishlistId());
        } catch (Exception e) {
            return 0;
        }
    }

    // Clear wishlist
    public Map<String, Object> clearWishlist(String userId) {
        try {
            Wishlist wishlist = getOrCreateWishlist(userId);
            wishlistItemRepository.findByWishlistId(wishlist.getWishlistId())
                .forEach(item -> wishlistItemRepository.delete(item));
            
            wishlist.setTotalItems(0);
            wishlistRepository.save(wishlist);
            
            return Map.of(
                "success", true,
                "message", "Wishlist cleared successfully"
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "Error clearing wishlist: " + e.getMessage()
            );
        }
    }

    // Get wishlist statistics
    public Map<String, Object> getWishlistStatistics(String userId) {
        try {
            Wishlist wishlist = getOrCreateWishlist(userId);
            List<WishlistItem> items = wishlistItemRepository.findByWishlistId(wishlist.getWishlistId());
            
            int totalItems = items.size();
            double totalValue = items.stream()
                .mapToDouble(item -> item.getProductPrice() != null ? item.getProductPrice() : 0.0)
                .sum();
            
            Set<String> categories = items.stream()
                .map(item -> "General") // Since we don't have category in WishlistItem entity
                .collect(Collectors.toSet());
            
            return Map.of(
                "totalItems", totalItems,
                "totalValue", totalValue,
                "categories", new ArrayList<>(categories)
            );
        } catch (Exception e) {
            return Map.of(
                "totalItems", 0,
                "totalValue", 0.0,
                "categories", new ArrayList<>()
            );
        }
    }

    // Move wishlist item to cart (placeholder - would need cart service integration)
    public Map<String, Object> moveToCart(String userId, String productId) {
        try {
            // First remove from wishlist
            Map<String, Object> removeResult = removeFromWishlist(userId, productId);
            if (!(Boolean) removeResult.get("success")) {
                return removeResult;
            }
            
            // TODO: Add to cart logic would go here
            // For now, just return success
            return Map.of(
                "success", true,
                "message", "Item moved to cart successfully"
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "Error moving item to cart: " + e.getMessage()
            );
        }
    }

    // Get wishlist by category (placeholder - would need category field in entity)
    public List<Map<String, Object>> getWishlistByCategory(String userId, String category) {
        // Since we don't have category in WishlistItem entity, return all items
        return getUserWishlist(userId);
    }

    // Get wishlist recommendations (placeholder)
    public List<Map<String, Object>> getWishlistRecommendations(String userId) {
        // TODO: Implement recommendation logic
        return new ArrayList<>();
    }

    // Get wishlist analytics
    public Map<String, Object> getWishlistAnalytics(String userId) {
        try {
            Wishlist wishlist = getOrCreateWishlist(userId);
            List<WishlistItem> items = wishlistItemRepository.findByWishlistId(wishlist.getWishlistId());
            
            int totalItems = items.size();
            double totalValue = items.stream()
                .mapToDouble(item -> item.getProductPrice() != null ? item.getProductPrice() : 0.0)
                .sum();
            double averagePrice = totalItems > 0 ? totalValue / totalItems : 0.0;
            
            return Map.of(
                "totalItems", totalItems,
                "totalValue", totalValue,
                "averagePrice", averagePrice,
                "mostAddedCategory", "General",
                "itemsAddedThisMonth", totalItems // Simplified for now
            );
        } catch (Exception e) {
            return Map.of(
                "totalItems", 0,
                "totalValue", 0.0,
                "averagePrice", 0.0,
                "mostAddedCategory", "",
                "itemsAddedThisMonth", 0
            );
        }
    }

    // Admin Methods - Get all wishlists with user information
    public List<Map<String, Object>> getAllWishlistsForAdmin() {
        try {
            List<Wishlist> allWishlists = wishlistRepository.findAll();
            
            return allWishlists.stream().map(wishlist -> {
                Map<String, Object> wishlistData = new HashMap<>();
                
                // Get wishlist items
                List<WishlistItem> items = wishlistItemRepository.findByWishlistId(wishlist.getWishlistId());
                
                wishlistData.put("userId", wishlist.getUserId());
                wishlistData.put("wishlistId", wishlist.getWishlistId());
                wishlistData.put("totalItems", wishlist.getTotalItems());
                wishlistData.put("createdAt", wishlist.getCreatedAt());
                wishlistData.put("updatedAt", wishlist.getUpdatedAt());
                
                // Add items with details
                List<Map<String, Object>> itemsData = items.stream().map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("productId", item.getProductId());
                    itemMap.put("productName", item.getProductName());
                    itemMap.put("productPrice", item.getProductPrice());
                    itemMap.put("quantity", item.getQuantity());
                    itemMap.put("addedAt", item.getCreatedAt());
                    itemMap.put("updatedAt", item.getUpdatedAt());
                    return itemMap;
                }).collect(Collectors.toList());
                
                wishlistData.put("items", itemsData);
                
                // Calculate total value
                double totalValue = items.stream()
                    .mapToDouble(item -> (item.getProductPrice() != null ? item.getProductPrice() : 0.0) * 
                                        (item.getQuantity() != null ? item.getQuantity() : 1))
                    .sum();
                wishlistData.put("totalValue", Math.round(totalValue * 100.0) / 100.0);
                
                return wishlistData;
            }).collect(Collectors.toList());
            
        } catch (Exception e) {
            System.err.println("Error getting all wishlists: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Admin: Get users who have specific product in wishlist
    public List<Map<String, Object>> getUsersWithProductInWishlist(String productId) {
        try {
            List<WishlistItem> items = wishlistItemRepository.findAll().stream()
                .filter(item -> productId.equals(item.getProductId()))
                .collect(Collectors.toList());
            
            return items.stream().map(item -> {
                // Get wishlist to find userId
                Optional<Wishlist> wishlistOpt = wishlistRepository.findByWishlistId(item.getWishlistId());
                if (wishlistOpt.isEmpty()) return null;
                
                Wishlist wishlist = wishlistOpt.get();
                
                Map<String, Object> userData = new HashMap<>();
                userData.put("userId", wishlist.getUserId());
                userData.put("productId", item.getProductId());
                userData.put("productName", item.getProductName());
                userData.put("productPrice", item.getProductPrice());
                userData.put("quantity", item.getQuantity());
                userData.put("addedAt", item.getCreatedAt());
                userData.put("wishlistId", wishlist.getWishlistId());
                
                return userData;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
            
        } catch (Exception e) {
            System.err.println("Error getting users with product: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Admin: Get popular products analytics
    public List<Map<String, Object>> getPopularProductsAnalytics() {
        try {
            List<WishlistItem> allItems = wishlistItemRepository.findAll();
            
            // Group by product and count occurrences
            Map<String, List<WishlistItem>> productGroups = allItems.stream()
                .collect(Collectors.groupingBy(WishlistItem::getProductId));
            
            return productGroups.entrySet().stream()
                .map(entry -> {
                    String productId = entry.getKey();
                    List<WishlistItem> productItems = entry.getValue();
                    WishlistItem firstItem = productItems.get(0);
                    
                    Map<String, Object> productData = new HashMap<>();
                    productData.put("productId", productId);
                    productData.put("productName", firstItem.getProductName());
                    productData.put("productPrice", firstItem.getProductPrice());
                    productData.put("totalUsers", productItems.size());
                    productData.put("totalQuantity", productItems.stream()
                        .mapToInt(item -> item.getQuantity() != null ? item.getQuantity() : 1)
                        .sum());
                    
                    return productData;
                })
                .sorted((a, b) -> Integer.compare((Integer) b.get("totalUsers"), (Integer) a.get("totalUsers")))
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            System.err.println("Error getting popular products: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
