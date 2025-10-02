# 🔄 Quick Frontend Update Guide - Existing Files

## 📋 Files Jo Update Karni Hain

### 1. API Configuration File
**Create:** `lib/config/api_config.dart`

```dart
class ApiConfig {
  static const String BASE_URL = 'http://localhost:8080/api';
  
  // Endpoints
  static const String ECO_CHALLENGES = '$BASE_URL/eco-challenges';
  static const String ECO_DISCOUNTS = '$BASE_URL/eco-discounts';
  static const String LEADERBOARD = '$BASE_URL/leaderboard';
  static const String CARBON_FOOTPRINT = '$BASE_URL/carbon-footprint';
}
```

---

### 2. Update Existing Providers

#### A. `lib/providers/eco_challenges_provider.dart`

**ADD these imports:**
```dart
import 'package:http/http.dart' as http;
import 'dart:convert';
import '../config/api_config.dart';
```

**REPLACE Firebase calls with Backend calls:**

```dart
// OLD (Firebase):
// await FirebaseFirestore.instance.collection('eco_challenges').get();

// NEW (Backend):
Future<void> loadActiveChallenges() async {
  final response = await http.get(
    Uri.parse('${ApiConfig.ECO_CHALLENGES}/active'),
  );
  
  if (response.statusCode == 200) {
    final data = json.decode(response.body);
    _challenges = data['challenges'] ?? [];
    notifyListeners();
  }
}

// Start challenge
Future<bool> startChallenge(String userId, String challengeId) async {
  final response = await http.post(
    Uri.parse('${ApiConfig.ECO_CHALLENGES}/$challengeId/start'),
    headers: {'Content-Type': 'application/json'},
    body: json.encode({'userId': userId}),
  );
  
  return response.statusCode == 200;
}

// Update progress
Future<bool> updateProgress(String progressId, int currentProgress) async {
  final response = await http.put(
    Uri.parse('${ApiConfig.ECO_CHALLENGES}/progress/$progressId'),
    headers: {'Content-Type': 'application/json'},
    body: json.encode({'currentProgress': currentProgress}),
  );
  
  return response.statusCode == 200;
}
```

---

#### B. `lib/providers/cart_provider.dart`

**ADD discount validation:**

```dart
import '../config/api_config.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

// ADD this method:
Future<bool> applyDiscountCode(String code, String userId) async {
  try {
    // Validate discount
    final response = await http.post(
      Uri.parse('${ApiConfig.ECO_DISCOUNTS}/validate'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode({
        'code': code,
        'userId': userId,
        'orderAmount': totalAmount,
        'category': 'General',
      }),
    );
    
    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      if (data['valid'] == true) {
        _appliedDiscount = data;
        notifyListeners();
        return true;
      }
    }
    return false;
  } catch (e) {
    print('Error applying discount: $e');
    return false;
  }
}

// Calculate discount amount
double get discountAmount {
  if (_appliedDiscount == null) return 0.0;
  
  final type = _appliedDiscount!['discountType'];
  final value = _appliedDiscount!['discountValue'];
  
  if (type == 'PERCENTAGE') {
    return totalAmount * (value / 100);
  } else if (type == 'FIXED_AMOUNT') {
    return value.toDouble();
  }
  
  return 0.0;
}

// Get final amount after discount
double get finalAmount {
  return totalAmount - discountAmount;
}
```

---

#### C. `lib/providers/carbon_tracking_provider.dart`

**REPLACE dummy calculations with real backend API:**

