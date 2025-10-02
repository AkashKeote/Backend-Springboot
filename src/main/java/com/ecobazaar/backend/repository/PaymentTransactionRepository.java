package com.ecobazaar.backend.repository;

import com.ecobazaar.backend.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {
    Optional<PaymentTransaction> findByTransactionId(String transactionId);
    List<PaymentTransaction> findByOrderId(String orderId);
    List<PaymentTransaction> findByUserId(String userId);
    List<PaymentTransaction> findByStatus(String status);
    boolean existsByTransactionId(String transactionId);
}
