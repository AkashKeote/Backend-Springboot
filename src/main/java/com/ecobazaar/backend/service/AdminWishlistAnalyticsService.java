package com.ecobazaar.backend.service;

import com.ecobazaar.backend.entity.Wishlist;
import com.ecobazaar.backend.entity.WishlistItem;
import com.ecobazaar.backend.entity.User;
import com.ecobazaar.backend.repository.WishlistRepository;
import com.ecobazaar.backend.repository.WishlistItemRepository;
import com.ecobazaar.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminWishlistAnalyticsService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private WishlistItemRepository wishlistItemRepository;

    @Autowired
    private UserRepository userRepository;

    // Get all wishlists with user information
    public List<Map<String, Object>> getAllWishlistsWithUsers() {
        try {
            List<Wishlist> wishlists = wishlistRepository.findAll();
            
            return wishlists.stream().map(wishlist -> {
                Map<String, Object> wishlistData = new HashMap<>();
                
                // Get user info
                User user = null;
                try {
                    Optional<User> userOpt = userRepository.findById(Long.parseLong(wishlist.getUserId()));
                    user = userOpt.orElse(null);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid userId format: " + wishlist.getUserId());
                }
                
                // Get wishlist items
                List<WishlistItem> items = wishlistItemRepository.findByWishlistId(wishlist.getWishlistId());
                
                wishlistData.put("wishlistId", wishlist.getWishlistId());
                wishlistData.put("userId", wishlist.getUserId());
                wishlistData.put("userName", user != null ? user.getName() : "Unknown User");
                wishlistData.put("userEmail", user != null ? user.getEmail() : "Unknown Email");
                wishlistData.put("totalItems", wishlist.getTotalItems());
                wishlistData.put("createdAt", wishlist.getCreatedAt());
                wishlistData.put("updatedAt", wishlist.getUpdatedAt());
                
                // Add items details
                List<Map<String, Object>> itemsData = items.stream().map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("productId", item.getProductId());
                    itemMap.put("productName", item.getProductName());
                    itemMap.put("productPrice", item.getProductPrice());
                    itemMap.put("quantity", item.getQuantity());
                    itemMap.put("addedAt", item.getCreatedAt());
                    return itemMap;
                }).collect(Collectors.toList());
                
                wishlistData.put("items", itemsData);
                
                return wishlistData;
            }).collect(Collectors.toList());
            
        } catch (Exception e) {
            System.err.println("Error getting all wishlists: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Get wishlist analytics summary
    public Map<String, Object> getWishlistAnalytics() {
        try {
            // Total statistics
            long totalWishlists = wishlistRepository.count();
            long totalWishlistItems = wishlistItemRepository.count();
            
            // Get all items for analysis
            List<WishlistItem> allItems = wishlistItemRepository.findAll();
            
            // Calculate total value
            double totalValue = allItems.stream()
                .mapToDouble(item -> item.getProductPrice() != null ? item.getProductPrice() : 0.0)
                .sum();
            
            // Average items per wishlist
            double avgItemsPerWishlist = totalWishlists > 0 ? (double) totalWishlistItems / totalWishlists : 0.0;
            
            // Most popular products
            Map<String, Long> productCounts = allItems.stream()
                .collect(Collectors.groupingBy(
                    WishlistItem::getProductId,
                    Collectors.counting()
                ));
            
            String mostPopularProduct = productCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
            
            // Recent activity (last 7 days)
            LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
            long recentItems = allItems.stream()
                .filter(item -> item.getCreatedAt() != null && item.getCreatedAt().isAfter(weekAgo))
                .count();
            
            Map<String, Object> analytics = new HashMap<>();
            analytics.put("totalWishlists", totalWishlists);
            analytics.put("totalWishlistItems", totalWishlistItems);
            analytics.put("totalValue", Math.round(totalValue * 100.0) / 100.0);
            analytics.put("avgItemsPerWishlist", Math.round(avgItemsPerWishlist * 100.0) / 100.0);
            analytics.put("mostPopularProduct", mostPopularProduct);
            analytics.put("recentItemsThisWeek", recentItems);
            analytics.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            return analytics;
            
        } catch (Exception e) {
            System.err.println("Error getting wishlist analytics: " + e.getMessage());
            return Map.of("error", e.getMessage());
        }
    }

    // Get popular products in wishlists
    public List<Map<String, Object>> getPopularWishlistProducts() {
        try {
            List<WishlistItem> allItems = wishlistItemRepository.findAll();
            
            // Group by product and count occurrences
            Map<String, List<WishlistItem>> productGroups = allItems.stream()
                .collect(Collectors.groupingBy(WishlistItem::getProductId));
            
            return productGroups.entrySet().stream()
                .map(entry -> {
                    String productId = entry.getKey();
                    List<WishlistItem> items = entry.getValue();
                    WishlistItem firstItem = items.get(0);
                    
                    Map<String, Object> productData = new HashMap<>();
                    productData.put("productId", productId);
                    productData.put("productName", firstItem.getProductName());
                    productData.put("productPrice", firstItem.getProductPrice());
                    productData.put("totalUsers", items.size());
                    productData.put("totalQuantity", items.stream().mapToInt(item -> 
                        item.getQuantity() != null ? item.getQuantity() : 1).sum());
                    
                    return productData;
                })
                .sorted((a, b) -> Integer.compare((Integer) b.get("totalUsers"), (Integer) a.get("totalUsers")))
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            System.err.println("Error getting popular products: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Get users who added specific product to wishlist
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
                
                // Get user info
                Optional<User> userOpt = userRepository.findById(Long.parseLong(wishlist.getUserId()));
                User user = userOpt.orElse(null);
                
                Map<String, Object> userData = new HashMap<>();
                userData.put("userId", wishlist.getUserId());
                userData.put("userName", user != null ? user.getName() : "Unknown User");
                userData.put("userEmail", user != null ? user.getEmail() : "Unknown Email");
                userData.put("userRole", user != null ? user.getRole().toString() : "Unknown");
                userData.put("quantity", item.getQuantity());
                userData.put("addedAt", item.getCreatedAt());
                userData.put("productName", item.getProductName());
                userData.put("productPrice", item.getProductPrice());
                
                return userData;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
            
        } catch (Exception e) {
            System.err.println("Error getting users with product: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Get wishlist items by date range
    public List<Map<String, Object>> getWishlistItemsByDateRange(String startDate, String endDate) {
        try {
            LocalDateTime start = LocalDateTime.parse(startDate + "T00:00:00");
            LocalDateTime end = LocalDateTime.parse(endDate + "T23:59:59");
            
            List<WishlistItem> items = wishlistItemRepository.findAll().stream()
                .filter(item -> item.getCreatedAt() != null)
                .filter(item -> !item.getCreatedAt().isBefore(start) && !item.getCreatedAt().isAfter(end))
                .collect(Collectors.toList());
            
            return items.stream().map(item -> {
                // Get wishlist to find userId
                Optional<Wishlist> wishlistOpt = wishlistRepository.findByWishlistId(item.getWishlistId());
                if (wishlistOpt.isEmpty()) return null;
                
                Wishlist wishlist = wishlistOpt.get();
                
                // Get user info
                Optional<User> userOpt = userRepository.findById(Long.parseLong(wishlist.getUserId()));
                User user = userOpt.orElse(null);
                
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("productId", item.getProductId());
                itemData.put("productName", item.getProductName());
                itemData.put("productPrice", item.getProductPrice());
                itemData.put("quantity", item.getQuantity());
                itemData.put("addedAt", item.getCreatedAt());
                itemData.put("userId", wishlist.getUserId());
                itemData.put("userName", user != null ? user.getName() : "Unknown User");
                itemData.put("userEmail", user != null ? user.getEmail() : "Unknown Email");
                
                return itemData;
            })
            .filter(Objects::nonNull)
            .sorted((a, b) -> {
                LocalDateTime dateA = (LocalDateTime) a.get("addedAt");
                LocalDateTime dateB = (LocalDateTime) b.get("addedAt");
                return dateB.compareTo(dateA); // Latest first
            })
            .collect(Collectors.toList());
            
        } catch (Exception e) {
            System.err.println("Error getting items by date range: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
