package com.ecobazaar.backend.repository;

import com.ecobazaar.backend.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByUserId(String userId);
    boolean existsByUserId(String userId);
}
