package com.ecobazaar.backend.service;

import com.ecobazaar.backend.entity.Cart;
import com.ecobazaar.backend.entity.CartItem;
import com.ecobazaar.backend.entity.User;
import com.ecobazaar.backend.repository.CartRepository;
import com.ecobazaar.backend.repository.CartItemRepository;
import com.ecobazaar.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;
    
    @Autowired
    private UserRepository userRepository;

    // Get or create cart for a user
    private Cart getOrCreateCart(String userId) {
        Optional<Cart> existingCart = cartRepository.findByUserId(userId);
        if (existingCart.isPresent()) {
            return existingCart.get();
        }
        
        // Create new cart for user
        String cartId = "cart_" + userId + "_" + System.currentTimeMillis();
        Cart newCart = new Cart(cartId, userId);
        return cartRepository.save(newCart);
    }

    // Add item to cart
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> addToCart(String userId, String productId, String productName, 
                                       Double productPrice, String productImage, String productCategory, 
                                       Integer quantity, Double carbonFootprint) {
        try {
            System.out.println("🛒 DEBUG: Adding to cart - userId: " + userId + ", productId: " + productId + ", price: " + productPrice);
            
            // Validate inputs first
            if (userId == null || userId.trim().isEmpty()) {
                throw new IllegalArgumentException("User ID cannot be null or empty");
            }
            if (productId == null || productId.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be null or empty");
            }
            
            Cart cart = getOrCreateCart(userId);
            System.out.println("🛒 DEBUG: Cart obtained - cartId: " + cart.getCartId());
            
            // Check if item already exists in cart
            Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
            
            CartItem savedItem;
            if (existingItem.isPresent()) {
                // Update quantity of existing item
                CartItem item = existingItem.get();
                int newQuantity = item.getQuantity() + (quantity != null ? quantity : 1);
                item.setQuantity(newQuantity);
                item.calculateTotalPrice(); // Recalculate total price
                savedItem = cartItemRepository.save(item);
                System.out.println("🛒 DEBUG: Updated existing item - new quantity: " + newQuantity);
            } else {
                // Add new item to cart
                CartItem newItem = new CartItem(
                    userId,
                    getUsernameFromUserId(userId),
                    productId,
                    productName != null ? productName : "Unknown Product",
                    productPrice != null ? productPrice : 0.0,
                    productImage != null ? productImage : "",
                    productCategory != null ? productCategory : "General",
                    quantity != null ? quantity : 1,
                    carbonFootprint != null ? carbonFootprint : 0.0
                );
                newItem.calculateTotalPrice(); // Calculate total price
                savedItem = cartItemRepository.save(newItem);
                System.out.println("🛒 DEBUG: Created new item - itemId: " + savedItem.getId());
            }
            
            // Update cart totals
            updateCartTotals(cart);
            System.out.println("🛒 DEBUG: Cart totals updated successfully");
            
            return Map.of(
                "success", true,
                "message", "Item added to cart successfully",
                "itemId", savedItem.getId()
            );
        } catch (Exception e) {
            System.err.println("🛒 ERROR: Failed to add to cart: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to add item to cart: " + e.getMessage(), e);
        }
    }

    // Remove item from cart
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> removeFromCart(String userId, String productId) {
        try {
            if (userId == null || userId.trim().isEmpty()) {
                throw new IllegalArgumentException("User ID cannot be null or empty");
            }
            if (productId == null || productId.trim().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be null or empty");
            }
            
            Cart cart = getOrCreateCart(userId);
            
            if (!cartItemRepository.existsByUserIdAndProductId(userId, productId)) {
                return Map.of(
                    "success", false,
                    "message", "Item not found in cart"
                );
            }
            
            cartItemRepository.deleteByUserIdAndProductId(userId, productId);
            
            // Update cart totals
            updateCartTotals(cart);
            
            return Map.of(
                "success", true,
                "message", "Item removed from cart successfully"
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "Error removing item from cart: " + e.getMessage()
            );
        }
    }

    // Update item quantity in cart
    @Transactional
    public Map<String, Object> updateCartItemQuantity(String userId, String productId, Integer quantity) {
        try {
            Cart cart = getOrCreateCart(userId);
            
            Optional<CartItem> cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
            
            if (cartItem.isEmpty()) {
                return Map.of(
                    "success", false,
                    "message", "Item not found in cart"
                );
            }
            
            if (quantity <= 0) {
                // Remove item if quantity is 0 or negative
                cartItemRepository.deleteByUserIdAndProductId(userId, productId);
            } else {
                // Update quantity
                CartItem item = cartItem.get();
                item.setQuantity(quantity);
                item.calculateTotalPrice(); // Recalculate total price
                cartItemRepository.save(item);
            }
            
            // Update cart totals
            updateCartTotals(cart);
            
            return Map.of(
                "success", true,
                "message", "Cart updated successfully"
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "Error updating cart: " + e.getMessage()
            );
        }
    }

    // Get user cart items
    public List<Map<String, Object>> getUserCart(String userId) {
        try {
            List<CartItem> items = cartItemRepository.findByUserId(userId);
            
            return items.stream().map(item -> {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("id", item.getId());
                itemMap.put("userId", item.getUserId());
                itemMap.put("customerUsername", item.getCustomerUsername() != null ? item.getCustomerUsername() : userId);
                itemMap.put("productId", item.getProductId());
                itemMap.put("name", item.getProductName() != null ? item.getProductName() : ""); // Frontend expects 'name'
                itemMap.put("productName", item.getProductName() != null ? item.getProductName() : "");
                itemMap.put("price", item.getProductPrice() != null ? item.getProductPrice() : 0.0); // Frontend expects 'price'
                itemMap.put("productPrice", item.getProductPrice() != null ? item.getProductPrice() : 0.0);
                itemMap.put("productImage", item.getProductImage() != null ? item.getProductImage() : "");
                itemMap.put("category", item.getProductCategory() != null ? item.getProductCategory() : "General"); // Frontend expects 'category'
                itemMap.put("productCategory", item.getProductCategory() != null ? item.getProductCategory() : "General");
                itemMap.put("quantity", item.getQuantity() != null ? item.getQuantity() : 1);
                itemMap.put("carbonFootprint", item.getCarbonFootprint() != null ? item.getCarbonFootprint() : 0.0);
                itemMap.put("totalPrice", item.getTotalPrice() != null ? item.getTotalPrice() : 0.0);
                itemMap.put("totalCarbonFootprint", item.getTotalCarbonFootprint());
                itemMap.put("createdAt", item.getCreatedAt());
                itemMap.put("updatedAt", item.getUpdatedAt());
                return itemMap;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting user cart: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Clear entire cart
    @Transactional
    public Map<String, Object> clearCart(String userId) {
        try {
            cartItemRepository.deleteByUserId(userId);
            
            // Also clean up the Cart entry if it exists
            Optional<Cart> cart = cartRepository.findByUserId(userId);
            if (cart.isPresent()) {
                Cart existingCart = cart.get();
                existingCart.setTotalItems(0);
                existingCart.setTotalAmount(0.0);
                cartRepository.save(existingCart);
            }
            
            return Map.of(
                "success", true,
                "message", "Cart cleared successfully"
            );
        } catch (Exception e) {
            return Map.of(
                "success", false,
                "message", "Error clearing cart: " + e.getMessage()
            );
        }
    }

    // Get cart summary
    public Map<String, Object> getCartSummary(String userId) {
        try {
            List<CartItem> items = cartItemRepository.findByUserId(userId);
            
            double totalAmount = items.stream().mapToDouble(CartItem::getTotalPrice).sum();
            double totalCarbonFootprint = items.stream().mapToDouble(CartItem::getTotalCarbonFootprint).sum();
            int totalItems = items.size();
            int totalQuantity = items.stream().mapToInt(CartItem::getQuantity).sum();
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("userId", userId);
            summary.put("totalItems", totalItems);
            summary.put("totalQuantity", totalQuantity);
            summary.put("totalAmount", totalAmount);
            summary.put("totalCarbonFootprint", totalCarbonFootprint);
            summary.put("updatedAt", java.time.LocalDateTime.now());
            
            return summary;
        } catch (Exception e) {
            System.err.println("Error getting cart summary: " + e.getMessage());
            return new HashMap<>();
        }
    }

    // Helper method to update cart totals
    private void updateCartTotals(Cart cart) {
        List<CartItem> items = cartItemRepository.findByUserId(cart.getUserId());
        
        int totalItems = items.size();
        double totalAmount = items.stream().mapToDouble(CartItem::getTotalPrice).sum();
        
        cart.setTotalItems(totalItems);
        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
    }

    // Get all carts (for admin)
    public List<Map<String, Object>> getAllCarts() {
        try {
            List<Cart> carts = cartRepository.findAll();
            
            return carts.stream().map(cart -> {
                List<CartItem> items = cartItemRepository.findByUserId(cart.getUserId());
                
                Map<String, Object> cartMap = new HashMap<>();
                cartMap.put("cartId", cart.getCartId());
                cartMap.put("userId", cart.getUserId());
                cartMap.put("totalItems", cart.getTotalItems());
                cartMap.put("totalAmount", cart.getTotalAmount());
                cartMap.put("createdAt", cart.getCreatedAt());
                cartMap.put("updatedAt", cart.getUpdatedAt());
                cartMap.put("items", items.stream().map(item -> Map.of(
                    "productId", item.getProductId(),
                    "productName", item.getProductName() != null ? item.getProductName() : "",
                    "quantity", item.getQuantity() != null ? item.getQuantity() : 1,
                    "totalPrice", item.getTotalPrice(),
                    "createdAt", item.getCreatedAt()
                )).collect(Collectors.toList()));
                
                return cartMap;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting all carts: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    // Helper method to get username from userId by fetching from Users table
    private String getUsernameFromUserId(String userId) {
        try {
            // First, try to find user by email (since userId is usually email)
            Optional<User> user = userRepository.findByEmail(userId);
            if (user.isPresent()) {
                return user.get().getName();
            }
            
            // If not found by email, try to find by numeric ID
            try {
                Long numericId = Long.parseLong(userId);
                Optional<User> userById = userRepository.findById(numericId);
                if (userById.isPresent()) {
                    return userById.get().getName();
                }
            } catch (NumberFormatException e) {
                // userId is not numeric, continue with other methods
            }
            
            // If not found by email or ID, create a friendly username from email-like userId
            if (userId != null && userId.contains("@")) {
                String emailPrefix = userId.split("@")[0];
                // Convert email prefix to title case (e.g., "akash" -> "Akash")
                return emailPrefix.substring(0, 1).toUpperCase() + emailPrefix.substring(1).toLowerCase();
            }
            
            // For non-email userIds like "testuser123", create a friendly name
            if (userId != null && !userId.isEmpty()) {
                // Convert userId to a more friendly format
                String friendlyName = userId.replaceAll("\\d+$", ""); // Remove trailing numbers
                if (friendlyName.isEmpty()) {
                    friendlyName = "User " + userId;
                } else {
                    // Capitalize first letter
                    friendlyName = friendlyName.substring(0, 1).toUpperCase() + 
                                 friendlyName.substring(1).toLowerCase() + 
                                 " (" + userId.replaceAll(".*?(\\d+)$", "$1") + ")";
                }
                return friendlyName;
            }
            
            return "Guest User";
        } catch (Exception e) {
            System.err.println("Error fetching username for userId: " + userId + ", Error: " + e.getMessage());
            return userId != null ? ("User " + userId) : "Guest User";
        }
    }
}
