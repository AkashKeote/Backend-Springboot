package com.ecobazaar.backend.repository;

import com.ecobazaar.backend.entity.CarbonFootprintRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CarbonFootprintRecordRepository extends JpaRepository<CarbonFootprintRecord, Long> {
    
    // Find by user
    List<CarbonFootprintRecord> findByUserIdOrderByCalculatedAtDesc(String userId);
    
    // Find by order
    List<CarbonFootprintRecord> findByOrderId(String orderId);
    
    // Find by product
    List<CarbonFootprintRecord> findByProductId(String productId);
    
    // Get user's total carbon footprint
    @Query("SELECT COALESCE(SUM(c.totalCarbonFootprint), 0.0) FROM CarbonFootprintRecord c WHERE c.userId = :userId")
    Double getTotalCarbonFootprintByUser(@Param("userId") String userId);
    
    // Get user's total carbon savings
    @Query("SELECT COALESCE(SUM(c.carbonSavings), 0.0) FROM CarbonFootprintRecord c WHERE c.userId = :userId")
    Double getTotalCarbonSavingsByUser(@Param("userId") String userId);
    
    // Get user's records by date range
    @Query("SELECT c FROM CarbonFootprintRecord c WHERE c.userId = :userId " +
           "AND c.calculatedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY c.calculatedAt DESC")
    List<CarbonFootprintRecord> findByUserAndDateRange(
        @Param("userId") String userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    // Get carbon footprint by category
    @Query("SELECT c.category, SUM(c.totalCarbonFootprint) FROM CarbonFootprintRecord c " +
           "WHERE c.userId = :userId GROUP BY c.category ORDER BY SUM(c.totalCarbonFootprint) DESC")
    List<Object[]> getCarbonFootprintByCategory(@Param("userId") String userId);
    
    // Get average eco rating by user
    @Query("SELECT c.ecoRating, COUNT(c) FROM CarbonFootprintRecord c " +
           "WHERE c.userId = :userId GROUP BY c.ecoRating ORDER BY c.ecoRating")
    List<Object[]> getEcoRatingDistribution(@Param("userId") String userId);
    
    // Get records by eco rating
    List<CarbonFootprintRecord> findByUserIdAndEcoRating(String userId, String ecoRating);
    
    // Get top eco-friendly purchases
    @Query("SELECT c FROM CarbonFootprintRecord c WHERE c.userId = :userId " +
           "AND c.carbonSavings > 0 ORDER BY c.carbonSavings DESC")
    List<CarbonFootprintRecord> findTopEcoFriendlyPurchases(@Param("userId") String userId);
    
    // Get records with highest carbon footprint
    @Query("SELECT c FROM CarbonFootprintRecord c WHERE c.userId = :userId " +
           "ORDER BY c.totalCarbonFootprint DESC")
    List<CarbonFootprintRecord> findHighestCarbonFootprintRecords(@Param("userId") String userId);
    
    // Count records by user
    Long countByUserId(String userId);
    
    // Get user's environmental equivalents totals
    @Query("SELECT " +
           "COALESCE(SUM(c.treesEquivalent), 0.0), " +
           "COALESCE(SUM(c.carKmEquivalent), 0.0), " +
           "COALESCE(SUM(c.electricityKwhEquivalent), 0.0), " +
           "COALESCE(SUM(c.plasticBottlesEquivalent), 0.0) " +
           "FROM CarbonFootprintRecord c WHERE c.userId = :userId")
    Object[] getTotalEnvironmentalEquivalents(@Param("userId") String userId);
    
    // Get monthly carbon footprint
    @Query("SELECT MONTH(c.calculatedAt), YEAR(c.calculatedAt), SUM(c.totalCarbonFootprint) " +
           "FROM CarbonFootprintRecord c WHERE c.userId = :userId " +
           "GROUP BY YEAR(c.calculatedAt), MONTH(c.calculatedAt) " +
           "ORDER BY YEAR(c.calculatedAt) DESC, MONTH(c.calculatedAt) DESC")
    List<Object[]> getMonthlyCarbonFootprint(@Param("userId") String userId);
    
    // Get records by material type
    @Query("SELECT c FROM CarbonFootprintRecord c WHERE c.userId = :userId " +
           "AND c.materialType = :materialType ORDER BY c.calculatedAt DESC")
    List<CarbonFootprintRecord> findByUserAndMaterialType(
        @Param("userId") String userId,
        @Param("materialType") String materialType
    );
    
    // Get records by transportation type
    @Query("SELECT c.transportationType, COUNT(c), AVG(c.transportationEmissions) " +
           "FROM CarbonFootprintRecord c WHERE c.userId = :userId " +
           "GROUP BY c.transportationType")
    List<Object[]> getTransportationStatistics(@Param("userId") String userId);
    
    // Get global statistics
    @Query("SELECT " +
           "COUNT(c), " +
           "COALESCE(SUM(c.totalCarbonFootprint), 0.0), " +
           "COALESCE(SUM(c.carbonSavings), 0.0), " +
           "COALESCE(AVG(c.totalCarbonFootprint), 0.0) " +
           "FROM CarbonFootprintRecord c")
    Object[] getGlobalStatistics();
    
    // Get recent records
    @Query("SELECT c FROM CarbonFootprintRecord c WHERE c.userId = :userId " +
           "ORDER BY c.calculatedAt DESC")
    List<CarbonFootprintRecord> findRecentByUser(@Param("userId") String userId);
    
    // Find by category
    List<CarbonFootprintRecord> findByCategory(String category);
}
