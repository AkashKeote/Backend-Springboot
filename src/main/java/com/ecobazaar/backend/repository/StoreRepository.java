package com.ecobazaar.backend.repository;

import com.ecobazaar.backend.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Optional<Store> findByStoreId(String storeId);
    List<Store> findByOwnerId(String ownerId);
    List<Store> findByIsActiveTrue();
    List<Store> findByIsVerifiedTrue();
    List<Store> findByCity(String city);
    List<Store> findByState(String state);
    List<Store> findByStoreNameContainingIgnoreCase(String name);
    boolean existsByStoreId(String storeId);
    boolean existsByOwnerId(String ownerId);
}
