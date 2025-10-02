# 🔗 EcoBazaarX - Frontend-Backend Integration Guide

## 📋 Overview
Complete guide to integrate Flutter frontend with Spring Boot backend for all eco features.

---

## 🌐 Backend Configuration

### Base URLs
```dart
// lib/config/api_config.dart
class ApiConfig {
  // Local Development
  static const String LOCAL_BASE_URL = 'http://localhost:8080/api';
  
  // Railway Production (Update with your Railway URL)
  static const String PROD_BASE_URL = 'https://your-railway-app.railway.app/api';
  
  // Current environment
  static const String BASE_URL = LOCAL_BASE_URL; // Change to PROD_BASE_URL for production
  
  // API Endpoints
  static const String ECO_CHALLENGES = '$BASE_URL/eco-challenges';
  static const String ECO_DISCOUNTS = '$BASE_URL/eco-discounts';
  static const String LEADERBOARD = '$BASE_URL/leaderboard';
  static const String CARBON_FOOTPRINT = '$BASE_URL/carbon-footprint';
}
```

---

## 🔧 Integration Steps

### Step 1: Update pubspec.yaml
```yaml
dependencies:
  flutter:
    sdk: flutter
  
  # HTTP Client
  http: ^1.1.0
  dio: ^5.3.3  # Alternative to http
  
  # State Management
  provider: ^6.0.5
  
  # JSON Serialization
  json_annotation: ^4.8.1
  
  # Local Storage
  shared_preferences: ^2.2.2
  
  # Environment Variables
  flutter_dotenv: ^5.1.0

dev_dependencies:
  build_runner: ^2.4.6
  json_serializable: ^6.7.1
```

### Step 2: Create API Service Base
```dart
// lib/services/api_service.dart
import 'package:http/http.dart' as http;
import 'dart:convert';
import '../config/api_config.dart';

class ApiService {
  static Future<Map<String, dynamic>> get(String endpoint) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.BASE_URL}$endpoint'),
        headers: {'Content-Type': 'application/json'},
      );
      
      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        throw Exception('Failed to load data: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Network error: $e');
    }
  }
  
  static Future<Map<String, dynamic>> post(
    String endpoint, 
    Map<String, dynamic> data
  ) async {
    try {
      final response = await http.post(
        Uri.parse('${ApiConfig.BASE_URL}$endpoint'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode(data),
      );
      
      if (response.statusCode == 200 || response.statusCode == 201) {
        return json.decode(response.body);
      } else {
        throw Exception('Failed to post data: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Network error: $e');
    }
  }
  
  static Future<Map<String, dynamic>> put(
    String endpoint, 
    Map<String, dynamic> data
  ) async {
    try {
      final response = await http.put(
        Uri.parse('${ApiConfig.BASE_URL}$endpoint'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode(data),
      );
      
      if (response.statusCode == 200) {
        return json.decode(response.body);
      } else {
        throw Exception('Failed to update data: ${response.statusCode}');
      }
    } catch (e) {
      throw Exception('Network error: $e');
    }
  }
  
  static Future<bool> delete(String endpoint) async {
    try {
      final response = await http.delete(
        Uri.parse('${ApiConfig.BASE_URL}$endpoint'),
        headers: {'Content-Type': 'application/json'},
      );
      
      return response.statusCode == 200 || response.statusCode == 204;
    } catch (e) {
      throw Exception('Network error: $e');
    }
  }
}
```

---

## 🌱 1. Eco Challenges Integration

