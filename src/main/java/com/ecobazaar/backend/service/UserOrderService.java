package com.ecobazaar.backend.service;

import com.ecobazaar.backend.entity.UserOrder;
import com.ecobazaar.backend.repository.UserOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserOrderService {

    @Autowired
    private UserOrderRepository userOrderRepository;

    // Create new user order (order item)
    public UserOrder createUserOrder(UserOrder userOrder) {
        try {
            // Generate unique user order ID if not provided
            if (userOrder.getUserOrderId() == null || userOrder.getUserOrderId().isEmpty()) {
                userOrder.setUserOrderId("UO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }
            
            // Set default values if not provided
            if (userOrder.getQuantity() == null || userOrder.getQuantity() <= 0) {
                userOrder.setQuantity(1);
            }
            
            // Calculate total amount if not provided
            if (userOrder.getTotalAmount() == null) {
                if (userOrder.getProductPrice() != null && userOrder.getQuantity() != null) {
                    userOrder.setTotalAmount(userOrder.getProductPrice() * userOrder.getQuantity());
                } else {
                    userOrder.setTotalAmount(0.0);
                }
            }
            
            // Set default order status
            if (userOrder.getOrderStatus() == null || userOrder.getOrderStatus().isEmpty()) {
                userOrder.setOrderStatus("PENDING");
            }
            
            return userOrderRepository.save(userOrder);
        } catch (Exception e) {
            throw new RuntimeException("Error creating user order: " + e.getMessage());
        }
    }

    // Get all user orders
    public List<UserOrder> getAllUserOrders() {
        try {
            return userOrderRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user orders: " + e.getMessage());
        }
    }

    // Get user order by ID
    public Optional<UserOrder> getUserOrderById(Long id) {
        try {
            return userOrderRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user order: " + e.getMessage());
        }
    }

    // Get user order by user order ID
    public Optional<UserOrder> getUserOrderByUserOrderId(String userOrderId) {
        try {
            return userOrderRepository.findByUserOrderId(userOrderId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user order: " + e.getMessage());
        }
    }

    // Get user orders by user ID
    public List<UserOrder> getUserOrdersByUserId(String userId) {
        try {
            return userOrderRepository.findByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user orders: " + e.getMessage());
        }
    }

    // Get user orders by order ID (all items in a specific order)
    public List<UserOrder> getUserOrdersByOrderId(String orderId) {
        try {
            return userOrderRepository.findByOrderId(orderId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching order items: " + e.getMessage());
        }
    }

    // Get user orders by product ID
    public List<UserOrder> getUserOrdersByProductId(String productId) {
        try {
            return userOrderRepository.findByProductId(productId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching orders by product: " + e.getMessage());
        }
    }

    // Get user orders by store ID
    public List<UserOrder> getUserOrdersByStoreId(String storeId) {
        try {
            return userOrderRepository.findByStoreId(storeId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching orders by store: " + e.getMessage());
        }
    }

    // Get user orders by status
    public List<UserOrder> getUserOrdersByStatus(String status) {
        try {
            return userOrderRepository.findByOrderStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching orders by status: " + e.getMessage());
        }
    }

    // Update user order status
    public UserOrder updateUserOrderStatus(String userOrderId, String newStatus) {
        try {
            Optional<UserOrder> userOrderOpt = userOrderRepository.findByUserOrderId(userOrderId);
            if (userOrderOpt.isPresent()) {
                UserOrder userOrder = userOrderOpt.get();
                userOrder.setOrderStatus(newStatus);
                return userOrderRepository.save(userOrder);
            } else {
                throw new RuntimeException("User order not found with ID: " + userOrderId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating user order status: " + e.getMessage());
        }
    }

    // Update user order quantity and recalculate total
    public UserOrder updateUserOrderQuantity(String userOrderId, Integer newQuantity) {
        try {
            Optional<UserOrder> userOrderOpt = userOrderRepository.findByUserOrderId(userOrderId);
            if (userOrderOpt.isPresent()) {
                UserOrder userOrder = userOrderOpt.get();
                userOrder.setQuantity(newQuantity);
                
                // Recalculate total amount
                if (userOrder.getProductPrice() != null) {
                    userOrder.setTotalAmount(userOrder.getProductPrice() * newQuantity);
                }
                
                return userOrderRepository.save(userOrder);
            } else {
                throw new RuntimeException("User order not found with ID: " + userOrderId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating user order quantity: " + e.getMessage());
        }
    }

    // Update delivery date
    public UserOrder updateDeliveryDate(String userOrderId, java.time.LocalDateTime deliveryDate) {
        try {
            Optional<UserOrder> userOrderOpt = userOrderRepository.findByUserOrderId(userOrderId);
            if (userOrderOpt.isPresent()) {
                UserOrder userOrder = userOrderOpt.get();
                userOrder.setDeliveryDate(deliveryDate);
                return userOrderRepository.save(userOrder);
            } else {
                throw new RuntimeException("User order not found with ID: " + userOrderId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating delivery date: " + e.getMessage());
        }
    }

    // Delete user order
    public void deleteUserOrder(String userOrderId) {
        try {
            Optional<UserOrder> userOrderOpt = userOrderRepository.findByUserOrderId(userOrderId);
            if (userOrderOpt.isPresent()) {
                userOrderRepository.delete(userOrderOpt.get());
            } else {
                throw new RuntimeException("User order not found with ID: " + userOrderId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user order: " + e.getMessage());
        }
    }

    // Create multiple user orders (for bulk order items)
    public List<UserOrder> createMultipleUserOrders(List<UserOrder> userOrders) {
        try {
            for (UserOrder userOrder : userOrders) {
                // Generate unique ID for each
                if (userOrder.getUserOrderId() == null || userOrder.getUserOrderId().isEmpty()) {
                    userOrder.setUserOrderId("UO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
                }
                
                // Set defaults
                if (userOrder.getQuantity() == null || userOrder.getQuantity() <= 0) {
                    userOrder.setQuantity(1);
                }
                
                if (userOrder.getTotalAmount() == null && userOrder.getProductPrice() != null) {
                    userOrder.setTotalAmount(userOrder.getProductPrice() * userOrder.getQuantity());
                }
                
                if (userOrder.getOrderStatus() == null || userOrder.getOrderStatus().isEmpty()) {
                    userOrder.setOrderStatus("PENDING");
                }
            }
            
            return userOrderRepository.saveAll(userOrders);
        } catch (Exception e) {
            throw new RuntimeException("Error creating multiple user orders: " + e.getMessage());
        }
    }
}
