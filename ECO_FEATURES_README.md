# Eco Challenge & Eco Discount Features - Backend Implementation

## Overview
This document describes the backend implementation of **Eco Challenges** and **Eco Discounts** features for EcoBazaarX platform.

## 🎯 Features Implemented

### 1. Eco Challenge System
A comprehensive challenge system that encourages users to participate in eco-friendly activities and earn rewards.

### 2. Eco Discount System
A flexible discount management system that provides sustainable shopping incentives.

---

## 📋 Database Entities

### Eco Challenge Entities

#### 1. **EcoChallenge**
Main challenge entity storing challenge details.

**Fields:**
- `id` (Long, Primary Key)
- `challengeId` (String, Unique) - Unique identifier like "CHALLENGE_20251002_123"
- `title` (String) - Challenge title
- `description` (Text) - Detailed description
- `category` (String) - e.g., "Waste Reduction", "Carbon Reduction", "Local Support"
- `targetValue` (Integer) - Goal to achieve
- `targetUnit` (String) - Unit of measurement (days, liters, stores, etc.)
- `durationDays` (Integer) - Challenge duration
- `reward` (String) - Reward description
- `rewardPoints` (Integer) - Eco points awarded
- `iconName` (String) - Icon identifier
- `colorHex` (String) - Color code for UI
- `isActive` (Boolean) - Active status
- `difficulty` (String) - EASY, MEDIUM, HARD
- `createdBy` (String) - Creator user ID
- `startDate` (DateTime)
- `endDate` (DateTime)
- `createdAt` (DateTime)
- `updatedAt` (DateTime)

#### 2. **UserChallengeProgress**
Tracks individual user progress on challenges.

**Fields:**
- `id` (Long, Primary Key)
- `userId` (String) - User identifier
- `challengeId` (String) - Challenge identifier
- `currentProgress` (Integer) - Current progress value
- `progressPercentage` (Double) - Progress percentage (0-100)
- `isCompleted` (Boolean) - Completion status
- `completedAt` (DateTime) - Completion timestamp
- `startedAt` (DateTime) - Start timestamp
- `pointsEarned` (Integer) - Points earned from this challenge
- `notes` (Text) - Optional notes
- `createdAt` (DateTime)
- `updatedAt` (DateTime)

### Eco Discount Entities

#### 3. **EcoDiscount**
Discount offers for eco-friendly shopping.

**Fields:**
- `id` (Long, Primary Key)
- `discountCode` (String, Unique) - e.g., "ECO10", "GREENSAVE"
- `title` (String) - Discount title
- `description` (Text) - Description
- `discountType` (String) - PERCENTAGE, FIXED_AMOUNT, FREE_SHIPPING
- `discountValue` (Double) - Discount value
- `minimumOrderAmount` (Double) - Minimum order requirement
- `maximumDiscountAmount` (Double) - Maximum discount cap
- `isActive` (Boolean) - Active status
- `applicableCategory` (String) - Specific category or null for all
- `applicableStoreId` (String) - Specific store or null for all
- `usageLimit` (Integer) - Total usage limit
- `currentUsageCount` (Integer) - Current usage count
- `userUsageLimit` (Integer) - Per-user usage limit
- `requiresEcoPoints` (Boolean) - Requires eco points
- `requiredEcoPoints` (Integer) - Minimum eco points needed
- `validFrom` (DateTime) - Start validity
- `validUntil` (DateTime) - End validity
- `createdBy` (String) - Creator admin ID
- `createdAt` (DateTime)
- `updatedAt` (DateTime)

#### 4. **UserDiscountUsage**
Tracks discount usage by users.

**Fields:**
- `id` (Long, Primary Key)
- `userId` (String) - User identifier
- `discountCode` (String) - Discount code used
- `orderId` (String) - Associated order
- `discountAmount` (Double) - Discount amount applied
- `orderAmount` (Double) - Total order amount
- `usedAt` (DateTime) - Usage timestamp

### Updated Entities

#### **User** (Updated)
Added eco-friendly tracking fields:
- `ecoPoints` (Integer) - Total eco points earned
- `totalCarbonSaved` (Double) - Total carbon footprint saved