### Backend Service
```dart
// lib/services/backend_eco_challenges_service.dart
import 'api_service.dart';

class BackendEcoChallengesService {
  // Get all active challenges
  static Future<List<dynamic>> getActiveChallenges() async {
    final response = await ApiService.get('/eco-challenges/active');
    return response['challenges'] ?? [];
  }
  
  // Get user's challenges
  static Future<List<dynamic>> getUserChallenges(String userId) async {
    final response = await ApiService.get('/eco-challenges/user/$userId');
    return response['challenges'] ?? [];
  }
  
  // Start a challenge
  static Future<Map<String, dynamic>> startChallenge(
    String userId, 
    String challengeId
  ) async {
    return await ApiService.post(
      '/eco-challenges/$challengeId/start',
      {'userId': userId}
    );
  }
  
  // Update progress
  static Future<Map<String, dynamic>> updateProgress(
    String progressId, 
    int currentProgress
  ) async {
    return await ApiService.put(
      '/eco-challenges/progress/$progressId',
      {'currentProgress': currentProgress}
    );
  }
  
  // Get user statistics
  static Future<Map<String, dynamic>> getUserStats(String userId) async {
    return await ApiService.get('/eco-challenges/user/$userId/stats');
  }
  
  // Get leaderboard
  static Future<List<dynamic>> getLeaderboard() async {
    final response = await ApiService.get('/eco-challenges/leaderboard');
    return response['leaderboard'] ?? [];
  }
  
  // Initialize sample data (Admin only)
  static Future<Map<String, dynamic>> initializeSampleData() async {
    return await ApiService.post('/eco-challenges/initialize-sample-data', {});
  }
}
```

### Update Provider
```dart
// lib/providers/eco_challenges_provider.dart (UPDATE EXISTING)
import 'package:flutter/material.dart';
import '../services/backend_eco_challenges_service.dart';

class EcoChallengesProvider with ChangeNotifier {
  List<dynamic> _challenges = [];
  List<dynamic> _userChallenges = [];
  Map<String, dynamic> _userStats = {};
  bool _isLoading = false;
  String? _error;
  
  List<dynamic> get challenges => _challenges;
  List<dynamic> get userChallenges => _userChallenges;
  Map<String, dynamic> get userStats => _userStats;
  bool get isLoading => _isLoading;
  String? get error => _error;
  
  // Load all active challenges from backend
  Future<void> loadActiveChallenges() async {
    _isLoading = true;
    _error = null;
    notifyListeners();
    
    try {
      _challenges = await BackendEcoChallengesService.getActiveChallenges();
      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
    }
  }
  
  // Load user's challenges from backend
  Future<void> loadUserChallenges(String userId) async {
    _isLoading = true;
    _error = null;
    notifyListeners();
    
    try {
      _userChallenges = await BackendEcoChallengesService.getUserChallenges(userId);
      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
    }
  }
  
  // Start a challenge
  Future<bool> startChallenge(String userId, String challengeId) async {
    try {
      final result = await BackendEcoChallengesService.startChallenge(
        userId, 
        challengeId
      );
      
      // Reload user challenges
      await loadUserChallenges(userId);
      
      return true;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
  
  // Update challenge progress
  Future<bool> updateChallengeProgress(
    String userId,
    String progressId, 
    int currentProgress
  ) async {
    try {
      await BackendEcoChallengesService.updateProgress(
        progressId, 
        currentProgress
      );
      
      // Reload user challenges and stats
      await loadUserChallenges(userId);
      await loadUserStats(userId);
      
      return true;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
  
  // Load user statistics
  Future<void> loadUserStats(String userId) async {
    try {
      _userStats = await BackendEcoChallengesService.getUserStats(userId);
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }
}
```

---

## 💚 2. Eco Discounts Integration

### Backend Service
```dart
// lib/services/backend_eco_discounts_service.dart
import 'api_service.dart';

class BackendEcoDiscountsService {
  // Get all active discounts
  static Future<List<dynamic>> getActiveDiscounts() async {
    final response = await ApiService.get('/eco-discounts/active');
    return response['discounts'] ?? [];
  }
  
  // Get applicable discounts for user
  static Future<List<dynamic>> getApplicableDiscounts(
    String userId,
    String category,
    double orderAmount
  ) async {
    final response = await ApiService.get(
      '/eco-discounts/user/$userId/applicable?category=$category&orderAmount=$orderAmount'
    );
    return response['discounts'] ?? [];
  }
  
  // Validate discount code
  static Future<Map<String, dynamic>> validateDiscount(
    String code,
    String userId,
    double orderAmount,
    String? category
  ) async {
    return await ApiService.post('/eco-discounts/validate', {
      'code': code,
      'userId': userId,
      'orderAmount': orderAmount,
      'category': category,
    });
  }
  
  // Apply discount
  static Future<Map<String, dynamic>> applyDiscount(
    String code,
    String userId,
    String orderId,
    double orderAmount
  ) async {
    return await ApiService.post('/eco-discounts/apply', {
      'code': code,
      'userId': userId,
      'orderId': orderId,
      'orderAmount': orderAmount,
    });
  }
  
  // Get user's usage history
  static Future<List<dynamic>> getUserUsageHistory(String userId) async {
    final response = await ApiService.get(
      '/eco-discounts/user/$userId/usage-history'
    );
    return response['usageHistory'] ?? [];
  }
  
  // Get user's usage statistics
  static Future<Map<String, dynamic>> getUserUsageStats(String userId) async {
    return await ApiService.get('/eco-discounts/user/$userId/usage-stats');
  }
  
  // Initialize sample data (Admin only)
  static Future<Map<String, dynamic>> initializeSampleData() async {
    return await ApiService.post('/eco-discounts/initialize-sample-data', {});
  }
}
```

