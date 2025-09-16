package com.ecobazaar.backend.repository;

import com.ecobazaar.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStoreId(String storeId);
    List<Product> findByCategory(String category);
    List<Product> findByIsActiveTrue();
    List<Product> findByIsActiveFalse();
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Product> findByQuantityGreaterThan(Integer quantity);
    List<Product> findByQuantityLessThanEqual(Integer quantity);
    
    @Query("SELECT p FROM Product p WHERE p.storeId = :storeId AND p.isActive = true")
    List<Product> findActiveProductsByStoreId(@Param("storeId") String storeId);
    
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.isActive = true")
    List<String> findDistinctCategories();
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.storeId = :storeId")
    Long countByStoreId(@Param("storeId") String storeId);
}