#### **Order** (Updated)
Added discount tracking:
- `discountCode` (String) - Applied discount code

---

## 🔌 API Endpoints

### Eco Challenge Endpoints

#### Challenge Management

**POST** `/api/eco-challenges`
- Create a new challenge
- Body: EcoChallenge object

**GET** `/api/eco-challenges`
- Get all active challenges

**GET** `/api/eco-challenges/active`
- Get currently active challenges (within date range)

**GET** `/api/eco-challenges/{challengeId}`
- Get challenge by ID

**GET** `/api/eco-challenges/category/{category}`
- Get challenges by category

**GET** `/api/eco-challenges/difficulty/{difficulty}`
- Get challenges by difficulty level

**GET** `/api/eco-challenges/search?keyword={keyword}`
- Search challenges by title/description

**PUT** `/api/eco-challenges/{challengeId}`
- Update challenge details

**DELETE** `/api/eco-challenges/{challengeId}`
- Deactivate a challenge

#### User Progress

**POST** `/api/eco-challenges/{challengeId}/start?userId={userId}`
- Start a challenge for user

**PUT** `/api/eco-challenges/{challengeId}/progress?userId={userId}&progress={progress}`
- Update user progress
- Returns completion status if challenge completed

**GET** `/api/eco-challenges/user/{userId}/progress`
- Get all user's challenge progress

**GET** `/api/eco-challenges/user/{userId}/completed`
- Get user's completed challenges

**GET** `/api/eco-challenges/user/{userId}/active`
- Get user's active challenges

#### Analytics

**GET** `/api/eco-challenges/user/{userId}/stats`
- Get user statistics
- Returns: totalPoints, completedChallenges, activeChallenges, averageProgress, completionRate

**GET** `/api/eco-challenges/{challengeId}/analytics`
- Get challenge analytics
- Returns: totalParticipants, completedUsers, completionRate, activeParticipants

**GET** `/api/eco-challenges/leaderboard`
- Get top users by eco points

#### Admin

**POST** `/api/eco-challenges/initialize-sample-data`
- Initialize sample challenges

---

### Eco Discount Endpoints

#### Discount Management

**POST** `/api/eco-discounts`
- Create a new discount
- Body: EcoDiscount object

**GET** `/api/eco-discounts`
- Get all active discounts

**GET** `/api/eco-discounts/available`
- Get available discounts (considering usage limits)

**GET** `/api/eco-discounts/valid`
- Get currently valid discounts (within date range)

**GET** `/api/eco-discounts/{discountCode}`
- Get discount by code

**GET** `/api/eco-discounts/type/{discountType}`
- Get discounts by type (PERCENTAGE, FIXED_AMOUNT, FREE_SHIPPING)

**GET** `/api/eco-discounts/category/{category}`
- Get discounts for specific category

**GET** `/api/eco-discounts/store/{storeId}`
- Get discounts for specific store

**GET** `/api/eco-discounts/search?keyword={keyword}`
- Search discounts

**PUT** `/api/eco-discounts/{discountCode}`
- Update discount

**DELETE** `/api/eco-discounts/{discountCode}`
- Deactivate discount

#### Discount Application

**POST** `/api/eco-discounts/validate`
- Validate discount for order
- Body: `{ discountCode, userId, orderAmount, category, storeId, userEcoPoints }`
- Returns: validation result with discount amount

**POST** `/api/eco-discounts/apply`
- Apply discount to order
- Body: `{ userId, discountCode, orderId, orderAmount, discountAmount }`
- Records usage and updates counters

**GET** `/api/eco-discounts/calculate?discountCode={code}&orderAmount={amount}`
- Calculate discount amount

#### User-Specific

**GET** `/api/eco-discounts/user/{userId}/applicable?userEcoPoints={points}&orderAmount={amount}`
- Get applicable discounts for user

**GET** `/api/eco-discounts/user/{userId}/stats`
- Get user discount statistics
- Returns: totalDiscountsUsed, totalAmountSaved, ordersWithDiscounts

#### Analytics

**GET** `/api/eco-discounts/{discountCode}/analytics`
- Get discount analytics
- Returns: totalUsages, averageDiscountAmount, totalDiscountGiven, uniqueUsers