### Update Provider
```dart
// lib/providers/eco_discounts_provider.dart (UPDATE EXISTING)
import 'package:flutter/material.dart';
import '../services/backend_eco_discounts_service.dart';

class EcoDiscountsProvider with ChangeNotifier {
  List<dynamic> _discounts = [];
  List<dynamic> _applicableDiscounts = [];
  Map<String, dynamic>? _appliedDiscount;
  bool _isLoading = false;
  String? _error;
  
  List<dynamic> get discounts => _discounts;
  List<dynamic> get applicableDiscounts => _applicableDiscounts;
  Map<String, dynamic>? get appliedDiscount => _appliedDiscount;
  bool get isLoading => _isLoading;
  String? get error => _error;
  
  // Load active discounts
  Future<void> loadActiveDiscounts() async {
    _isLoading = true;
    _error = null;
    notifyListeners();
    
    try {
      _discounts = await BackendEcoDiscountsService.getActiveDiscounts();
      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
    }
  }
  
  // Load applicable discounts for cart
  Future<void> loadApplicableDiscounts(
    String userId,
    String category,
    double orderAmount
  ) async {
    try {
      _applicableDiscounts = await BackendEcoDiscountsService.getApplicableDiscounts(
        userId, 
        category, 
        orderAmount
      );
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }
  
  // Validate and apply discount
  Future<bool> validateAndApplyDiscount(
    String code,
    String userId,
    String orderId,
    double orderAmount,
    String? category
  ) async {
    try {
      // First validate
      final validationResult = await BackendEcoDiscountsService.validateDiscount(
        code, 
        userId, 
        orderAmount, 
        category
      );
      
      if (validationResult['valid'] == true) {
        // Then apply
        _appliedDiscount = await BackendEcoDiscountsService.applyDiscount(
          code, 
          userId, 
          orderId, 
          orderAmount
        );
        notifyListeners();
        return true;
      } else {
        _error = validationResult['message'] ?? 'Invalid discount code';
        notifyListeners();
        return false;
      }
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
  
  // Clear applied discount
  void clearAppliedDiscount() {
    _appliedDiscount = null;
    notifyListeners();
  }
}
```

---

## 🏆 3. Leaderboard Integration

### Backend Service
```dart
// lib/services/backend_leaderboard_service.dart
import 'api_service.dart';

class BackendLeaderboardService {
  // Get global leaderboard
  static Future<Map<String, dynamic>> getGlobalLeaderboard({
    int page = 0,
    int size = 50
  }) async {
    return await ApiService.get(
      '/leaderboard/global?page=$page&size=$size'
    );
  }
  
  // Get top 10 by eco points
  static Future<List<dynamic>> getTop10ByEcoPoints() async {
    final response = await ApiService.get('/leaderboard/top10/eco-points');
    return response['leaderboard'] ?? [];
  }
  
  // Get top 10 by carbon saved
  static Future<List<dynamic>> getTop10ByCarbonSaved() async {
    final response = await ApiService.get('/leaderboard/top10/carbon-saved');
    return response['leaderboard'] ?? [];
  }
  
  // Get top 10 by challenges
  static Future<List<dynamic>> getTop10ByChallenges() async {
    final response = await ApiService.get('/leaderboard/top10/challenges');
    return response['leaderboard'] ?? [];
  }
  
  // Get user profile
  static Future<Map<String, dynamic>> getUserProfile(String userId) async {
    return await ApiService.get('/leaderboard/profile/$userId');
  }
  
  // Get user ranking info
  static Future<Map<String, dynamic>> getUserRankingInfo(String userId) async {
    return await ApiService.get('/leaderboard/profile/$userId/rank');
  }
  
  // Add eco points
  static Future<Map<String, dynamic>> addEcoPoints(
    String userId,
    int points,
    String reason
  ) async {
    return await ApiService.post(
      '/leaderboard/profile/$userId/add-points',
      {'points': points, 'reason': reason}
    );
  }
  
  // Update streak
  static Future<Map<String, dynamic>> updateStreak(String userId) async {
    return await ApiService.post(
      '/leaderboard/profile/$userId/update-streak',
      {}
    );
  }
  
  // Update carbon saved
  static Future<Map<String, dynamic>> updateCarbonSaved(
    String userId,
    double carbonAmount
  ) async {
    return await ApiService.post(
      '/leaderboard/profile/$userId/update-carbon',
      {'carbonAmount': carbonAmount}
    );
  }
  
  // Get global statistics
  static Future<Map<String, dynamic>> getGlobalStatistics() async {
    return await ApiService.get('/leaderboard/statistics/global');
  }
}
```

