package com.ecobazaar.backend.service;

import com.ecobazaar.backend.entity.EcoDiscount;
import com.ecobazaar.backend.entity.UserDiscountUsage;
import com.ecobazaar.backend.repository.EcoDiscountRepository;
import com.ecobazaar.backend.repository.UserDiscountUsageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service class for managing Eco Discounts
 */
@Service
@Transactional
public class EcoDiscountService {

    @Autowired
    private EcoDiscountRepository ecoDiscountRepository;

    @Autowired
    private UserDiscountUsageRepository usageRepository;

    // Discount Management
    public EcoDiscount createDiscount(EcoDiscount discount) {
        // Set default values
        if (discount.getValidFrom() == null) {
            discount.setValidFrom(LocalDateTime.now());
        }
        if (discount.getValidUntil() == null) {
            discount.setValidUntil(LocalDateTime.now().plusDays(30)); // Default 30 days
        }
        
        return ecoDiscountRepository.save(discount);
    }

    public Optional<EcoDiscount> getDiscountById(Long id) {
        return ecoDiscountRepository.findById(id);
    }

    public Optional<EcoDiscount> getDiscountByCode(String discountCode) {
        return ecoDiscountRepository.findByDiscountCode(discountCode);
    }

    public List<EcoDiscount> getAllActiveDiscounts() {
        return ecoDiscountRepository.findByIsActiveTrue();
    }

    public List<EcoDiscount> getCurrentlyValidDiscounts() {
        return ecoDiscountRepository.findCurrentlyValidDiscounts(LocalDateTime.now());
    }

    public List<EcoDiscount> getAvailableDiscounts() {
        return ecoDiscountRepository.findAvailableDiscounts(LocalDateTime.now());
    }

    public List<EcoDiscount> getDiscountsByType(String discountType) {
        return ecoDiscountRepository.findByDiscountTypeAndIsActiveTrue(discountType);
    }

    public List<EcoDiscount> getDiscountsByCategory(String category) {
        return ecoDiscountRepository.findByApplicableCategoryAndIsActiveTrue(category);
    }

    public List<EcoDiscount> getDiscountsByStore(String storeId) {
        return ecoDiscountRepository.findByApplicableStoreIdAndIsActiveTrue(storeId);
    }

    public List<EcoDiscount> searchDiscounts(String keyword) {
        return ecoDiscountRepository.searchDiscounts(keyword);
    }

    public EcoDiscount updateDiscount(EcoDiscount discount) {
        return ecoDiscountRepository.save(discount);
    }

    public void deleteDiscount(Long id) {
        ecoDiscountRepository.deleteById(id);
    }

    public void deactivateDiscount(String discountCode) {
        ecoDiscountRepository.findByDiscountCode(discountCode).ifPresent(discount -> {
            discount.setIsActive(false);
            ecoDiscountRepository.save(discount);
        });
    }

