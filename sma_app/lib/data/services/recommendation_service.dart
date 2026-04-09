import 'package:flutter/foundation.dart';
import '../models/menu_item_model.dart';
import '../models/sma_preference_model.dart';
import 'api_service.dart';
import '../../core/constants/api_constants.dart';

class RecommendationService {
  final ApiService _apiService = ApiService();

  Future<List<MenuItem>> getMealRecommendations({
    required int userId,
    required MealType mealType,
    int count = 5,
  }) async {
    try {
      final response = await _apiService.get(
        '${ApiConstants.recommendations}/meal/batch',
        queryParameters: {
          'mealType': mealType.backendValue,
          'count': count.toString(),
        },
        headers: {'X-User-Id': userId.toString()},
      );

      debugPrint('Recommendations response: $response');

      if (response['success'] == true) {
        final data = response['data'];
        if (data != null && data['recommendations'] != null) {
          final List<dynamic> items = data['recommendations'];
          return items.map((json) => MenuItem.fromJson(json)).toList();
        }
      }
      if (response['message'] != null) {
        debugPrint('Backend message: ${response['message']}');
      }
      return [];
    } catch (e) {
      debugPrint('Error getting recommendations: $e');
      return [];
    }
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
