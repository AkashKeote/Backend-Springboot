package com.ecobazaar.backend.repository;

import com.ecobazaar.backend.entity.UserOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserOrderRepository extends JpaRepository<UserOrder, Long> {
    Optional<UserOrder> findByUserOrderId(String userOrderId);
    List<UserOrder> findByUserId(String userId);
    List<UserOrder> findByOrderId(String orderId);
    List<UserOrder> findByProductId(String productId);
    List<UserOrder> findByStoreId(String storeId);
    List<UserOrder> findByOrderStatus(String orderStatus);
    boolean existsByUserOrderId(String userOrderId);
}
