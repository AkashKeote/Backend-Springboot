-- =========================================
-- EcoBazaarX - Database Migration Script
-- Eco Challenges, Eco Discounts & Leaderboard
-- =========================================

-- =========================================
-- DROP EXISTING TABLES (Fresh Start)
-- =========================================

-- Drop tables in correct order (foreign keys first)
DROP TABLE IF EXISTS user_challenge_progress;
DROP TABLE IF EXISTS user_discount_usage;
DROP TABLE IF EXISTS carbon_footprint_records;
DROP TABLE IF EXISTS emission_factors;
DROP TABLE IF EXISTS eco_challenges;
DROP TABLE IF EXISTS eco_discounts;
DROP TABLE IF EXISTS user_eco_profiles;

-- =========================================
-- CREATE TABLES
-- =========================================

-- Create eco_challenges table
CREATE TABLE IF NOT EXISTS eco_challenges (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    challenge_id VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    target_value INT NOT NULL,
    target_unit VARCHAR(50) NOT NULL,
    duration_days INT NOT NULL,
    reward VARCHAR(255) NOT NULL,
    reward_points INT NOT NULL,
    icon_name VARCHAR(100),
    color_hex VARCHAR(20),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    difficulty VARCHAR(50) NOT NULL,
    created_by VARCHAR(255),
    start_date DATETIME,
    end_date DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_challenge_category (category),
    INDEX idx_challenge_difficulty (difficulty),
    INDEX idx_challenge_active (is_active),
    INDEX idx_challenge_dates (start_date, end_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create user_challenge_progress table
CREATE TABLE IF NOT EXISTS user_challenge_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    challenge_id VARCHAR(255) NOT NULL,
    current_progress INT NOT NULL DEFAULT 0,
    progress_percentage DOUBLE NOT NULL DEFAULT 0.0,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    completed_at DATETIME,
    started_at DATETIME,
    points_earned INT NOT NULL DEFAULT 0,
    notes TEXT,
    eco_challenge_id BIGINT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_progress (user_id),
    INDEX idx_challenge_progress (challenge_id),
    INDEX idx_completed (is_completed),
    UNIQUE KEY unique_user_challenge (user_id, challenge_id),
    FOREIGN KEY (eco_challenge_id) REFERENCES eco_challenges(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create eco_discounts table
CREATE TABLE IF NOT EXISTS eco_discounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    discount_code VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    discount_type VARCHAR(50) NOT NULL,
    discount_value DOUBLE NOT NULL,
    minimum_order_amount DOUBLE,
    maximum_discount_amount DOUBLE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    applicable_category VARCHAR(100),
    applicable_store_id VARCHAR(255),
    usage_limit INT,
    current_usage_count INT NOT NULL DEFAULT 0,
    user_usage_limit INT NOT NULL DEFAULT 1,
    requires_eco_points BOOLEAN NOT NULL DEFAULT FALSE,
    required_eco_points INT NOT NULL DEFAULT 0,
    valid_from DATETIME,
    valid_until DATETIME,
    created_by VARCHAR(255),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_discount_active (is_active),
    INDEX idx_discount_type (discount_type),
    INDEX idx_discount_validity (valid_from, valid_until),
    INDEX idx_discount_category (applicable_category),
    INDEX idx_discount_store (applicable_store_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create user_discount_usage table
CREATE TABLE IF NOT EXISTS user_discount_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    discount_code VARCHAR(255) NOT NULL,
    order_id VARCHAR(255) NOT NULL,
    discount_amount DOUBLE NOT NULL,
    order_amount DOUBLE NOT NULL,
    eco_discount_id BIGINT,
    used_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_usage (user_id),
    INDEX idx_discount_usage (discount_code),
    INDEX idx_order_usage (order_id),
    INDEX idx_used_at (used_at),
    FOREIGN KEY (eco_discount_id) REFERENCES eco_discounts(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create user_eco_profiles table (Leaderboard)
CREATE TABLE IF NOT EXISTS user_eco_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL UNIQUE,
    user_name VARCHAR(255) NOT NULL,
    user_email VARCHAR(255),
    
    -- Eco Points and Rankings
    total_eco_points INT NOT NULL DEFAULT 0,
    current_rank INT,
    previous_rank INT,
    eco_level INT NOT NULL DEFAULT 1,
    level_name VARCHAR(100) DEFAULT 'Eco Beginner',
    
    -- Environmental Impact
    total_carbon_saved DOUBLE NOT NULL DEFAULT 0.0,
    total_water_saved DOUBLE DEFAULT 0.0,
    total_energy_saved DOUBLE DEFAULT 0.0,
    total_waste_reduced DOUBLE DEFAULT 0.0,
    trees_equivalent DOUBLE DEFAULT 0.0,
    
    -- Challenge Statistics
    total_challenges_completed INT NOT NULL DEFAULT 0,
    active_challenges INT NOT NULL DEFAULT 0,
    challenge_completion_rate DOUBLE DEFAULT 0.0,
    current_streak_days INT NOT NULL DEFAULT 0,
    longest_streak_days INT NOT NULL DEFAULT 0,
    last_activity_date DATETIME,
    
    -- Shopping Statistics
    total_orders INT NOT NULL DEFAULT 0,
    total_spent DOUBLE DEFAULT 0.0,
    total_savings_from_discounts DOUBLE DEFAULT 0.0,
    eco_products_purchased INT NOT NULL DEFAULT 0,
    
    -- Badges and Achievements
    total_badges INT NOT NULL DEFAULT 0,
    badges TEXT,
    achievements TEXT,
    
    -- Social Features
    total_referrals INT NOT NULL DEFAULT 0,
    community_contributions INT NOT NULL DEFAULT 0,
    
    -- Profile Settings
    is_public_profile BOOLEAN DEFAULT TRUE,
    show_on_leaderboard BOOLEAN DEFAULT TRUE,
    profile_image_url VARCHAR(500),
    bio TEXT,
    
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_eco_points (total_eco_points DESC),
    INDEX idx_carbon_saved (total_carbon_saved DESC),
    INDEX idx_level (eco_level DESC),
    INDEX idx_leaderboard (show_on_leaderboard, total_eco_points DESC),
    INDEX idx_challenges (total_challenges_completed DESC),
    INDEX idx_streak (current_streak_days DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Update users table - Add eco points and carbon tracking
ALTER TABLE users 
ADD COLUMN IF NOT EXISTS eco_points INT DEFAULT 0,
ADD COLUMN IF NOT EXISTS total_carbon_saved DOUBLE DEFAULT 0.0;

-- Update orders table - Add discount code
ALTER TABLE orders 
ADD COLUMN IF NOT EXISTS discount_code VARCHAR(255);

-- =========================================
-- Carbon Footprint Tables
-- =========================================

-- Create emission_factors table
CREATE TABLE IF NOT EXISTS emission_factors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(255) NOT NULL,
    subcategory VARCHAR(255) NOT NULL,
    material_type VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    unit VARCHAR(255) NOT NULL,
    emission_factor DOUBLE NOT NULL,
    description TEXT,
    data_source VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    carbon_sequestration DOUBLE,
    recycling_benefit DOUBLE,
    biodegradation_rate DOUBLE,
    region VARCHAR(100),
    confidence_level VARCHAR(50),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category),
    INDEX idx_subcategory (subcategory),
    INDEX idx_material_type (material_type),
    INDEX idx_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create carbon_footprint_records table
CREATE TABLE IF NOT EXISTS carbon_footprint_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    order_id VARCHAR(255),
    product_id VARCHAR(255),
    product_name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    
    -- Material
    material_type VARCHAR(100),
    material_emissions DOUBLE DEFAULT 0.0,
    
    -- Manufacturing
    manufacturing_type VARCHAR(100),
    manufacturing_emissions DOUBLE DEFAULT 0.0,
    
    -- Transportation
    transportation_type VARCHAR(100),
    transportation_distance DOUBLE,
    transportation_emissions DOUBLE DEFAULT 0.0,
    
    -- Packaging
    packaging_type VARCHAR(100),
    packaging_emissions DOUBLE DEFAULT 0.0,
    
    -- End of life
    end_of_life_emissions DOUBLE DEFAULT 0.0,
    
    -- Totals
    total_carbon_footprint DOUBLE NOT NULL,
    conventional_footprint DOUBLE,
    carbon_savings DOUBLE DEFAULT 0.0,
    savings_percentage DOUBLE DEFAULT 0.0,
    eco_rating VARCHAR(10),
    
    -- Product details
    product_weight DOUBLE,
    is_recycled BOOLEAN DEFAULT FALSE,
    is_organic BOOLEAN DEFAULT FALSE,
    product_lifespan DOUBLE,
    source_country VARCHAR(100),
    
    -- Environmental equivalents
    trees_equivalent DOUBLE,
    car_km_equivalent DOUBLE,
    electricity_kwh_equivalent DOUBLE,
    plastic_bottles_equivalent DOUBLE,
    
    calculated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    
    INDEX idx_user_id (user_id),
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id),
    INDEX idx_calculated_at (calculated_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================================
-- Insert Sample Emission Factors
-- =========================================

-- Material factors - Textiles
INSERT INTO emission_factors (category, subcategory, material_type, name, unit, emission_factor, data_source, confidence_level, carbon_sequestration, recycling_benefit, biodegradation_rate, description)
VALUES
('MATERIAL', 'textiles', 'organic_cotton', 'Organic Cotton', 'kg CO2e per kg', 3.8, 'IPCC 2021', 'HIGH', 0.0, 0.5, 95.0, 'Scientific emission factor for Organic Cotton'),
('MATERIAL', 'textiles', 'conventional_cotton', 'Conventional Cotton', 'kg CO2e per kg', 8.0, 'Ecoinvent v3.8', 'HIGH', 0.0, 0.3, 90.0, 'Scientific emission factor for Conventional Cotton'),
('MATERIAL', 'textiles', 'recycled_polyester', 'Recycled Polyester', 'kg CO2e per kg', 4.2, 'EPA Guidelines', 'HIGH', 0.0, 3.0, 80.0, 'Scientific emission factor for Recycled Polyester'),
('MATERIAL', 'textiles', 'virgin_polyester', 'Virgin Polyester', 'kg CO2e per kg', 10.5, 'Ecoinvent v3.8', 'HIGH', 0.0, 2.5, 70.0, 'Scientific emission factor for Virgin Polyester');

-- Material factors - Plastics
INSERT INTO emission_factors (category, subcategory, material_type, name, unit, emission_factor, data_source, confidence_level, carbon_sequestration, recycling_benefit, biodegradation_rate, description)
VALUES
('MATERIAL', 'plastics', 'virgin_plastic', 'Virgin Plastic', 'kg CO2e per kg', 6.15, 'EPA Guidelines', 'HIGH', 0.0, 1.5, 50.0, 'Scientific emission factor for Virgin Plastic'),
('MATERIAL', 'plastics', 'recycled_plastic', 'Recycled Plastic', 'kg CO2e per kg', 2.1, 'EPA Guidelines', 'HIGH', 0.0, 4.0, 85.0, 'Scientific emission factor for Recycled Plastic'),
('MATERIAL', 'plastics', 'pla_plastic', 'PLA Bioplastic', 'kg CO2e per kg', 3.2, 'IPCC 2021', 'MEDIUM', 0.0, 0.5, 100.0, 'Scientific emission factor for PLA Bioplastic');

-- Material factors - Metals
INSERT INTO emission_factors (category, subcategory, material_type, name, unit, emission_factor, data_source, confidence_level, carbon_sequestration, recycling_benefit, biodegradation_rate, description)
VALUES
('MATERIAL', 'metals', 'aluminum', 'Aluminum', 'kg CO2e per kg', 11.5, 'Ecoinvent v3.8', 'HIGH', 0.0, 0.5, 20.0, 'Scientific emission factor for Aluminum'),
('MATERIAL', 'metals', 'recycled_aluminum', 'Recycled Aluminum', 'kg CO2e per kg', 0.5, 'EPA Guidelines', 'HIGH', 0.0, 10.0, 95.0, 'Scientific emission factor for Recycled Aluminum'),
('MATERIAL', 'metals', 'steel', 'Steel', 'kg CO2e per kg', 2.9, 'Ecoinvent v3.8', 'HIGH', 0.0, 0.4, 30.0, 'Scientific emission factor for Steel');

-- Material factors - Natural Materials
INSERT INTO emission_factors (category, subcategory, material_type, name, unit, emission_factor, data_source, confidence_level, carbon_sequestration, recycling_benefit, biodegradation_rate, description)
VALUES
('MATERIAL', 'natural', 'bamboo', 'Bamboo', 'kg CO2e per kg', 1.2, 'IPCC 2021', 'MEDIUM', -0.5, 0.3, 100.0, 'Scientific emission factor for Bamboo'),
('MATERIAL', 'natural', 'hemp', 'Hemp', 'kg CO2e per kg', 1.8, 'IPCC 2021', 'MEDIUM', -0.3, 0.4, 100.0, 'Scientific emission factor for Hemp'),
('MATERIAL', 'natural', 'cork', 'Cork', 'kg CO2e per kg', 0.8, 'Ecoinvent v3.8', 'MEDIUM', -0.9, 0.2, 100.0, 'Scientific emission factor for Cork');

-- Transportation factors
INSERT INTO emission_factors (category, subcategory, material_type, name, unit, emission_factor, data_source, confidence_level, carbon_sequestration, recycling_benefit, biodegradation_rate, description)
VALUES
('TRANSPORTATION', 'freight', 'truck_local', 'Local Truck', 'kg CO2e per km per kg', 0.000089, 'EPA Guidelines', 'HIGH', 0.0, 0.0, 0.0, 'Scientific emission factor for Local Truck'),
('TRANSPORTATION', 'freight', 'truck_long_distance', 'Long Distance Truck', 'kg CO2e per km per kg', 0.000062, 'EPA Guidelines', 'HIGH', 0.0, 0.0, 0.0, 'Scientific emission factor for Long Distance Truck'),
('TRANSPORTATION', 'freight', 'ship_freight', 'Ship Freight', 'kg CO2e per km per kg', 0.000011, 'IMO Guidelines', 'HIGH', 0.0, 0.0, 0.0, 'Scientific emission factor for Ship Freight'),
('TRANSPORTATION', 'freight', 'air_freight', 'Air Freight', 'kg CO2e per km per kg', 0.000602, 'ICAO Guidelines', 'HIGH', 0.0, 0.0, 0.0, 'Scientific emission factor for Air Freight'),
('TRANSPORTATION', 'freight', 'electric_vehicle', 'Electric Vehicle', 'kg CO2e per km per kg', 0.000025, 'EPA Guidelines', 'HIGH', 0.0, 0.0, 0.0, 'Scientific emission factor for Electric Vehicle'),
('TRANSPORTATION', 'freight', 'rail_freight', 'Rail Freight', 'kg CO2e per km per kg', 0.000022, 'EPA Guidelines', 'HIGH', 0.0, 0.0, 0.0, 'Scientific emission factor for Rail Freight');

-- Manufacturing factors
INSERT INTO emission_factors (category, subcategory, material_type, name, unit, emission_factor, data_source, confidence_level, carbon_sequestration, recycling_benefit, biodegradation_rate, description)
VALUES
('MANUFACTURING', 'clothing', 'eco_friendly', 'Eco-Friendly Textile Manufacturing', 'kg CO2e per unit', 1.2, 'ISO 14040', 'MEDIUM', 0.0, 0.0, 0.0, 'Scientific emission factor for Eco-Friendly Textile Manufacturing'),
('MANUFACTURING', 'clothing', 'conventional', 'Conventional Textile Manufacturing', 'kg CO2e per unit', 2.5, 'ISO 14040', 'MEDIUM', 0.0, 0.0, 0.0, 'Scientific emission factor for Conventional Textile Manufacturing'),
('MANUFACTURING', 'electronics', 'eco_friendly', 'Eco-Friendly Electronics Manufacturing', 'kg CO2e per unit', 4.2, 'EPA Guidelines', 'MEDIUM', 0.0, 0.0, 0.0, 'Scientific emission factor for Eco-Friendly Electronics Manufacturing'),
('MANUFACTURING', 'electronics', 'conventional', 'Conventional Electronics Manufacturing', 'kg CO2e per unit', 8.5, 'EPA Guidelines', 'MEDIUM', 0.0, 0.0, 0.0, 'Scientific emission factor for Conventional Electronics Manufacturing');

-- Packaging factors
INSERT INTO emission_factors (category, subcategory, material_type, name, unit, emission_factor, data_source, confidence_level, carbon_sequestration, recycling_benefit, biodegradation_rate, description)
VALUES
('PACKAGING', 'general', 'biodegradable_packaging', 'Biodegradable Packaging', 'kg CO2e per kg', 0.3, 'EPA Guidelines', 'MEDIUM', 0.0, 0.0, 100.0, 'Scientific emission factor for Biodegradable Packaging'),
('PACKAGING', 'general', 'recycled_cardboard', 'Recycled Cardboard', 'kg CO2e per kg', 0.5, 'EPA Guidelines', 'HIGH', 0.0, 0.8, 90.0, 'Scientific emission factor for Recycled Cardboard'),
('PACKAGING', 'general', 'virgin_plastic', 'Virgin Plastic Packaging', 'kg CO2e per kg', 6.0, 'Ecoinvent v3.8', 'HIGH', 0.0, 1.5, 50.0, 'Scientific emission factor for Virgin Plastic Packaging'),
('PACKAGING', 'general', 'recycled_plastic', 'Recycled Plastic Packaging', 'kg CO2e per kg', 2.0, 'EPA Guidelines', 'HIGH', 0.0, 3.0, 80.0, 'Scientific emission factor for Recycled Plastic Packaging'),
('PACKAGING', 'general', 'paper', 'Paper Packaging', 'kg CO2e per kg', 1.2, 'Ecoinvent v3.8', 'HIGH', 0.0, 0.5, 95.0, 'Scientific emission factor for Paper Packaging'),
('PACKAGING', 'general', 'no_packaging', 'No Packaging', 'kg CO2e per kg', 0.0, 'Direct Calculation', 'HIGH', 0.0, 0.0, 100.0, 'Scientific emission factor for No Packaging');

-- End-of-life factors
INSERT INTO emission_factors (category, subcategory, material_type, name, unit, emission_factor, data_source, confidence_level, carbon_sequestration, recycling_benefit, biodegradation_rate, description)
VALUES
('END_OF_LIFE', 'disposal', 'organic_cotton', 'Organic Cotton Disposal', 'kg CO2e per kg', 0.1, 'EPA Guidelines', 'MEDIUM', 0.0, 0.0, 95.0, 'Scientific emission factor for Organic Cotton Disposal'),
('END_OF_LIFE', 'disposal', 'virgin_plastic', 'Plastic Disposal', 'kg CO2e per kg', 0.8, 'EPA Guidelines', 'MEDIUM', 0.0, 0.0, 10.0, 'Scientific emission factor for Plastic Disposal'),
('END_OF_LIFE', 'disposal', 'recycled_plastic', 'Recycled Plastic Disposal', 'kg CO2e per kg', 0.2, 'EPA Guidelines', 'MEDIUM', 0.0, 0.0, 85.0, 'Scientific emission factor for Recycled Plastic Disposal'),
('END_OF_LIFE', 'disposal', 'bamboo', 'Bamboo Disposal', 'kg CO2e per kg', 0.05, 'IPCC 2021', 'MEDIUM', 0.0, 0.0, 100.0, 'Scientific emission factor for Bamboo Disposal');

-- =========================================
-- Insert Sample Eco Challenges
-- =========================================

INSERT INTO eco_challenges 
(challenge_id, title, description, category, target_value, target_unit, duration_days, 
 reward, reward_points, difficulty, icon_name, color_hex, created_by, start_date, end_date)
VALUES
('CHALLENGE_20251002_001', 'Plastic-Free Week', 
 'Avoid single-use plastic for 7 consecutive days. Bring your own bags, bottles, and containers.',
 'Waste Reduction', 7, 'days', 7, '500 Eco Points', 500, 'EASY', 
 'recycling_rounded', '#B5C7F7', 'system', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY)),

('CHALLENGE_20251002_002', 'Carbon Footprint Reduction', 
 'Reduce your daily carbon footprint by 20%. Walk or cycle instead of driving, use public transport.',
 'Carbon Reduction', 20, '% reduction', 30, '300 Eco Points', 300, 'MEDIUM', 
 'eco_rounded', '#F9E79F', 'system', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY)),

('CHALLENGE_20251002_003', 'Local Shopping Spree', 
 'Buy from 5 local eco-friendly stores. Support local businesses and reduce transportation emissions.',
 'Local Support', 5, 'stores', 14, '200 Eco Points', 200, 'EASY', 
 'store_rounded', '#E8D5C4', 'system', NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY)),

('CHALLENGE_20251002_004', 'Water Conservation', 
 'Save 100 liters of water through mindful usage. Fix leaks, shorter showers, efficient appliances.',
 'Resource Conservation', 100, 'liters', 21, '250 Eco Points', 250, 'MEDIUM', 
 'water_drop_rounded', '#D6EAF8', 'system', NOW(), DATE_ADD(NOW(), INTERVAL 21 DAY)),

('CHALLENGE_20251002_005', 'Energy Saver', 
 'Reduce energy consumption by 50 kWh. Use LED bulbs, unplug devices, optimize heating/cooling.',
 'Energy Conservation', 50, 'kWh', 30, '400 Eco Points', 400, 'HARD', 
 'electric_bolt_rounded', '#E8F5E8', 'system', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY));

-- =========================================
-- Insert Sample Eco Discounts
-- =========================================

INSERT INTO eco_discounts 
(discount_code, title, description, discount_type, discount_value, 
 minimum_order_amount, maximum_discount_amount, created_by, valid_from, valid_until, usage_limit)
VALUES
('ECO10', 'Eco-Friendly Discount', 
 '10% off on all eco-friendly products',
 'PERCENTAGE', 10.0, NULL, 100.0, 'system', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1000),

('GREENSAVE', 'Green Saver', 
 '₹100 off on orders above ₹500',
 'FIXED_AMOUNT', 100.0, 500.0, NULL, 'system', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1000),

('CARBON20', 'Carbon Reduction Special', 
 '20% off on carbon-neutral products',
 'PERCENTAGE', 20.0, 300.0, 200.0, 'system', NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 500),

('ECOFIRST', 'First Time Eco Buyer', 
 '15% off for new eco shoppers',
 'PERCENTAGE', 15.0, 200.0, 150.0, 'system', NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 2000),

('GREENPOINTS', 'Eco Points Discount', 
 '25% off for eco warriors with 100+ points',
 'PERCENTAGE', 25.0, 400.0, 300.0, 'system', NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY), 500);

