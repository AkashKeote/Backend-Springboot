package com.ecobazaarx.backend.repository;

import com.ecobazaarx.backend.entity.EmissionFactor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmissionFactorRepository extends JpaRepository<EmissionFactor, Long> {
    
    // Find by category
    List<EmissionFactor> findByCategory(String category);
    
    // Find by category and subcategory
    List<EmissionFactor> findByCategoryAndSubcategory(String category, String subcategory);
    
    // Find by material type (exact match)
    Optional<EmissionFactor> findByMaterialTypeAndIsActive(String materialType, Boolean isActive);
    
    // Find by category, subcategory, and material type
    @Query("SELECT e FROM EmissionFactor e WHERE e.category = :category " +
           "AND e.subcategory = :subcategory AND e.materialType = :materialType " +
           "AND e.isActive = true")
    Optional<EmissionFactor> findByCategorySubcategoryAndMaterial(
        @Param("category") String category,
        @Param("subcategory") String subcategory,
        @Param("materialType") String materialType
    );
    
    // Get all active emission factors
    List<EmissionFactor> findByIsActive(Boolean isActive);
    
    // Get all factors by data source
    List<EmissionFactor> findByDataSource(String dataSource);
    
    // Get factors by confidence level
    List<EmissionFactor> findByConfidenceLevel(String confidenceLevel);
    
    // Search by name
    @Query("SELECT e FROM EmissionFactor e WHERE LOWER(e.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "AND e.isActive = true")
    List<EmissionFactor> searchByName(@Param("keyword") String keyword);
    
    // Get all materials with carbon sequestration (negative emissions)
    @Query("SELECT e FROM EmissionFactor e WHERE e.carbonSequestration < 0 " +
           "AND e.isActive = true ORDER BY e.carbonSequestration ASC")
    List<EmissionFactor> findCarbonSequesteringMaterials();
    
    // Get materials by category ordered by emission factor
    @Query("SELECT e FROM EmissionFactor e WHERE e.category = :category " +
           "AND e.isActive = true ORDER BY e.emissionFactor ASC")
    List<EmissionFactor> findByCategoryOrderedByEmissions(@Param("category") String category);
    
    // Get transportation factors ordered by emissions
    @Query("SELECT e FROM EmissionFactor e WHERE e.category = 'TRANSPORTATION' " +
           "AND e.isActive = true ORDER BY e.emissionFactor ASC")
    List<EmissionFactor> findTransportationFactorsOrderedByEmissions();
    
    // Get manufacturing factors by type
    @Query("SELECT e FROM EmissionFactor e WHERE e.category = 'MANUFACTURING' " +
           "AND e.subcategory = :subcategory AND e.isActive = true")
    List<EmissionFactor> findManufacturingFactorsByType(@Param("subcategory") String subcategory);
    
    // Get packaging factors
    @Query("SELECT e FROM EmissionFactor e WHERE e.category = 'PACKAGING' " +
           "AND e.isActive = true ORDER BY e.emissionFactor ASC")
    List<EmissionFactor> findPackagingFactors();
    
    // Count by category
    @Query("SELECT COUNT(e) FROM EmissionFactor e WHERE e.category = :category AND e.isActive = true")
    Long countByCategory(@Param("category") String category);
    
    // Get all categories
    @Query("SELECT DISTINCT e.category FROM EmissionFactor e WHERE e.isActive = true")
    List<String> findAllCategories();
    
    // Get all subcategories by category
    @Query("SELECT DISTINCT e.subcategory FROM EmissionFactor e WHERE e.category = :category AND e.isActive = true")
    List<String> findSubcategoriesByCategory(@Param("category") String category);
    
    // Get factors with recycling benefits
    @Query("SELECT e FROM EmissionFactor e WHERE e.recyclingBenefit > 0 " +
           "AND e.isActive = true ORDER BY e.recyclingBenefit DESC")
    List<EmissionFactor> findMaterialsWithRecyclingBenefits();
    
    // Get biodegradable materials
    @Query("SELECT e FROM EmissionFactor e WHERE e.biodegradationRate > 0 " +
           "AND e.isActive = true ORDER BY e.biodegradationRate DESC")
    List<EmissionFactor> findBiodegradableMaterials();
}
