package com.ecobazaar.backend.service;

import com.ecobazaar.backend.entity.Order;
import com.ecobazaar.backend.entity.OrderStatus;
import com.ecobazaar.backend.entity.PaymentStatus;
import com.ecobazaar.backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Create new order
    public Order createOrder(Order order) {
        try {
            // Generate unique order ID if not provided
            if (order.getOrderId() == null || order.getOrderId().isEmpty()) {
                order.setOrderId("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            }
            
            // Set default values if not provided
            if (order.getOrderStatus() == null) {
                order.setOrderStatus(OrderStatus.PENDING);
            }
            
            if (order.getPaymentStatus() == null) {
                order.setPaymentStatus(PaymentStatus.PENDING);
            }
            
            if (order.getCurrency() == null || order.getCurrency().isEmpty()) {
                order.setCurrency("INR");
            }
            
            // Calculate amounts if not provided
            if (order.getTotalAmount() == null) {
                order.setTotalAmount(0.0);
            }
            
            if (order.getFinalAmount() == null) {
                order.setFinalAmount(order.getTotalAmount());
            }
            
            if (order.getTaxAmount() == null) {
                order.setTaxAmount(0.0);
            }
            
            if (order.getShippingAmount() == null) {
                order.setShippingAmount(0.0);
            }
            
            if (order.getDiscountAmount() == null) {
                order.setDiscountAmount(0.0);
            }
            
            return orderRepository.save(order);
        } catch (Exception e) {
            throw new RuntimeException("Error creating order: " + e.getMessage());
        }
    }

    // Get all orders
    public List<Order> getAllOrders() {
        try {
            return orderRepository.findAllOrderByCreatedAtDesc();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching orders: " + e.getMessage());
        }
    }

    // Get order by ID
    public Optional<Order> getOrderById(Long id) {
        try {
            return orderRepository.findById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching order: " + e.getMessage());
        }
    }

    // Get order by order ID
    public Optional<Order> getOrderByOrderId(String orderId) {
        try {
            return orderRepository.findByOrderId(orderId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching order: " + e.getMessage());
        }
    }

    // Get orders by user ID
    public List<Order> getOrdersByUserId(String userId) {
        try {
            return orderRepository.findByUserId(userId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user orders: " + e.getMessage());
        }
    }

    // Get orders by status
    public List<Order> getOrdersByStatus(OrderStatus status) {
        try {
            return orderRepository.findByOrderStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching orders by status: " + e.getMessage());
        }
    }

    // Update order status
    public Order updateOrderStatus(String orderId, OrderStatus newStatus) {
        try {
            Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                order.setOrderStatus(newStatus);
                return orderRepository.save(order);
            } else {
                throw new RuntimeException("Order not found with ID: " + orderId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating order status: " + e.getMessage());
        }
    }

    // Update payment status
    public Order updatePaymentStatus(String orderId, PaymentStatus newPaymentStatus, String paymentId) {
        try {
            Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                order.setPaymentStatus(newPaymentStatus);
                if (paymentId != null && !paymentId.isEmpty()) {
                    order.setPaymentId(paymentId);
                }
                return orderRepository.save(order);
            } else {
                throw new RuntimeException("Order not found with ID: " + orderId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating payment status: " + e.getMessage());
        }
    }

    // Get orders by date range
    public List<Order> getOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            return orderRepository.findOrdersByDateRange(startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching orders by date range: " + e.getMessage());
        }
    }

    // Get order count by status
    public long getOrderCountByStatus(OrderStatus status) {
        try {
            return orderRepository.countByOrderStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Error counting orders by status: " + e.getMessage());
        }
    }

    // Get total revenue by date range
    public Double getTotalRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        try {
            Double revenue = orderRepository.getTotalRevenueByDateRange(startDate, endDate);
            return revenue != null ? revenue : 0.0;
        } catch (Exception e) {
            throw new RuntimeException("Error calculating total revenue: " + e.getMessage());
        }
    }

    // Delete order
    public void deleteOrder(String orderId) {
        try {
            Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
            if (orderOpt.isPresent()) {
                orderRepository.delete(orderOpt.get());
            } else {
                throw new RuntimeException("Order not found with ID: " + orderId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting order: " + e.getMessage());
        }
    }
}
