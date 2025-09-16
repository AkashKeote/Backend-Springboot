package com.ecobazaar.backend.service;

import com.ecobazaar.backend.entity.*;
import com.ecobazaar.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Data Migration Service for migrating data from Firestore to MySQL
 * This service provides methods to migrate existing Firestore data to MySQL
 */
@Service
@Transactional
public class DataMigrationService {

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private WishlistRepository wishlistRepository;
    
    @Autowired
    private WishlistItemRepository wishlistItemRepository;
    
    @Autowired
    private CartRepository cartRepository;
    
    
    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;
    
    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private UserOrderRepository userOrderRepository;
    
    @Autowired
    private UserSettingsRepository userSettingsRepository;

    /**
     * Migrate user data from Firestore to MySQL
     * This method should be called with data extracted from Firestore
     */
    public User migrateUser(String firestoreUserId, String email, String name, String phone, String role) {
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            return userRepository.findByEmail(email).get();
        }
        
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPhone(phone);
        user.setRole(UserRole.valueOf(role.toUpperCase()));
        user.setIsActive(true);
        // Set a default password for migrated users
        user.setPassword("$2a$10$defaultPasswordForMigratedUsers");
        
        return userRepository.save(user);
    }

    /**
     * Migrate product data from Firestore to MySQL
     */
    public Product migrateProduct(String firestoreProductId, String name, String description, 
                                Double price, Integer quantity, String category, String storeId, String storeName) {
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setCategory(category);
        product.setStoreId(storeId);
        product.setStoreName(storeName);
        product.setIsActive(true);
        
        return productRepository.save(product);
    }

    /**
     * Migrate wishlist data from Firestore to MySQL
     */
    public Wishlist migrateWishlist(String firestoreWishlistId, String userId, Integer totalItems) {
        Wishlist wishlist = new Wishlist();
        wishlist.setWishlistId(firestoreWishlistId != null && !firestoreWishlistId.isEmpty() ? firestoreWishlistId : UUID.randomUUID().toString());
        wishlist.setUserId(userId);
        wishlist.setTotalItems(totalItems != null ? totalItems : 0);
        
        return wishlistRepository.save(wishlist);
    }

    /**
     * Migrate wishlist item data from Firestore to MySQL
     */
    public WishlistItem migrateWishlistItem(String wishlistId, String productId, String productName, 
                                          Double productPrice, String productImage, Integer quantity) {
        WishlistItem item = new WishlistItem();
        item.setWishlistId(wishlistId);
        item.setProductId(productId);
        item.setProductName(productName);
        item.setProductPrice(productPrice);
        item.setProductImage(productImage);
        item.setQuantity(quantity != null ? quantity : 1);
        
        return wishlistItemRepository.save(item);
    }

    /**
     * Migrate cart data from Firestore to MySQL
     */
    public Cart migrateCart(String firestoreCartId, String userId, Integer totalItems, Double totalAmount) {
        Cart cart = new Cart();
        cart.setCartId(firestoreCartId != null && !firestoreCartId.isEmpty() ? firestoreCartId : UUID.randomUUID().toString());
        cart.setUserId(userId);
        cart.setTotalItems(totalItems != null ? totalItems : 0);
        cart.setTotalAmount(totalAmount != null ? totalAmount : 0.0);
        
        return cartRepository.save(cart);
    }


    /**
     * Migrate store data from Firestore to MySQL
     */
    public Store migrateStore(String firestoreStoreId, String storeName, String description, 
                            String ownerId, String ownerEmail, String contactPhone, String address) {
        Store store = new Store();
        store.setStoreId(firestoreStoreId != null && !firestoreStoreId.isEmpty() ? firestoreStoreId : UUID.randomUUID().toString());
        store.setStoreName(storeName);
        store.setDescription(description);
        store.setOwnerId(ownerId);
        store.setOwnerEmail(ownerEmail);
        store.setContactPhone(contactPhone);
        store.setAddress(address);
        store.setIsActive(true);
        store.setIsVerified(false);
        
        return storeRepository.save(store);
    }

    /**
     * Migrate payment transaction data from Firestore to MySQL
     */
    public PaymentTransaction migratePaymentTransaction(String firestoreTransactionId, String orderId, 
                                                      String userId, Double amount, String paymentMethod, 
                                                      String status, String gatewayTransactionId) {
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setTransactionId(firestoreTransactionId != null && !firestoreTransactionId.isEmpty() ? firestoreTransactionId : UUID.randomUUID().toString());
        transaction.setOrderId(orderId);
        transaction.setUserId(userId);
        transaction.setAmount(amount);
        transaction.setPaymentMethod(paymentMethod);
        transaction.setStatus(status);
        transaction.setGatewayTransactionId(gatewayTransactionId);
        
        return paymentTransactionRepository.save(transaction);
    }

    /**
     * Migrate user order data from Firestore to MySQL
     */
    public UserOrder migrateUserOrder(String firestoreUserOrderId, String userId, String orderId, 
                                    String productId, String productName, Double productPrice, 
                                    Integer quantity, String storeId, String storeName) {
        UserOrder userOrder = new UserOrder();
        userOrder.setUserOrderId(firestoreUserOrderId != null && !firestoreUserOrderId.isEmpty() ? firestoreUserOrderId : UUID.randomUUID().toString());
        userOrder.setUserId(userId);
        userOrder.setOrderId(orderId);
        userOrder.setProductId(productId);
        userOrder.setProductName(productName);
        userOrder.setProductPrice(productPrice);
        userOrder.setQuantity(quantity);
        userOrder.setTotalAmount(productPrice * quantity);
        userOrder.setStoreId(storeId);
        userOrder.setStoreName(storeName);
        
        return userOrderRepository.save(userOrder);
    }

    /**
     * Create default user settings for a user
     */
    public UserSettings createDefaultUserSettings(String userId) {
        UserSettings settings = new UserSettings();
        settings.setUserId(userId);
        // Default settings are already set in the entity constructor
        
        return userSettingsRepository.save(settings);
    }

    /**
     * Migrate all data from exported Firestore JSON files
     */
    public String migrateAllData() {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            
            String firestoreExportsPath = "firestore-exports";
            File exportsDir = new File(firestoreExportsPath);
            
            if (!exportsDir.exists()) {
                return "Error: firestore-exports directory not found. Please run the export script first.";
            }
            
            StringBuilder result = new StringBuilder();
            result.append("Starting data migration from Firestore exports...\n\n");
            
            // Migrate users
            result.append(migrateUsersFromFile(objectMapper, exportsDir));
            
            // Migrate products
            result.append(migrateProductsFromFile(objectMapper, exportsDir));
            
            // Migrate stores
            result.append(migrateStoresFromFile(objectMapper, exportsDir));
            
            // Migrate wishlists
            result.append(migrateWishlistsFromFile(objectMapper, exportsDir));
            
            // Migrate wishlist items
            result.append(migrateWishlistItemsFromFile(objectMapper, exportsDir));
            
            // Migrate carts
            result.append(migrateCartsFromFile(objectMapper, exportsDir));
            
            
            // Migrate payment transactions
            result.append(migratePaymentTransactionsFromFile(objectMapper, exportsDir));
            
            // Migrate user orders
            result.append(migrateUserOrdersFromFile(objectMapper, exportsDir));
            
            // Migrate user settings
            result.append(migrateUserSettingsFromFile(objectMapper, exportsDir));
            
            result.append("\nMigration completed successfully!\n");
            result.append(getMigrationStats());
            
            return result.toString();
            
        } catch (Exception e) {
            return "Error during migration: " + e.getMessage();
        }
    }
    
    private String migrateUsersFromFile(ObjectMapper objectMapper, File exportsDir) {
        try {
            File usersFile = new File(exportsDir, "users.json");
            if (!usersFile.exists()) {
                return "Users file not found, skipping...\n";
            }
            
            List<Map<String, Object>> users = objectMapper.readValue(usersFile, List.class);
            int migrated = 0;
            
            for (Map<String, Object> userData : users) {
                try {
                    String email = (String) userData.get("email");
                    String name = (String) userData.get("name");
                    String phone = (String) userData.get("phone");
                    String role = (String) userData.get("role");
                    
                    if (email != null && name != null) {
                        migrateUser("", email, name, phone != null ? phone : "", role != null ? role : "USER");
                        migrated++;
                    }
                } catch (Exception e) {
                    System.err.println("Error migrating user: " + e.getMessage());
                }
            }
            
            return String.format("Migrated %d users\n", migrated);
        } catch (Exception e) {
            return "Error migrating users: " + e.getMessage() + "\n";
        }
    }
    
    private String migrateProductsFromFile(ObjectMapper objectMapper, File exportsDir) {
        try {
            File productsFile = new File(exportsDir, "products.json");
            if (!productsFile.exists()) {
                return "Products file not found, skipping...\n";
            }
            
            List<Map<String, Object>> products = objectMapper.readValue(productsFile, List.class);
            int migrated = 0;
            
            for (Map<String, Object> productData : products) {
                try {
                    String name = (String) productData.get("name");
                    String description = (String) productData.get("description");
                    Double price = productData.get("price") != null ? Double.valueOf(productData.get("price").toString()) : 0.0;
                    Integer quantity = productData.get("quantity") != null ? Integer.valueOf(productData.get("quantity").toString()) : 0;
                    String category = (String) productData.get("category");
                    String storeId = (String) productData.get("storeId");
                    String storeName = (String) productData.get("storeName");
                    
                    if (name != null) {
                        migrateProduct("", name, description != null ? description : "", price, quantity, 
                                     category != null ? category : "", storeId != null ? storeId : "", 
                                     storeName != null ? storeName : "");
                        migrated++;
                    }
                } catch (Exception e) {
                    System.err.println("Error migrating product: " + e.getMessage());
                }
            }
            
            return String.format("Migrated %d products\n", migrated);
        } catch (Exception e) {
            return "Error migrating products: " + e.getMessage() + "\n";
        }
    }
    
    private String migrateStoresFromFile(ObjectMapper objectMapper, File exportsDir) {
        try {
            File storesFile = new File(exportsDir, "stores.json");
            if (!storesFile.exists()) {
                return "Stores file not found, skipping...\n";
            }
            
            List<Map<String, Object>> stores = objectMapper.readValue(storesFile, List.class);
            int migrated = 0;
            
            for (Map<String, Object> storeData : stores) {
                try {
                    String storeName = (String) storeData.get("storeName");
                    String description = (String) storeData.get("description");
                    String ownerId = (String) storeData.get("ownerId");
                    String ownerEmail = (String) storeData.get("ownerEmail");
                    String contactPhone = (String) storeData.get("contactPhone");
                    String address = (String) storeData.get("address");
                    
                    if (storeName != null) {
                        migrateStore("", storeName, description != null ? description : "", 
                                   ownerId != null ? ownerId : "", ownerEmail != null ? ownerEmail : "", 
                                   contactPhone != null ? contactPhone : "", address != null ? address : "");
                        migrated++;
                    }
                } catch (Exception e) {
                    System.err.println("Error migrating store: " + e.getMessage());
                }
            }
            
            return String.format("Migrated %d stores\n", migrated);
        } catch (Exception e) {
            return "Error migrating stores: " + e.getMessage() + "\n";
        }
    }
    
    private String migrateWishlistsFromFile(ObjectMapper objectMapper, File exportsDir) {
        try {
            File wishlistsFile = new File(exportsDir, "wishlists.json");
            if (!wishlistsFile.exists()) {
                return "Wishlists file not found, skipping...\n";
            }
            
            List<Map<String, Object>> wishlists = objectMapper.readValue(wishlistsFile, List.class);
            int migrated = 0;
            
            for (Map<String, Object> wishlistData : wishlists) {
                try {
                    String userId = (String) wishlistData.get("userId");
                    Integer totalItems = wishlistData.get("totalItems") != null ? Integer.valueOf(wishlistData.get("totalItems").toString()) : 0;
                    
                    if (userId != null) {
                        migrateWishlist("", userId, totalItems);
                        migrated++;
                    }
                } catch (Exception e) {
                    System.err.println("Error migrating wishlist: " + e.getMessage());
                }
            }
            
            return String.format("Migrated %d wishlists\n", migrated);
        } catch (Exception e) {
            return "Error migrating wishlists: " + e.getMessage() + "\n";
        }
    }
    
    private String migrateWishlistItemsFromFile(ObjectMapper objectMapper, File exportsDir) {
        try {
            File wishlistItemsFile = new File(exportsDir, "wishlist_items.json");
            if (!wishlistItemsFile.exists()) {
                return "Wishlist items file not found, skipping...\n";
            }
            
            List<Map<String, Object>> wishlistItems = objectMapper.readValue(wishlistItemsFile, List.class);
            int migrated = 0;
            
            for (Map<String, Object> itemData : wishlistItems) {
                try {
                    String wishlistId = (String) itemData.get("wishlistId");
                    String productId = (String) itemData.get("productId");
                    String productName = (String) itemData.get("productName");
                    Double productPrice = itemData.get("productPrice") != null ? Double.valueOf(itemData.get("productPrice").toString()) : 0.0;
                    String productImage = (String) itemData.get("productImage");
                    Integer quantity = itemData.get("quantity") != null ? Integer.valueOf(itemData.get("quantity").toString()) : 1;
                    
                    if (wishlistId != null && productId != null) {
                        migrateWishlistItem(wishlistId, productId, productName != null ? productName : "", 
                                          productPrice, productImage != null ? productImage : "", quantity);
                        migrated++;
                    }
                } catch (Exception e) {
                    System.err.println("Error migrating wishlist item: " + e.getMessage());
                }
            }
            
            return String.format("Migrated %d wishlist items\n", migrated);
        } catch (Exception e) {
            return "Error migrating wishlist items: " + e.getMessage() + "\n";
        }
    }
    
    private String migrateCartsFromFile(ObjectMapper objectMapper, File exportsDir) {
        try {
            File cartsFile = new File(exportsDir, "carts.json");
            if (!cartsFile.exists()) {
                return "Carts file not found, skipping...\n";
            }
            
            List<Map<String, Object>> carts = objectMapper.readValue(cartsFile, List.class);
            int migrated = 0;
            
            for (Map<String, Object> cartData : carts) {
                try {
                    String userId = (String) cartData.get("userId");
                    Integer totalItems = cartData.get("totalItems") != null ? Integer.valueOf(cartData.get("totalItems").toString()) : 0;
                    Double totalAmount = cartData.get("totalAmount") != null ? Double.valueOf(cartData.get("totalAmount").toString()) : 0.0;
                    
                    if (userId != null) {
                        migrateCart("", userId, totalItems, totalAmount);
                        migrated++;
                    }
                } catch (Exception e) {
                    System.err.println("Error migrating cart: " + e.getMessage());
                }
            }
            
            return String.format("Migrated %d carts\n", migrated);
        } catch (Exception e) {
            return "Error migrating carts: " + e.getMessage() + "\n";
        }
    }
    
    
    private String migratePaymentTransactionsFromFile(ObjectMapper objectMapper, File exportsDir) {
        try {
            File transactionsFile = new File(exportsDir, "payment_transactions.json");
            if (!transactionsFile.exists()) {
                return "Payment transactions file not found, skipping...\n";
            }
            
            List<Map<String, Object>> transactions = objectMapper.readValue(transactionsFile, List.class);
            int migrated = 0;
            
            for (Map<String, Object> transactionData : transactions) {
                try {
                    String orderId = (String) transactionData.get("orderId");
                    String userId = (String) transactionData.get("userId");
                    Double amount = transactionData.get("amount") != null ? Double.valueOf(transactionData.get("amount").toString()) : 0.0;
                    String paymentMethod = (String) transactionData.get("paymentMethod");
                    String status = (String) transactionData.get("status");
                    String gatewayTransactionId = (String) transactionData.get("gatewayTransactionId");
                    
                    if (orderId != null && userId != null) {
                        migratePaymentTransaction("", orderId, userId, amount, 
                                                paymentMethod != null ? paymentMethod : "", 
                                                status != null ? status : "", 
                                                gatewayTransactionId != null ? gatewayTransactionId : "");
                        migrated++;
                    }
                } catch (Exception e) {
                    System.err.println("Error migrating payment transaction: " + e.getMessage());
                }
            }
            
            return String.format("Migrated %d payment transactions\n", migrated);
        } catch (Exception e) {
            return "Error migrating payment transactions: " + e.getMessage() + "\n";
        }
    }
    
    private String migrateUserOrdersFromFile(ObjectMapper objectMapper, File exportsDir) {
        try {
            File userOrdersFile = new File(exportsDir, "user_orders.json");
            if (!userOrdersFile.exists()) {
                return "User orders file not found, skipping...\n";
            }
            
            List<Map<String, Object>> userOrders = objectMapper.readValue(userOrdersFile, List.class);
            int migrated = 0;
            
            for (Map<String, Object> orderData : userOrders) {
                try {
                    String userId = (String) orderData.get("userId");
                    String orderId = (String) orderData.get("orderId");
                    String productId = (String) orderData.get("productId");
                    String productName = (String) orderData.get("productName");
                    Double productPrice = orderData.get("productPrice") != null ? Double.valueOf(orderData.get("productPrice").toString()) : 0.0;
                    Integer quantity = orderData.get("quantity") != null ? Integer.valueOf(orderData.get("quantity").toString()) : 1;
                    String storeId = (String) orderData.get("storeId");
                    String storeName = (String) orderData.get("storeName");
                    
                    if (userId != null && orderId != null && productId != null) {
                        migrateUserOrder("", userId, orderId, productId, 
                                       productName != null ? productName : "", productPrice, quantity, 
                                       storeId != null ? storeId : "", storeName != null ? storeName : "");
                        migrated++;
                    }
                } catch (Exception e) {
                    System.err.println("Error migrating user order: " + e.getMessage());
                }
            }
            
            return String.format("Migrated %d user orders\n", migrated);
        } catch (Exception e) {
            return "Error migrating user orders: " + e.getMessage() + "\n";
        }
    }
    
    private String migrateUserSettingsFromFile(ObjectMapper objectMapper, File exportsDir) {
        try {
            File userSettingsFile = new File(exportsDir, "user_settings.json");
            if (!userSettingsFile.exists()) {
                return "User settings file not found, skipping...\n";
            }
            
            List<Map<String, Object>> userSettings = objectMapper.readValue(userSettingsFile, List.class);
            int migrated = 0;
            
            for (Map<String, Object> settingsData : userSettings) {
                try {
                    String userId = (String) settingsData.get("userId");
                    
                    if (userId != null) {
                        createDefaultUserSettings(userId);
                        migrated++;
                    }
                } catch (Exception e) {
                    System.err.println("Error migrating user settings: " + e.getMessage());
                }
            }
            
            return String.format("Migrated %d user settings\n", migrated);
        } catch (Exception e) {
            return "Error migrating user settings: " + e.getMessage() + "\n";
        }
    }

    /**
     * Get migration statistics
     */
    public String getMigrationStats() {
        return String.format(
            "Migration Statistics:\n" +
            "Users: %d\n" +
            "Products: %d\n" +
            "Orders: %d\n" +
            "Wishlists: %d\n" +
            "Wishlist Items: %d\n" +
            "Carts: %d\n" +
            "Stores: %d\n" +
            "Payment Transactions: %d\n" +
            "User Orders: %d\n" +
            "User Settings: %d",
            userRepository.count(),
            productRepository.count(),
            orderRepository.count(),
            wishlistRepository.count(),
            wishlistItemRepository.count(),
            cartRepository.count(),
            storeRepository.count(),
            paymentTransactionRepository.count(),
            userOrderRepository.count(),
            userSettingsRepository.count()
        );
    }
}
