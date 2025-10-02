package com.ecobazaar.backend.repository;

import com.ecobazaar.backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCartId(String cartId);
    Optional<Cart> findByUserId(String userId);
    boolean existsByCartId(String cartId);
    boolean existsByUserId(String userId);
}