```dart
import '../config/api_config.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

// REPLACE existing calculation method:
Future<Map<String, dynamic>?> calculateProductFootprint({
  required String productName,
  required String category,
  required double weight,
  required String material,
  String? userId,
  String? productId,
}) async {
  try {
    final response = await http.post(
      Uri.parse('${ApiConfig.CARBON_FOOTPRINT}/calculate'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode({
        'productName': productName,
        'category': category,
        'weight': weight,
        'material': material,
        'manufacturingType': 'eco_friendly',
        'transportationDistance': 150.0,
        'transportationType': 'truck_local',
        'packagingType': 'biodegradable_packaging',
        'userId': userId,
        'productId': productId,
        'isRecycled': false,
        'isOrganic': true,
      }),
    );
    
    if (response.statusCode == 200) {
      return json.decode(response.body);
    }
    return null;
  } catch (e) {
    print('Error calculating footprint: $e');
    return null;
  }
}

// Get user statistics
Future<Map<String, dynamic>?> getUserStatistics(String userId) async {
  try {
    final response = await http.get(
      Uri.parse('${ApiConfig.CARBON_FOOTPRINT}/user/$userId/statistics'),
    );
    
    if (response.statusCode == 200) {
      return json.decode(response.body);
    }
    return null;
  } catch (e) {
    print('Error getting statistics: $e');
    return null;
  }
}
```

---

### 3. Update Product Display Screens

#### `lib/screens/shopping/product_detail_screen.dart`

**ADD real-time carbon calculation:**

```dart
// In initState or when product loads:
@override
void initState() {
  super.initState();
  _loadProductCarbonFootprint();
}

Future<void> _loadProductCarbonFootprint() async {
  final carbonProvider = Provider.of<CarbonTrackingProvider>(
    context, 
    listen: false
  );
  
  final result = await carbonProvider.calculateProductFootprint(
    productName: widget.product.name,
    category: widget.product.category,
    weight: widget.product.weight ?? 1.0,
    material: widget.product.material ?? 'organic_cotton',
    userId: currentUserId,
    productId: widget.product.id,
  );
  
  setState(() {
    _carbonFootprint = result;
  });
}

// Display carbon info:
Widget _buildCarbonInfo() {
  if (_carbonFootprint == null) {
    return CircularProgressIndicator();
  }
  
  return Column(
    children: [
      // Eco Rating Badge
      Container(
        padding: EdgeInsets.all(8),
        decoration: BoxDecoration(
          color: _getRatingColor(_carbonFootprint!['ecoRating']),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Text(
          'Eco Rating: ${_carbonFootprint!['ecoRating']}',
          style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
        ),
      ),
      
      SizedBox(height: 16),
      
      // Carbon Footprint
      Text(
        'Carbon Footprint: ${_carbonFootprint!['totalCarbonFootprint'].toStringAsFixed(2)} kg CO₂e',
        style: TextStyle(fontSize: 16),
      ),
      
      // Savings
      if (_carbonFootprint!['carbonSavings'] > 0)
        Text(
          '✅ Saves ${_carbonFootprint!['carbonSavings'].toStringAsFixed(2)} kg CO₂e',
          style: TextStyle(color: Colors.green, fontSize: 14),
        ),
      
      SizedBox(height: 16),
      
      // Environmental Equivalents
      Wrap(
        spacing: 16,
        children: [
          _buildEquivalent(
            '🌳', 
            '${_carbonFootprint!['equivalentImpacts']['trees_planted'].toStringAsFixed(2)} trees'
          ),
          _buildEquivalent(
            '🚗', 
            '${_carbonFootprint!['equivalentImpacts']['car_km_avoided'].toStringAsFixed(1)} km avoided'
          ),
        ],
      ),
    ],
  );
}

Color _getRatingColor(String rating) {
  switch (rating) {
    case 'A+': return Colors.green[800]!;
    case 'A': return Colors.green;
    case 'B+': return Colors.lightGreen;
    case 'B': return Colors.lime;
    case 'C': return Colors.yellow[700]!;
    case 'D': return Colors.orange;
    case 'F': return Colors.red;
    default: return Colors.grey;
  }
}
```

---

### 4. Update Cart Screen

#### `lib/screens/shopping/cart_screen.dart`

**ADD discount code input:**