**GET** `/api/eco-discounts/analytics/popular`
- Get most popular discounts

#### Admin

**POST** `/api/eco-discounts/initialize-sample-data`
- Initialize sample discounts

---

## 📊 Sample Data

### Sample Challenges

1. **Plastic-Free Week**
   - Category: Waste Reduction
   - Target: 7 days
   - Reward: 500 Eco Points
   - Difficulty: EASY

2. **Carbon Footprint Reduction**
   - Category: Carbon Reduction
   - Target: 20% reduction
   - Reward: 300 Eco Points
   - Difficulty: MEDIUM

3. **Local Shopping Spree**
   - Category: Local Support
   - Target: 5 stores
   - Reward: 200 Eco Points
   - Difficulty: EASY

4. **Water Conservation**
   - Category: Resource Conservation
   - Target: 100 liters
   - Reward: 250 Eco Points
   - Difficulty: MEDIUM

5. **Energy Saver**
   - Category: Energy Conservation
   - Target: 50 kWh
   - Reward: 400 Eco Points
   - Difficulty: HARD

### Sample Discounts

1. **ECO10** - 10% off on all eco-friendly products
2. **GREENSAVE** - ₹100 off on orders above ₹500
3. **CARBON20** - 20% off on carbon-neutral products (min ₹300)
4. **ECOFIRST** - 15% off for new eco shoppers (min ₹200)
5. **GREENPOINTS** - 25% off for users with 100+ eco points (min ₹400)

---

## 🔄 Integration with Frontend

### Eco Challenge Integration

The frontend uses Firebase Firestore for real-time updates, but the backend provides RESTful APIs for:
- Challenge CRUD operations
- User progress tracking
- Statistics and leaderboards
- Challenge validation and completion

### Eco Discount Integration

The frontend cart system integrates with discount validation:
1. User enters discount code
2. Frontend calls `/api/eco-discounts/validate` with order details
3. Backend validates and calculates discount
4. Frontend displays discount amount
5. On order completion, frontend calls `/api/eco-discounts/apply`

---

## 🚀 Usage Examples

### Starting a Challenge

```bash
POST /api/eco-challenges/CHALLENGE_20251002_123/start?userId=user123
```

### Updating Progress

```bash
PUT /api/eco-challenges/CHALLENGE_20251002_123/progress?userId=user123&progress=5
```

### Validating Discount

```bash
POST /api/eco-discounts/validate
Content-Type: application/json

{
  "discountCode": "ECO10",
  "userId": "user123",
  "orderAmount": 1000.0,
  "category": "Clothing",
  "storeId": null,
  "userEcoPoints": 150
}
```

### Applying Discount

```bash
POST /api/eco-discounts/apply
Content-Type: application/json

{
  "userId": "user123",
  "discountCode": "ECO10",
  "orderId": "ORDER_20251002_456",
  "orderAmount": 1000.0,
  "discountAmount": 100.0
}
```

---

## 🔐 Security Considerations

1. **Authentication**: All endpoints should be protected with JWT authentication
2. **Authorization**: 
   - Admin-only endpoints: create/update/delete challenges and discounts
   - User endpoints: require user ID validation
3. **Validation**: Input validation for all API requests
4. **Rate Limiting**: Implement rate limiting for discount validation to prevent abuse

---

## 📝 Future Enhancements

1. **Challenge Notifications**: Push notifications for challenge milestones
2. **Social Features**: Share challenge progress with friends
3. **Team Challenges**: Group challenges for communities
4. **Dynamic Discounts**: AI-based personalized discount recommendations
5. **Gamification**: Badges and achievements for completing challenges
6. **Seasonal Challenges**: Time-limited special challenges
7. **Referral Discounts**: Discounts for referring friends

---

## 🛠️ Testing

Use the initialize sample data endpoints to populate test data:

```bash
POST /api/eco-challenges/initialize-sample-data
POST /api/eco-discounts/initialize-sample-data
```

---

## 📞 Support

For any issues or questions regarding these features, please contact the development team or create an issue in the repository.

---

**Version**: 1.0.0  
**Last Updated**: October 2, 2025  
**Author**: EcoBazaarX Team