-- Update eco points requirement for GREENPOINTS discount
UPDATE eco_discounts 
SET requires_eco_points = TRUE, required_eco_points = 100 
WHERE discount_code = 'GREENPOINTS';

-- =========================================
-- Verification Queries
-- =========================================

-- Check if tables are created
SELECT 
    'eco_challenges' as table_name, COUNT(*) as record_count FROM eco_challenges
UNION ALL
SELECT 
    'user_challenge_progress', COUNT(*) FROM user_challenge_progress
UNION ALL
SELECT 
    'eco_discounts', COUNT(*) FROM eco_discounts
UNION ALL
SELECT 
    'user_discount_usage', COUNT(*) FROM user_discount_usage
UNION ALL
SELECT 
    'user_eco_profiles', COUNT(*) FROM user_eco_profiles
UNION ALL
SELECT 
    'emission_factors', COUNT(*) FROM emission_factors
UNION ALL
SELECT 
    'carbon_footprint_records', COUNT(*) FROM carbon_footprint_records;

-- =========================================
-- Useful Queries for Testing
-- =========================================

-- Get all active challenges
-- SELECT * FROM eco_challenges WHERE is_active = TRUE ORDER BY created_at DESC;

-- Get all available discounts
-- SELECT * FROM eco_discounts WHERE is_active = TRUE AND valid_from <= NOW() AND valid_until >= NOW();

-- Get top 10 users by eco points
-- SELECT * FROM user_eco_profiles WHERE show_on_leaderboard = TRUE ORDER BY total_eco_points DESC LIMIT 10;

-- Get user's rank
-- SELECT COUNT(*) + 1 as rank FROM user_eco_profiles WHERE total_eco_points > (SELECT total_eco_points FROM user_eco_profiles WHERE user_id = 'YOUR_USER_ID');

-- =========================================
-- End of Migration Script
-- =========================================
