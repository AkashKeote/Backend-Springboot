package com.ecobazaar.backend.repository;

import com.ecobazaar.backend.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByWishlistId(String wishlistId);
    Optional<Wishlist> findByUserId(String userId);
    boolean existsByWishlistId(String wishlistId);
    boolean existsByUserId(String userId);
}