### Create/Update Provider
```dart
// lib/providers/leaderboard_provider.dart
import 'package:flutter/material.dart';
import '../services/backend_leaderboard_service.dart';

class LeaderboardProvider with ChangeNotifier {
  List<dynamic> _globalLeaderboard = [];
  Map<String, dynamic>? _userProfile;
  Map<String, dynamic>? _userRanking;
  Map<String, dynamic>? _globalStats;
  bool _isLoading = false;
  String? _error;
  
  List<dynamic> get globalLeaderboard => _globalLeaderboard;
  Map<String, dynamic>? get userProfile => _userProfile;
  Map<String, dynamic>? get userRanking => _userRanking;
  Map<String, dynamic>? get globalStats => _globalStats;
  bool get isLoading => _isLoading;
  String? get error => _error;
  
  // Load global leaderboard
  Future<void> loadGlobalLeaderboard({int page = 0, int size = 50}) async {
    _isLoading = true;
    _error = null;
    notifyListeners();
    
    try {
      final response = await BackendLeaderboardService.getGlobalLeaderboard(
        page: page,
        size: size
      );
      _globalLeaderboard = response['leaderboard'] ?? [];
      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
    }
  }
  
  // Load user profile
  Future<void> loadUserProfile(String userId) async {
    try {
      _userProfile = await BackendLeaderboardService.getUserProfile(userId);
      _userRanking = await BackendLeaderboardService.getUserRankingInfo(userId);
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }
  
  // Load global statistics
  Future<void> loadGlobalStatistics() async {
    try {
      _globalStats = await BackendLeaderboardService.getGlobalStatistics();
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }
  
  // Add eco points to user
  Future<bool> addEcoPoints(String userId, int points, String reason) async {
    try {
      await BackendLeaderboardService.addEcoPoints(userId, points, reason);
      await loadUserProfile(userId);
      return true;
    } catch (e) {
      _error = e.toString();
      notifyListeners();
      return false;
    }
  }
}
```

---

## 🌍 4. Carbon Footprint Integration

### Backend Service
```dart
// lib/services/backend_carbon_footprint_service.dart
import 'api_service.dart';

class BackendCarbonFootprintService {
  // Calculate product carbon footprint
  static Future<Map<String, dynamic>> calculateFootprint(
    Map<String, dynamic> productData
  ) async {
    return await ApiService.post('/carbon-footprint/calculate', productData);
  }
  
  // Get user's carbon history
  static Future<List<dynamic>> getUserHistory(String userId) async {
    final response = await ApiService.get(
      '/carbon-footprint/user/$userId/history'
    );
    return response['history'] ?? [];
  }
  
  // Get user's carbon statistics
  static Future<Map<String, dynamic>> getUserStatistics(String userId) async {
    return await ApiService.get('/carbon-footprint/user/$userId/statistics');
  }
  
  // Initialize emission factors (Admin only)
  static Future<Map<String, dynamic>> initializeEmissionFactors() async {
    return await ApiService.post(
      '/carbon-footprint/initialize-emission-factors',
      {}
    );
  }
  
  // Health check
  static Future<Map<String, dynamic>> healthCheck() async {
    return await ApiService.get('/carbon-footprint/health');
  }
}
```

