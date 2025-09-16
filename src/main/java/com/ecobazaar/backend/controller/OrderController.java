package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.entity.Order;
import com.ecobazaar.backend.entity.OrderStatus;
import com.ecobazaar.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    // Get all orders
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllOrders() {
        try {
            List<Order> orders = orderRepository.findAll();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orders);
            response.put("count", orders.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error fetching orders: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Create order
    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(@RequestBody Order order) {
        try {
            Order savedOrder = orderRepository.save(order);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("order", savedOrder);
            response.put("message", "Order created successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error creating order: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Get orders by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getOrdersByUser(@PathVariable String userId) {
        try {
            List<Order> orders = orderRepository.findByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orders);
            response.put("count", orders.size());
            response.put("userId", userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error fetching user orders: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Get orders by status
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getOrdersByStatus(@PathVariable String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = orderRepository.findByOrderStatus(orderStatus);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orders", orders);
            response.put("count", orders.size());
            response.put("status", status);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Invalid order status: " + status);
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error fetching orders by status: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    // Get order by ID
    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderById(@PathVariable String orderId) {
        try {
            Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
            Order order = orderOpt.orElse(null);
            Map<String, Object> response = new HashMap<>();
            if (order != null) {
                response.put("success", true);
                response.put("order", order);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "Order not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "Error fetching order: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
