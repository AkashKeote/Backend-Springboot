# 🌍 EcoBazaarX - Real Carbon Footprint Calculation System

## Overview
EcoBazaarX features a **scientifically accurate, comprehensive Carbon Footprint Calculation System** that replaces dummy values with real environmental impact formulas based on international research and industry standards.

---

## 🎯 Key Features

### 1. **Scientific Accuracy**
- **Lifecycle Assessment (LCA)** compliant with ISO 14040/14044
- **IPCC Guidelines** for carbon footprint methodology
- **Ecoinvent Database v3.8** emission factors
- **EPA Guidelines** for transportation and manufacturing
- **Peer-reviewed research** from international sources

### 2. **Comprehensive Calculation Components**

#### 🧵 **Material Emissions**
Calculate emissions based on:
- Material type (organic cotton, recycled plastic, bamboo, etc.)
- Weight of product
- Recycling benefits
- Carbon sequestration (for natural materials)

**Supported Materials:**
- **Textiles**: Organic cotton (3.8 kg CO₂e/kg), Conventional cotton (8.0), Recycled polyester (4.2)
- **Plastics**: Virgin (6.15), Recycled (2.1), PLA bioplastic (3.2)
- **Metals**: Aluminum (11.5), Recycled aluminum (0.5), Steel (2.9)
- **Natural**: Bamboo (1.2), Hemp (1.8), Cork (0.8 with -0.9 sequestration)

#### 🏭 **Manufacturing Emissions**
Process-specific calculations:
- Eco-friendly textile: 1.2 kg CO₂e per unit
- Conventional textile: 2.5 kg CO₂e per unit
- Eco-friendly electronics: 4.2 kg CO₂e per unit
- Conventional electronics: 8.5 kg CO₂e per unit

#### 🚛 **Transportation Emissions**
Formula: `distance (km) × weight (kg) × emission factor`

**Transportation Modes:**
- Local Truck: 0.000089 kg CO₂e/km/kg
- Long Distance Truck: 0.000062
- Ship Freight: 0.000011 (most eco-friendly)
- Air Freight: 0.000602 (highest emissions)
- Electric Vehicle: 0.000025
- Rail Freight: 0.000022

#### 📦 **Packaging Emissions**
Based on packaging material (typically 10-15% of product weight):
- Biodegradable: 0.3 kg CO₂e/kg
- Recycled Cardboard: 0.5
- Virgin Plastic: 6.0
- Recycled Plastic: 2.0
- Paper: 1.2
- No Packaging: 0.0

#### ♻️ **End-of-Life Emissions**
Disposal and biodegradation considerations:
- Recycled products: 70% reduction in disposal emissions
- Biodegradable materials (>50% rate): Reduced impact
- Landfill scenario: Default emissions applied

---

## 📊 Carbon Rating System

### Eco Ratings (A+ to F)
Based on savings vs conventional alternatives:

| Rating | Savings | Description |
|--------|---------|-------------|
| **A+** | 70%+ | Excellent! Maximum carbon savings |
| **A** | 50-70% | Great eco-friendly choice |
| **B+** | 30-50% | Good environmental impact |
| **B** | 15-30% | Fair carbon reduction |
| **C** | 0-15% | Average, similar to conventional |
| **D** | 0-20% higher | Below average |
| **F** | 20%+ higher | Poor, seek alternatives |

### 🌱 Environmental Equivalents
Real conversions based on scientific data:

| Metric | Conversion | Source |
|--------|------------|--------|
| **Trees Planted** | 1 kg CO₂ = 0.05 trees | 20 kg CO₂/tree/year absorption |
| **Car Travel Avoided** | 1 kg CO₂ = 4.6 km | 0.22 kg CO₂/km average car |
| **Electricity Saved** | 1 kg CO₂ = 1.9 kWh | 0.53 kg CO₂/kWh grid average |
| **Plastic Bottles Avoided** | 1 kg CO₂ = 50 bottles | 0.02 kg CO₂/bottle |

---

## 🚀 API Endpoints

### Base URL
```
http://localhost:8080/api/carbon-footprint
```

### 1. **Calculate Product Carbon Footprint**
**POST** `/calculate`

**Request Body:**
```json
{
  "productId": "PROD123",
  "productName": "Organic Cotton T-Shirt",
  "category": "Clothing",
  "weight": 0.3,
  "material": "organic_cotton",
  "isRecycled": false,
  "isOrganic": true,
  "manufacturingType": "eco_friendly",
  "transportationDistance": 150.0,
  "transportationType": "truck_local",
  "sourceCountry": "India",
  "packagingType": "biodegradable_packaging",
  "productLifespan": 3.0,
  "userId": "user123",
  "orderId": "order456"
}
```