### Update Provider
```dart
// lib/providers/carbon_tracking_provider.dart (UPDATE EXISTING)
import 'package:flutter/material.dart';
import '../services/backend_carbon_footprint_service.dart';

class CarbonTrackingProvider with ChangeNotifier {
  Map<String, dynamic>? _currentFootprint;
  List<dynamic> _history = [];
  Map<String, dynamic>? _statistics;
  bool _isLoading = false;
  String? _error;
  
  Map<String, dynamic>? get currentFootprint => _currentFootprint;
  List<dynamic> get history => _history;
  Map<String, dynamic>? get statistics => _statistics;
  bool get isLoading => _isLoading;
  String? get error => _error;
  
  // Calculate carbon footprint for product
  Future<Map<String, dynamic>?> calculateProductFootprint({
    required String productName,
    required String category,
    required double weight,
    required String material,
    required String manufacturingType,
    required double transportationDistance,
    required String transportationType,
    required String packagingType,
    String? userId,
    String? orderId,
    String? productId,
    bool isRecycled = false,
    bool isOrganic = false,
    double? productLifespan,
    String? sourceCountry,
  }) async {
    _isLoading = true;
    _error = null;
    notifyListeners();
    
    try {
      final productData = {
        'productName': productName,
        'category': category,
        'weight': weight,
        'material': material,
        'manufacturingType': manufacturingType,
        'transportationDistance': transportationDistance,
        'transportationType': transportationType,
        'packagingType': packagingType,
        'userId': userId,
        'orderId': orderId,
        'productId': productId,
        'isRecycled': isRecycled,
        'isOrganic': isOrganic,
        'productLifespan': productLifespan,
        'sourceCountry': sourceCountry,
      };
      
      _currentFootprint = await BackendCarbonFootprintService.calculateFootprint(
        productData
      );
      
      _isLoading = false;
      notifyListeners();
      
      return _currentFootprint;
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
      return null;
    }
  }
  
  // Load user's carbon history
  Future<void> loadUserHistory(String userId) async {
    try {
      _history = await BackendCarbonFootprintService.getUserHistory(userId);
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }
  
  // Load user's carbon statistics
  Future<void> loadUserStatistics(String userId) async {
    try {
      _statistics = await BackendCarbonFootprintService.getUserStatistics(userId);
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      notifyListeners();
    }
  }
  
  // Calculate cart carbon footprint
  Future<Map<String, dynamic>?> calculateCartFootprint(
    List<Map<String, dynamic>> cartItems,
    String userId
  ) async {
    double totalFootprint = 0.0;
    double totalSavings = 0.0;
    List<Map<String, dynamic>> itemResults = [];
    
    for (var item in cartItems) {
      final result = await calculateProductFootprint(
        productName: item['name'] ?? 'Product',
        category: item['category'] ?? 'General',
        weight: (item['weight'] ?? 1.0).toDouble(),
        material: item['material'] ?? 'conventional_cotton',
        manufacturingType: item['manufacturingType'] ?? 'conventional',
        transportationDistance: (item['transportationDistance'] ?? 100.0).toDouble(),
        transportationType: item['transportationType'] ?? 'truck_local',
        packagingType: item['packagingType'] ?? 'recycled_cardboard',
        userId: userId,
        productId: item['id'],
        isRecycled: item['isRecycled'] ?? false,
        isOrganic: item['isOrganic'] ?? false,
      );
      
      if (result != null) {
        totalFootprint += result['totalCarbonFootprint'] ?? 0.0;
        totalSavings += result['carbonSavings'] ?? 0.0;
        itemResults.add(result);
      }
    }
    
    return {
      'totalCarbonFootprint': totalFootprint,
      'totalCarbonSavings': totalSavings,
      'items': itemResults,
      'averageEcoRating': _calculateAverageRating(itemResults),
    };
  }
  
  String _calculateAverageRating(List<Map<String, dynamic>> items) {
    if (items.isEmpty) return 'C';
    
    Map<String, int> ratingScores = {
      'A+': 7, 'A': 6, 'B+': 5, 'B': 4, 'C': 3, 'D': 2, 'F': 1
    };
    
    int totalScore = 0;
    for (var item in items) {
      String rating = item['ecoRating'] ?? 'C';
      totalScore += ratingScores[rating] ?? 3;
    }
    
    int avgScore = (totalScore / items.length).round();
    
    return ratingScores.entries
        .firstWhere((entry) => entry.value == avgScore, 
                    orElse: () => MapEntry('C', 3))
        .key;
  }
}
```

