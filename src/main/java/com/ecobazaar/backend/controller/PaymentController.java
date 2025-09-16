package com.ecobazaar.backend.controller;

import com.ecobazaar.backend.entity.PaymentTransaction;
import com.ecobazaar.backend.repository.PaymentTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = {"*", "https://ecobazzarx.web.app", "http://localhost:62804", "http://127.0.0.1:62804"})
public class PaymentController {

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    // Process payment and create payment transaction
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processPayment(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("💳 Processing payment: " + request);
            
            // Extract payment details
            String orderId = (String) request.get("orderId");
            String userId = (String) request.get("userId");
            String paymentMethod = (String) request.get("paymentMethod");
            String paymentGateway = (String) request.get("paymentGateway");
            Double amount = Double.valueOf(request.get("amount").toString());
            @SuppressWarnings("unchecked")
            Map<String, Object> gatewayResponse = (Map<String, Object>) request.get("gatewayResponse");
            String failureReason = (String) request.get("failureReason");
            
            // Generate transaction ID
            String transactionId = "TXN_" + System.currentTimeMillis() + "_" + Math.abs(orderId.hashCode() % 1000);
            
            // Create payment transaction
            PaymentTransaction transaction = new PaymentTransaction();
            transaction.setTransactionId(transactionId);
            transaction.setOrderId(orderId);
            transaction.setUserId(userId);
            transaction.setAmount(amount);
            transaction.setPaymentMethod(paymentMethod);
            transaction.setPaymentGateway(paymentGateway != null ? paymentGateway : "razorpay");
            
            // Determine payment status based on gateway response
            Boolean isSuccess = gatewayResponse != null && (Boolean) gatewayResponse.get("success");
            transaction.setStatus(isSuccess ? "COMPLETED" : "FAILED");
            
            if (failureReason != null && !isSuccess) {
                transaction.setFailureReason(failureReason);
            }
            
            // Set gateway response as JSON string
            if (gatewayResponse != null) {
                transaction.setGatewayResponse(gatewayResponse.toString());
            }
            
            // Save payment transaction
            PaymentTransaction savedTransaction = paymentTransactionRepository.save(transaction);
            
            System.out.println("💳 Payment transaction saved: " + savedTransaction.getId());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "paymentId", transactionId,
                "transactionId", savedTransaction.getId(),
                "message", "Payment processed successfully",
                "status", savedTransaction.getStatus()
            ));
            
        } catch (Exception e) {
            System.err.println("💳 Error processing payment: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "message", "Error processing payment: " + e.getMessage()
            ));
        }
    }

    // Get payment by transaction ID
    @GetMapping("/{transactionId}")
    public ResponseEntity<PaymentTransaction> getPaymentByTransactionId(@PathVariable String transactionId) {
        try {
            Optional<PaymentTransaction> payment = paymentTransactionRepository.findByTransactionId(transactionId);
            if (payment.isPresent()) {
                return ResponseEntity.ok(payment.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // Get payments by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentTransaction>> getPaymentsByUser(@PathVariable String userId) {
        try {
            List<PaymentTransaction> payments = paymentTransactionRepository.findByUserId(userId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Get payments by order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<PaymentTransaction>> getPaymentsByOrder(@PathVariable String orderId) {
        try {
            List<PaymentTransaction> payments = paymentTransactionRepository.findByOrderId(orderId);
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Get all payments (admin)
    @GetMapping("/admin/all")
    public ResponseEntity<List<PaymentTransaction>> getAllPayments() {
        try {
            List<PaymentTransaction> payments = paymentTransactionRepository.findAll();
            return ResponseEntity.ok(payments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(List.of());
        }
    }

    // Get payment statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getPaymentStats() {
        try {
            List<PaymentTransaction> allPayments = paymentTransactionRepository.findAll();
            List<PaymentTransaction> completedPayments = paymentTransactionRepository.findByStatus("COMPLETED");
            List<PaymentTransaction> failedPayments = paymentTransactionRepository.findByStatus("FAILED");
            
            double totalAmount = completedPayments.stream()
                    .mapToDouble(PaymentTransaction::getAmount)
                    .sum();
            
            return ResponseEntity.ok(Map.of(
                "totalPayments", allPayments.size(),
                "completedPayments", completedPayments.size(),
                "failedPayments", failedPayments.size(),
                "totalAmount", totalAmount
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                "totalPayments", 0,
                "completedPayments", 0,
                "failedPayments", 0,
                "totalAmount", 0.0
            ));
        }
    }
}