    // Discount Application Logic
    public Map<String, Object> validateDiscount(String discountCode, String userId, Double orderAmount, 
                                               String category, String storeId, Integer userEcoPoints) {
        Map<String, Object> result = new HashMap<>();
        
        Optional<EcoDiscount> discountOpt = ecoDiscountRepository.findByDiscountCode(discountCode);
        if (discountOpt.isEmpty()) {
            result.put("valid", false);
            result.put("message", "Discount code not found");
            return result;
        }

        EcoDiscount discount = discountOpt.get();
        
        // Check if discount is active
        if (!discount.getIsActive()) {
            result.put("valid", false);
            result.put("message", "Discount code is not active");
            return result;
        }

        // Check validity period
        LocalDateTime now = LocalDateTime.now();
        if (discount.getValidFrom().isAfter(now) || discount.getValidUntil().isBefore(now)) {
            result.put("valid", false);
            result.put("message", "Discount code has expired or not yet valid");
            return result;
        }

        // Check usage limits
        if (discount.getUsageLimit() != null && discount.getCurrentUsageCount() >= discount.getUsageLimit()) {
            result.put("valid", false);
            result.put("message", "Discount usage limit exceeded");
            return result;
        }

        // Check user usage limit
        if (userId != null) {
            Long userUsageCount = usageRepository.countUserDiscountUsage(userId, discountCode);
            if (userUsageCount >= discount.getUserUsageLimit()) {
                result.put("valid", false);
                result.put("message", "You have already used this discount code");
                return result;
            }
        }

        // Check minimum order amount
        if (discount.getMinimumOrderAmount() != null && orderAmount < discount.getMinimumOrderAmount()) {
            result.put("valid", false);
            result.put("message", "Minimum order amount not met. Required: ₹" + discount.getMinimumOrderAmount());
            return result;
        }

        // Check category restriction
        if (discount.getApplicableCategory() != null && !discount.getApplicableCategory().equals(category)) {
            result.put("valid", false);
            result.put("message", "Discount not applicable for this category");
            return result;
        }

        // Check store restriction
        if (discount.getApplicableStoreId() != null && !discount.getApplicableStoreId().equals(storeId)) {
            result.put("valid", false);
            result.put("message", "Discount not applicable for this store");
            return result;
        }

        // Check eco points requirement
        if (discount.getRequiresEcoPoints() && (userEcoPoints == null || userEcoPoints < discount.getRequiredEcoPoints())) {
            result.put("valid", false);
            result.put("message", "Insufficient eco points. Required: " + discount.getRequiredEcoPoints());
            return result;
        }

        // Calculate discount amount
        Double discountAmount = calculateDiscountAmount(discount, orderAmount);
        
        result.put("valid", true);
        result.put("discount", discount);
        result.put("discountAmount", discountAmount);
        result.put("finalAmount", orderAmount - discountAmount);
        result.put("message", "Discount applied successfully");
        
        return result;
    }

    public Double calculateDiscountAmount(EcoDiscount discount, Double orderAmount) {
        Double discountAmount = 0.0;
        
        switch (discount.getDiscountType()) {
            case "PERCENTAGE":
                discountAmount = (orderAmount * discount.getDiscountValue()) / 100;
                break;
            case "FIXED_AMOUNT":
                discountAmount = discount.getDiscountValue();
                break;
            case "FREE_SHIPPING":
                // For free shipping, we could have a standard shipping amount
                discountAmount = 50.0; // Assuming ₹50 shipping
                break;
            default:
                discountAmount = 0.0;
        }

        // Apply maximum discount limit if specified
        if (discount.getMaximumDiscountAmount() != null && discountAmount > discount.getMaximumDiscountAmount()) {
            discountAmount = discount.getMaximumDiscountAmount();
        }

        // Ensure discount doesn't exceed order amount
        if (discountAmount > orderAmount) {
            discountAmount = orderAmount;
        }

        return discountAmount;
    }

    public UserDiscountUsage applyDiscount(String userId, String discountCode, String orderId, 
                                         Double orderAmount, Double discountAmount) {
        // Record the usage
        UserDiscountUsage usage = new UserDiscountUsage(userId, discountCode, orderId, discountAmount, orderAmount);
        usage = usageRepository.save(usage);

        // Update discount usage count
        ecoDiscountRepository.findByDiscountCode(discountCode).ifPresent(discount -> {
            discount.setCurrentUsageCount(discount.getCurrentUsageCount() + 1);
            ecoDiscountRepository.save(discount);
        });

        return usage;
    }

    // User-specific discount methods
    public List<EcoDiscount> getApplicableDiscountsForUser(String userId, Integer userEcoPoints, Double orderAmount) {
        List<EcoDiscount> allDiscounts = getCurrentlyValidDiscounts();
        List<EcoDiscount> applicableDiscounts = new ArrayList<>();

        for (EcoDiscount discount : allDiscounts) {
            Map<String, Object> validation = validateDiscount(
                discount.getDiscountCode(), userId, orderAmount, null, null, userEcoPoints
            );
            
            if ((Boolean) validation.get("valid")) {
                applicableDiscounts.add(discount);
            }
        }

        return applicableDiscounts;
    }

