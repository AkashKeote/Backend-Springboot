package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.entity.UserOrder;
import com.ecobazaar.backend.service.UserOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/userorders")
@CrossOrigin(origins = "*")
public class UserOrderController {

    @Autowired
    private UserOrderService userOrderService;

    // Create new user order (order item)
    @PostMapping
    public ResponseEntity<Map<String, Object>> createUserOrder(@RequestBody UserOrder userOrder) {
        try {
            UserOrder savedUserOrder = userOrderService.createUserOrder(userOrder);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userOrder", savedUserOrder);
            response.put("message", "User order created successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error creating user order: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Create multiple user orders (bulk order items)
    @PostMapping("/bulk")
    public ResponseEntity<Map<String, Object>> createMultipleUserOrders(@RequestBody List<UserOrder> userOrders) {
        try {
            List<UserOrder> savedUserOrders = userOrderService.createMultipleUserOrders(userOrders);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userOrders", savedUserOrders);
            response.put("count", savedUserOrders.size());
            response.put("message", "User orders created successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error creating user orders: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Get all user orders
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUserOrders() {
        try {
            List<UserOrder> userOrders = userOrderService.getAllUserOrders();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userOrders", userOrders);
            response.put("count", userOrders.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error fetching user orders: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Get user order by ID
    @GetMapping("/{userOrderId}")
    public ResponseEntity<Map<String, Object>> getUserOrderById(@PathVariable Long userOrderId) {
        try {
            Optional<UserOrder> userOrder = userOrderService.getUserOrderById(userOrderId);
            Map<String, Object> response = new HashMap<>();
            if (userOrder.isPresent()) {
                response.put("success", true);
                response.put("userOrder", userOrder.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "User order not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error fetching user order: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Get user order by user order ID
    @GetMapping("/orderid/{userOrderId}")
    public ResponseEntity<Map<String, Object>> getUserOrderByUserOrderId(@PathVariable String userOrderId) {
        try {
            Optional<UserOrder> userOrder = userOrderService.getUserOrderByUserOrderId(userOrderId);
            Map<String, Object> response = new HashMap<>();
            if (userOrder.isPresent()) {
                response.put("success", true);
                response.put("userOrder", userOrder.get());
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "User order not found with ID: " + userOrderId);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error fetching user order: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Get user orders by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserOrdersByUserId(@PathVariable String userId) {
        try {
            List<UserOrder> userOrders = userOrderService.getUserOrdersByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userOrders", userOrders);
            response.put("count", userOrders.size());
            response.put("userId", userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error fetching user orders: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Get user orders by order ID (all items in a specific order)
    @GetMapping("/order/{orderId}")
    public ResponseEntity<Map<String, Object>> getUserOrdersByOrderId(@PathVariable String orderId) {
        try {
            List<UserOrder> userOrders = userOrderService.getUserOrdersByOrderId(orderId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userOrders", userOrders);
            response.put("count", userOrders.size());
            response.put("orderId", orderId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error fetching order items: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Get user orders by product ID
    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> getUserOrdersByProductId(@PathVariable String productId) {
        try {
            List<UserOrder> userOrders = userOrderService.getUserOrdersByProductId(productId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userOrders", userOrders);
            response.put("count", userOrders.size());
            response.put("productId", productId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error fetching orders by product: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Get user orders by store ID
    @GetMapping("/store/{storeId}")
    public ResponseEntity<Map<String, Object>> getUserOrdersByStoreId(@PathVariable String storeId) {
        try {
            List<UserOrder> userOrders = userOrderService.getUserOrdersByStoreId(storeId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userOrders", userOrders);
            response.put("count", userOrders.size());
            response.put("storeId", storeId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error fetching orders by store: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Get user orders by status
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getUserOrdersByStatus(@PathVariable String status) {
        try {
            List<UserOrder> userOrders = userOrderService.getUserOrdersByStatus(status);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userOrders", userOrders);
            response.put("count", userOrders.size());
            response.put("status", status);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error fetching orders by status: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Update user order status
    @PutMapping("/{userOrderId}/status")
    public ResponseEntity<Map<String, Object>> updateUserOrderStatus(
            @PathVariable String userOrderId, 
            @RequestBody Map<String, String> request) {
        try {
            String newStatus = request.get("status");
            UserOrder updatedUserOrder = userOrderService.updateUserOrderStatus(userOrderId, newStatus);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userOrder", updatedUserOrder);
            response.put("message", "User order status updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error updating user order status: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Update user order quantity
    @PutMapping("/{userOrderId}/quantity")
    public ResponseEntity<Map<String, Object>> updateUserOrderQuantity(
            @PathVariable String userOrderId, 
            @RequestBody Map<String, Integer> request) {
        try {
            Integer newQuantity = request.get("quantity");
            UserOrder updatedUserOrder = userOrderService.updateUserOrderQuantity(userOrderId, newQuantity);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userOrder", updatedUserOrder);
            response.put("message", "User order quantity updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error updating user order quantity: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Update delivery date
    @PutMapping("/{userOrderId}/delivery")
    public ResponseEntity<Map<String, Object>> updateDeliveryDate(
            @PathVariable String userOrderId, 
            @RequestBody Map<String, String> request) {
        try {
            String deliveryDateStr = request.get("deliveryDate");
            LocalDateTime deliveryDate = LocalDateTime.parse(deliveryDateStr);
            UserOrder updatedUserOrder = userOrderService.updateDeliveryDate(userOrderId, deliveryDate);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userOrder", updatedUserOrder);
            response.put("message", "Delivery date updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error updating delivery date: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Delete user order
    @DeleteMapping("/{userOrderId}")
    public ResponseEntity<Map<String, Object>> deleteUserOrder(@PathVariable String userOrderId) {
        try {
            userOrderService.deleteUserOrder(userOrderId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User order deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error deleting user order: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