---

## 📱 5. Update Main App Configuration

```dart
// lib/main.dart (UPDATE)
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'providers/eco_challenges_provider.dart';
import 'providers/eco_discounts_provider.dart';
import 'providers/leaderboard_provider.dart';
import 'providers/carbon_tracking_provider.dart';

void main() {
  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => EcoChallengesProvider()),
        ChangeNotifierProvider(create: (_) => EcoDiscountsProvider()),
        ChangeNotifierProvider(create: (_) => LeaderboardProvider()),
        ChangeNotifierProvider(create: (_) => CarbonTrackingProvider()),
        // ... other providers
      ],
      child: MyApp(),
    ),
  );
}
```

---

## 🧪 Testing Integration

### Test Script
```dart
// lib/test_backend_integration.dart
import 'services/backend_eco_challenges_service.dart';
import 'services/backend_eco_discounts_service.dart';
import 'services/backend_leaderboard_service.dart';
import 'services/backend_carbon_footprint_service.dart';

Future<void> testBackendIntegration() async {
  print('🧪 Testing Backend Integration...\n');
  
  // Test 1: Eco Challenges
  try {
    print('1️⃣ Testing Eco Challenges...');
    final challenges = await BackendEcoChallengesService.getActiveChallenges();
    print('✅ Found ${challenges.length} active challenges');
  } catch (e) {
    print('❌ Eco Challenges Error: $e');
  }
  
  // Test 2: Eco Discounts
  try {
    print('\n2️⃣ Testing Eco Discounts...');
    final discounts = await BackendEcoDiscountsService.getActiveDiscounts();
    print('✅ Found ${discounts.length} active discounts');
  } catch (e) {
    print('❌ Eco Discounts Error: $e');
  }
  
  // Test 3: Leaderboard
  try {
    print('\n3️⃣ Testing Leaderboard...');
    final leaderboard = await BackendLeaderboardService.getGlobalLeaderboard();
    print('✅ Leaderboard loaded: ${leaderboard['totalElements']} users');
  } catch (e) {
    print('❌ Leaderboard Error: $e');
  }
  
  // Test 4: Carbon Footprint
  try {
    print('\n4️⃣ Testing Carbon Footprint...');
    final health = await BackendCarbonFootprintService.healthCheck();
    print('✅ Carbon service status: ${health['status']}');
  } catch (e) {
    print('❌ Carbon Footprint Error: $e');
  }
  
  print('\n✅ Integration testing complete!');
}
```

---

## 🚀 Deployment Checklist

### Before Production:
- [ ] Update `ApiConfig.BASE_URL` to production URL
- [ ] Test all API endpoints
- [ ] Add error handling and retry logic
- [ ] Implement caching for frequently accessed data
- [ ] Add loading indicators
- [ ] Test offline scenarios
- [ ] Add analytics tracking
- [ ] Implement proper authentication headers
- [ ] Test with real data
- [ ] Performance optimization

### Environment Setup:
```dart
// .env file
BACKEND_BASE_URL=https://your-railway-app.railway.app
API_TIMEOUT=30000
ENABLE_LOGGING=true
```

---

## 📊 API Response Examples

### Eco Challenge Response
```json
{
  "id": 1,
  "challengeId": "CHALLENGE_20251002_001",
  "title": "Plastic-Free Week",
  "targetValue": 7,
  "rewardPoints": 500,
  "difficulty": "EASY"
}
```

### Carbon Footprint Response
```json
{
  "totalCarbonFootprint": 2.385,
  "carbonSavings": 1.215,
  "ecoRating": "B+",
  "equivalentImpacts": {
    "trees_planted": 0.119,
    "car_km_avoided": 10.97
  }
}
```

---

**Ab aapka Flutter app complete backend se connected ho jayega! 🔗🚀**