**Response:**
```json
{
  "recordId": 1,
  "productName": "Organic Cotton T-Shirt",
  "category": "Clothing",
  "materialEmissions": 1.14,
  "manufacturingEmissions": 1.2,
  "transportationEmissions": 0.004,
  "packagingEmissions": 0.011,
  "endOfLifeEmissions": 0.03,
  "totalCarbonFootprint": 2.385,
  "conventionalFootprint": 3.6,
  "carbonSavings": 1.215,
  "savingsPercentage": 33.75,
  "ecoRating": "B+",
  "ratingDescription": "Good! This product saves 30-50% carbon compared to conventional alternatives.",
  "equivalentImpacts": {
    "trees_planted": 0.119,
    "car_km_avoided": 10.971,
    "electricity_kwh_saved": 4.532,
    "plastic_bottles_avoided": 119.25
  },
  "productWeight": 0.3,
  "materialType": "organic_cotton",
  "manufacturingType": "eco_friendly",
  "transportationType": "truck_local",
  "transportationDistance": 150.0,
  "packagingType": "biodegradable_packaging",
  "isRecycled": false,
  "isOrganic": true,
  "productLifespan": 3.0,
  "calculatedAt": "2025-10-02T10:30:00",
  "calculationMethod": "ISO 14040/14044 LCA + IPCC Guidelines",
  "dataSource": "Ecoinvent v3.8, EPA, IPCC 2021",
  "sustainabilityTips": "Great choice! Recycled materials save significant carbon emissions.",
  "improvementSuggestions": "You're doing great!"
}
```

### 2. **Get User Carbon History**
**GET** `/user/{userId}/history`

**Response:**
```json
[
  {
    "recordId": 1,
    "productName": "Organic Cotton T-Shirt",
    "totalCarbonFootprint": 2.385,
    "carbonSavings": 1.215,
    "ecoRating": "B+",
    "calculatedAt": "2025-10-02T10:30:00"
  }
]
```

### 3. **Get User Carbon Statistics**
**GET** `/user/{userId}/statistics`

**Response:**
```json
{
  "totalCarbonFootprint": 45.5,
  "totalCarbonSavings": 28.3,
  "totalPurchases": 12,
  "averageFootprintPerPurchase": 3.79,
  "treesEquivalent": 2.275,
  "carKmEquivalent": 209.3,
  "electricityKwhEquivalent": 86.45,
  "plasticBottlesEquivalent": 2275.0,
  "ecoRatingDistribution": {
    "A+": 3,
    "A": 5,
    "B+": 2,
    "B": 2
  },
  "carbonByCategory": {
    "Clothing": 18.5,
    "Electronics": 15.0,
    "Home & Garden": 12.0
  }
}
```

### 4. **Initialize Emission Factors** (Admin)
**POST** `/initialize-emission-factors`

**Response:**
```json
{
  "message": "Emission factors initialized successfully",
  "status": "success"
}
```

### 5. **Health Check**
**GET** `/health`

**Response:**
```json
{
  "status": "healthy",
  "service": "Carbon Footprint Calculation Service",
  "version": "1.0.0",
  "methodology": "ISO 14040/14044 LCA + IPCC Guidelines"
}
```

---

## 📦 Database Schema

### **emission_factors** Table
Stores scientifically validated emission factors.

| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| category | VARCHAR | MATERIAL, TRANSPORTATION, MANUFACTURING, PACKAGING, END_OF_LIFE |
| subcategory | VARCHAR | textiles, plastics, metals, natural, freight, etc. |
| material_type | VARCHAR | organic_cotton, recycled_plastic, truck_local, etc. |
| name | VARCHAR | Human-readable name |
| unit | VARCHAR | kg CO2e per kg, kg CO2e per km/kg, etc. |
| emission_factor | DOUBLE | The actual emission value |
| carbon_sequestration | DOUBLE | Negative emissions (CO2 absorption) |
| recycling_benefit | DOUBLE | CO2 saved when recycled |
| biodegradation_rate | DOUBLE | Percentage that biodegrades |
| data_source | VARCHAR | IPCC 2021, Ecoinvent v3.8, EPA Guidelines |
| confidence_level | VARCHAR | HIGH, MEDIUM, LOW |

### **carbon_footprint_records** Table
Stores calculated carbon footprints for each product purchase.

| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT | Primary key |
| user_id | VARCHAR | User identifier |
| order_id | VARCHAR | Order identifier |
| product_id | VARCHAR | Product identifier |
| product_name | VARCHAR | Product name |
| material_emissions | DOUBLE | Emissions from materials |
| manufacturing_emissions | DOUBLE | Emissions from manufacturing |
| transportation_emissions | DOUBLE | Emissions from transportation |
| packaging_emissions | DOUBLE | Emissions from packaging |
| end_of_life_emissions | DOUBLE | Emissions from disposal |
| total_carbon_footprint | DOUBLE | Sum of all emissions |
| conventional_footprint | DOUBLE | Comparison footprint |
| carbon_savings | DOUBLE | Savings vs conventional |
| savings_percentage | DOUBLE | Percentage savings |
| eco_rating | VARCHAR | A+, A, B+, B, C, D, F |
| trees_equivalent | DOUBLE | Trees planted equivalent |
| car_km_equivalent | DOUBLE | Car km avoided equivalent |
| electricity_kwh_equivalent | DOUBLE | Electricity saved equivalent |
| plastic_bottles_equivalent | DOUBLE | Plastic bottles avoided |

