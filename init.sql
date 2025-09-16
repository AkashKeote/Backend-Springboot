-- Database initialization script for EcoBazaarX
-- This script will run when the MySQL container starts for the first time

-- Create additional databases if needed
-- CREATE DATABASE IF NOT EXISTS ecobazaar_test;

-- Create additional users if needed
-- CREATE USER IF NOT EXISTS 'test_user'@'%' IDENTIFIED BY 'test_password';
-- GRANT ALL PRIVILEGES ON ecobazaar_test.* TO 'test_user'@'%';

-- Sample data insertion (uncomment if needed)
-- USE ecobazaar_db;

-- Insert sample categories
-- INSERT IGNORE INTO categories (name, description) VALUES 
-- ('Electronics', 'Electronic devices and gadgets'),
-- ('Clothing', 'Fashion and apparel'),
-- ('Books', 'Books and literature');

-- Flush privileges
FLUSH PRIVILEGES;

-- Set timezone
SET GLOBAL time_zone = '+00:00';