```dart
// Add discount code field:
Widget _buildDiscountSection(BuildContext context) {
  final cartProvider = Provider.of<CartProvider>(context);
  
  return Card(
    child: Padding(
      padding: EdgeInsets.all(16),
      child: Column(
        children: [
          TextField(
            controller: _discountController,
            decoration: InputDecoration(
              labelText: 'Discount Code',
              hintText: 'Enter code (e.g., ECO10)',
              suffixIcon: IconButton(
                icon: Icon(Icons.check),
                onPressed: () => _applyDiscount(context),
              ),
            ),
          ),
          
          if (cartProvider.appliedDiscount != null) ...[
            SizedBox(height: 8),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  '${cartProvider.appliedDiscount!['code']} Applied',
                  style: TextStyle(color: Colors.green),
                ),
                Text(
                  '-₹${cartProvider.discountAmount.toStringAsFixed(2)}',
                  style: TextStyle(
                    color: Colors.green,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
          ],
        ],
      ),
    ),
  );
}

Future<void> _applyDiscount(BuildContext context) async {
  final cartProvider = Provider.of<CartProvider>(context, listen: false);
  final userId = 'user123'; // Get from auth
  
  final success = await cartProvider.applyDiscountCode(
    _discountController.text,
    userId,
  );
  
  if (success) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('✅ Discount applied!')),
    );
  } else {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('❌ Invalid discount code')),
    );
  }
}

// Update price summary:
Widget _buildPriceBreakdown(BuildContext context) {
  final cartProvider = Provider.of<CartProvider>(context);
  
  return Column(
    children: [
      _buildPriceRow('Subtotal', cartProvider.totalAmount),
      
      if (cartProvider.appliedDiscount != null)
        _buildPriceRow(
          'Discount (${cartProvider.appliedDiscount!['code']})', 
          -cartProvider.discountAmount,
          color: Colors.green,
        ),
      
      Divider(),
      
      _buildPriceRow(
        'Total', 
        cartProvider.finalAmount,
        isBold: true,
      ),
    ],
  );
}
```

---

### 5. Create Leaderboard Screen

#### `lib/screens/eco_challenges/leaderboard_screen.dart`

```dart
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../providers/leaderboard_provider.dart';

class LeaderboardScreen extends StatefulWidget {
  @override
  _LeaderboardScreenState createState() => _LeaderboardScreenState();
}

class _LeaderboardScreenState extends State<LeaderboardScreen> {
  @override
  void initState() {
    super.initState();
    _loadLeaderboard();
  }
  
  Future<void> _loadLeaderboard() async {
    final provider = Provider.of<LeaderboardProvider>(context, listen: false);
    await provider.loadGlobalLeaderboard();
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('🏆 Eco Leaderboard'),
      ),
      body: Consumer<LeaderboardProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading) {
            return Center(child: CircularProgressIndicator());
          }
          
          if (provider.error != null) {
            return Center(
              child: Text('Error: ${provider.error}'),
            );
          }
          
          return ListView.builder(
            itemCount: provider.globalLeaderboard.length,
            itemBuilder: (context, index) {
              final user = provider.globalLeaderboard[index];
              return _buildLeaderboardTile(user, index + 1);
            },
          );
        },
      ),
    );
  }
  
  Widget _buildLeaderboardTile(Map<String, dynamic> user, int rank) {
    return Card(
      margin: EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: _getRankColor(rank),
          child: Text(
            '#$rank',
            style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold),
          ),
        ),
        title: Text(
          user['userName'] ?? 'User',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('Level: ${user['ecoLevel']} - ${user['levelName']}'),
            Text('Carbon Saved: ${user['totalCarbonSaved']?.toStringAsFixed(2)} kg CO₂e'),
          ],
        ),
        trailing: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(
              '${user['totalEcoPoints']} pts',
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
                color: Colors.green,
              ),
            ),
            Text(
              '${user['totalChallengesCompleted']} challenges',
              style: TextStyle(fontSize: 12),
            ),
          ],
        ),
      ),
    );
  }
  
  Color _getRankColor(int rank) {
    if (rank == 1) return Colors.amber;
    if (rank == 2) return Colors.grey[400]!;
    if (rank == 3) return Colors.brown[300]!;
    return Colors.blue;
  }
}
```