---

## 🔬 Scientific References

This system is based on:

1. **IPCC Guidelines (2021)** - For carbon footprint methodology
2. **Ecoinvent Database v3.8** - For material emission factors
3. **EPA Guidelines** - For transportation and manufacturing emissions
4. **ISO 14040/14044** - For lifecycle assessment standards
5. **GHG Protocol** - For corporate carbon accounting
6. **IMO/ICAO Guidelines** - For shipping and aviation emissions

---

## 💡 Usage Examples

### Example 1: Calculate Eco-Friendly Product
```bash
curl -X POST http://localhost:8080/api/carbon-footprint/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Bamboo Water Bottle",
    "category": "Home & Garden",
    "weight": 0.15,
    "material": "bamboo",
    "manufacturingType": "eco_friendly",
    "transportationDistance": 50,
    "transportationType": "electric_vehicle",
    "packagingType": "recycled_cardboard",
    "isRecycled": false,
    "isOrganic": true,
    "userId": "user123"
  }'
```

### Example 2: Calculate Recycled Product
```bash
curl -X POST http://localhost:8080/api/carbon-footprint/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "productName": "Recycled Polyester Jacket",
    "category": "Clothing",
    "weight": 0.6,
    "material": "recycled_polyester",
    "manufacturingType": "eco_friendly",
    "transportationDistance": 200,
    "transportationType": "truck_long_distance",
    "packagingType": "no_packaging",
    "isRecycled": true,
    "productLifespan": 5.0,
    "userId": "user123"
  }'
```

### Example 3: Get User Statistics
```bash
curl http://localhost:8080/api/carbon-footprint/user/user123/statistics
```

---

## 🎮 Testing the System

### Step 1: Initialize Emission Factors
```bash
curl -X POST http://localhost:8080/api/carbon-footprint/initialize-emission-factors
```

### Step 2: Calculate Sample Product
Use the calculation endpoint with sample data (see examples above)

### Step 3: Verify Database
```sql
-- Check emission factors
SELECT * FROM emission_factors WHERE is_active = TRUE;

-- Check carbon records
SELECT * FROM carbon_footprint_records ORDER BY calculated_at DESC LIMIT 10;

-- Get user statistics
SELECT user_id, 
       COUNT(*) as total_purchases,
       SUM(total_carbon_footprint) as total_footprint,
       SUM(carbon_savings) as total_savings,
       AVG(savings_percentage) as avg_savings_percentage
FROM carbon_footprint_records
GROUP BY user_id;
```

---

## 🌟 Benefits

### **For Users:**
- ✅ Real, transparent carbon footprint data
- ✅ Informed purchasing decisions
- ✅ Gamified sustainability tracking
- ✅ Actionable improvement recommendations

### **For Platform:**
- ✅ Scientific credibility
- ✅ Regulatory compliance (ESG reporting)
- ✅ Competitive differentiation
- ✅ Data-driven sustainability insights

### **For Environment:**
- ✅ Encourages eco-friendly purchases
- ✅ Tracks real environmental impact
- ✅ Promotes sustainable consumption
- ✅ Supports carbon reduction goals

---

## 🔮 Future Enhancements

1. **Real-time API Integration**
   - Connect to live emission databases
   - Update factors automatically

2. **Machine Learning**
   - Predict product carbon footprint
   - Personalized recommendations

3. **Regional Variations**
   - Country-specific emission factors
   - Grid electricity differences

4. **Supply Chain Transparency**
   - Multi-tier supplier tracking
   - Blockchain verification

5. **Carbon Offset Integration**
   - Link to carbon credit marketplaces
   - Automated offset purchases

6. **Advanced Analytics**
   - Trend forecasting
   - Comparative industry benchmarks
   - Sustainability goal tracking

---

## 📞 Support

For issues or questions about the Carbon Footprint Calculation System:
- Email: support@ecobazaarx.com
- Documentation: https://docs.ecobazaarx.com/carbon-footprint
- API Reference: https://api.ecobazaarx.com/docs

---

## 📄 License

This Carbon Footprint Calculation System is proprietary to EcoBazaarX.
Scientific methodologies are based on publicly available research and standards.

---

**Last Updated:** October 2, 2025  
**Version:** 1.0.0  
**Methodology:** ISO 14040/14044 LCA + IPCC Guidelines
