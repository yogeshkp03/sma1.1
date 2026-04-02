import 'package:flutter/foundation.dart';
import '../models/menu_item_model.dart';
import '../models/sma_preference_model.dart';
import 'api_service.dart';
import '../../core/constants/api_constants.dart';

class RecommendationService {
  String _mapMealTypeToBackend(MealType mealType) {
    switch (mealType) {
      case MealType.breakfast:
        return 'MORNING_FUEL';
      case MealType.lunch:
        return 'POWER_HOUR';
      case MealType.dinner:
        return 'TWILIGHT_FEAST';
      case MealType.snacks:
        return 'CRAVE_CORNER';
    }
  }

  final ApiService _apiService = ApiService();

  Future<List<MenuItem>> getMealRecommendations({
    required int userId,
    required MealType mealType,
    int count = 5,
  }) async {
    List<MenuItem> recommendations = [];

    try {
      for (int i = 0; i < count; i++) {
        try {
          final response = await _apiService.get(
            '${ApiConstants.recommendations}/meal',
            queryParameters: {
              'mealType': _mapMealTypeToBackend(mealType),
            },
            headers: {'X-User-Id': userId.toString()},
          );

          debugPrint('Recommendation $i response: $response');

          if (response['success'] == true) {
            final data = response['data'];
            if (data != null && data['menuItem'] != null) {
              final item = MenuItem.fromJson(data['menuItem']);
              if (!recommendations.any((r) => r.id == item.id)) {
                recommendations.add(item);
              }
            }
          }
          if (response['message'] != null) {
            debugPrint('Backend message: ${response['message']}');
          }
        } catch (e) {
          debugPrint('Error getting recommendation $i: $e');
        }
      }
    } catch (e) {
      debugPrint('Error getting recommendations: $e');
    }

    return recommendations;
  }

  Future<MenuItem?> getMealRecommendation({
    required int userId,
    required MealType mealType,
  }) async {
    final recommendations = await getMealRecommendations(
      userId: userId,
      mealType: mealType,
      count: 1,
    );
    return recommendations.isNotEmpty ? recommendations.first : null;
  }

  Future<List<RecommendationHistory>> getRecommendationHistory(
      int userId) async {
    try {
      final response = await _apiService.get(
        '${ApiConstants.recommendations}/history',
        headers: {'X-User-Id': userId.toString()},
      );

      if (response['success'] == true) {
        final List<dynamic> data = response['data'];
        return data
            .map((json) => RecommendationHistory.fromJson(json))
            .toList();
      }
      return [];
    } catch (e) {
      debugPrint('Error getting history: $e');
      return [];
    }
  }

  Future<bool> isSmaEnabled(int userId) async {
    try {
      final response = await _apiService.get(
        '${ApiConstants.recommendations}/sma-status',
        headers: {'X-User-Id': userId.toString()},
      );

      if (response['success'] == true) {
        return response['data']['isEnabled'] ?? false;
      }
      return false;
    } catch (e) {
      return false;
    }
  }
}

class RecommendationHistory {
  final int id;
  final String menuItemName;
  final String restaurantName;
  final String mealType;
  final int? calories;
  final DateTime recommendedAt;

  RecommendationHistory({
    required this.id,
    required this.menuItemName,
    required this.restaurantName,
    required this.mealType,
    this.calories,
    required this.recommendedAt,
  });

  factory RecommendationHistory.fromJson(Map<String, dynamic> json) {
    return RecommendationHistory(
      id: json['id'] ?? 0,
      menuItemName: json['menuItemName'] ?? '',
      restaurantName: json['restaurantName'] ?? '',
      mealType: json['mealType'] ?? '',
      calories: json['calories'],
      recommendedAt: json['recommendedAt'] != null
          ? DateTime.parse(json['recommendedAt'])
          : DateTime.now(),
    );
  }
}