---

### 6. Initialize Sample Data (One-time Setup)

#### Create: `lib/utils/backend_initializer.dart`

```dart
import 'package:http/http.dart' as http;
import '../config/api_config.dart';

class BackendInitializer {
  static Future<void> initializeBackendData() async {
    print('🚀 Initializing backend data...');
    
    try {
      // Initialize Eco Challenges
      await http.post(
        Uri.parse('${ApiConfig.ECO_CHALLENGES}/initialize-sample-data'),
      );
      print('✅ Eco Challenges initialized');
      
      // Initialize Eco Discounts
      await http.post(
        Uri.parse('${ApiConfig.ECO_DISCOUNTS}/initialize-sample-data'),
      );
      print('✅ Eco Discounts initialized');
      
      // Initialize Emission Factors
      await http.post(
        Uri.parse('${ApiConfig.CARBON_FOOTPRINT}/initialize-emission-factors'),
      );
      print('✅ Emission Factors initialized');
      
      print('🎉 Backend initialization complete!');
    } catch (e) {
      print('❌ Backend initialization error: $e');
    }
  }
}
```

**Call in main.dart:**
```dart
void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  // Initialize backend data (first time only)
  // await BackendInitializer.initializeBackendData();
  
  runApp(MyApp());
}
```

---

## 🔄 Step-by-Step Update Process

### Step 1: Install Dependencies
```bash
cd "C:\Users\AkashK\Desktop\New folder\EcoBazaarX"
flutter pub add http
flutter pub get
```

### Step 2: Create Config Files
- ✅ Create `lib/config/api_config.dart`

### Step 3: Update Providers (One by One)
- ✅ Update `eco_challenges_provider.dart`
- ✅ Update `cart_provider.dart`
- ✅ Update `carbon_tracking_provider.dart`
- ✅ Create `leaderboard_provider.dart`

### Step 4: Update Screens
- ✅ Update `product_detail_screen.dart`
- ✅ Update `cart_screen.dart`
- ✅ Create `leaderboard_screen.dart`

### Step 5: Test Integration
- ✅ Start backend: `.\mvnw.cmd spring-boot:run`
- ✅ Run Flutter app: `flutter run`
- ✅ Test all features

---

## 🧪 Quick Test Checklist

```
[ ] Backend server running on localhost:8080
[ ] API config file created
[ ] HTTP package installed
[ ] Eco challenges loading from backend
[ ] Discount codes validating
[ ] Carbon footprint calculating
[ ] Leaderboard displaying
[ ] No Firebase conflicts
[ ] Error handling working
[ ] Loading indicators showing
```

---

## 📝 Important Notes

1. **Dual Mode:** Aap Firebase aur Backend dono use kar sakte ho simultaneously
2. **Gradual Migration:** Ek feature ek time migrate karo
3. **Error Handling:** Har API call me try-catch use karo
4. **Loading States:** User ko feedback do jab data load ho raha ho
5. **Offline Support:** Bad network pe gracefully handle karo

---

## 🎯 Priority Order

1. **High Priority:**
   - ✅ Carbon Footprint (real calculations chahiye)
   - ✅ Eco Discounts (order placement me use hoga)

2. **Medium Priority:**
   - ✅ Eco Challenges (user engagement ke liye)
   - ✅ Leaderboard (gamification)

3. **Low Priority:**
   - Analytics integration
   - Advanced features

---

**Yeh guide follow karo aur step-by-step frontend ko backend se connect karo! 🚀**