    // Analytics and Statistics
    public Map<String, Object> getUserDiscountStats(String userId) {
        Map<String, Object> stats = new HashMap<>();
        
        List<UserDiscountUsage> userUsages = usageRepository.findByUserId(userId);
        Double totalSaved = usageRepository.getTotalDiscountSavedByUser(userId);
        Long ordersWithDiscounts = usageRepository.countOrdersWithDiscountsByUser(userId);
        
        stats.put("totalDiscountsUsed", userUsages.size());
        stats.put("totalAmountSaved", totalSaved != null ? totalSaved : 0.0);
        stats.put("ordersWithDiscounts", ordersWithDiscounts != null ? ordersWithDiscounts : 0);
        
        return stats;
    }

    public Map<String, Object> getDiscountAnalytics(String discountCode) {
        Map<String, Object> analytics = new HashMap<>();
        
        Object[] stats = usageRepository.getDiscountUsageStats(discountCode);
        List<UserDiscountUsage> usages = usageRepository.findByDiscountCode(discountCode);
        
        if (stats != null && stats.length >= 3) {
            analytics.put("totalUsages", stats[0]);
            analytics.put("averageDiscountAmount", stats[1]);
            analytics.put("totalDiscountGiven", stats[2]);
        } else {
            analytics.put("totalUsages", 0);
            analytics.put("averageDiscountAmount", 0.0);
            analytics.put("totalDiscountGiven", 0.0);
        }
        
        analytics.put("uniqueUsers", usages.stream().map(UserDiscountUsage::getUserId).distinct().count());
        
        return analytics;
    }

    public List<Map<String, Object>> getMostPopularDiscounts() {
        List<Object[]> results = usageRepository.findMostPopularDiscounts();
        List<Map<String, Object>> popularDiscounts = new ArrayList<>();
        
        for (Object[] result : results) {
            Map<String, Object> discount = new HashMap<>();
            discount.put("discountCode", result[0]);
            discount.put("usageCount", result[1]);
            popularDiscounts.add(discount);
        }
        
        return popularDiscounts;
    }

    // Sample Data Initialization
    public void initializeSampleDiscounts() {
        if (ecoDiscountRepository.count() > 0) {
            return; // Already initialized
        }

        List<EcoDiscount> sampleDiscounts = Arrays.asList(
            createSampleDiscount("ECO10", "Eco-Friendly Discount", "10% off on all eco-friendly products",
                "PERCENTAGE", 10.0, null, 100.0, null, null),
            createSampleDiscount("GREENSAVE", "Green Saver", "₹100 off on orders above ₹500",
                "FIXED_AMOUNT", 100.0, 500.0, null, null, null),
            createSampleDiscount("CARBON20", "Carbon Reduction Special", "20% off on carbon-neutral products",
                "PERCENTAGE", 20.0, 300.0, 200.0, null, null),
            createSampleDiscount("ECOFIRST", "First Time Eco Buyer", "15% off for new eco shoppers",
                "PERCENTAGE", 15.0, 200.0, 150.0, null, null),
            createSampleDiscount("GREENPOINTS", "Eco Points Discount", "25% off for eco warriors",
                "PERCENTAGE", 25.0, 400.0, 300.0, null, null, true, 100)
        );

        ecoDiscountRepository.saveAll(sampleDiscounts);
    }

    private EcoDiscount createSampleDiscount(String code, String title, String description,
                                           String type, Double value, Double minOrder, Double maxDiscount,
                                           String category, String storeId) {
        return createSampleDiscount(code, title, description, type, value, minOrder, maxDiscount, 
                                   category, storeId, false, 0);
    }

    private EcoDiscount createSampleDiscount(String code, String title, String description,
                                           String type, Double value, Double minOrder, Double maxDiscount,
                                           String category, String storeId, Boolean requiresEcoPoints, Integer requiredPoints) {
        EcoDiscount discount = new EcoDiscount(code, title, description, type, value);
        discount.setMinimumOrderAmount(minOrder);
        discount.setMaximumDiscountAmount(maxDiscount);
        discount.setApplicableCategory(category);
        discount.setApplicableStoreId(storeId);
        discount.setRequiresEcoPoints(requiresEcoPoints);
        discount.setRequiredEcoPoints(requiredPoints);
        discount.setCreatedBy("system");
        discount.setUsageLimit(1000); // Set usage limit for sample discounts
        
        return discount;
    }
}