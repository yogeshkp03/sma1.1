import 'package:flutter/foundation.dart';
import '../models/sma_preference_model.dart';
import '../services/api_service.dart';
import '../../core/constants/api_constants.dart';

class SmaRepository {
  final ApiService _apiService = ApiService();

  Future<List<SmaPreference>> getPreferences(int userId) async {
    try {
      final response = await _apiService.get(
        '${ApiConstants.smaPreferences}/user/$userId',
        headers: {'X-User-Id': userId.toString()},
      );

      debugPrint('getPreferences response: $response');

      if (response['success'] == true) {
        final data = response['data'];
        if (data != null && data is List) {
          return data.map((json) => SmaPreference.fromJson(json)).toList();
        }
      }
      if (response['message'] != null) {
        debugPrint('getPreferences error: ${response['message']}');
      }
      return [];
    } catch (e) {
      debugPrint('Exception getPreferences: $e');
      throw 'Failed to fetch SMA preferences: $e';
    }
  }

  Future<List<SmaPreference>> getActivePreferences(int userId) async {
    try {
      final response = await _apiService.get(
        '${ApiConstants.smaPreferences}/user/$userId/active',
        headers: {'X-User-Id': userId.toString()},
      );

      debugPrint('getActivePreferences response: $response');

      if (response['success'] == true) {
        final data = response['data'];
        if (data != null && data is List) {
          return data.map((json) => SmaPreference.fromJson(json)).toList();
        }
      }
      if (response['message'] != null) {
        debugPrint('getActivePreferences error: ${response['message']}');
      }
      return [];
    } catch (e) {
      debugPrint('Exception getActivePreferences: $e');
      throw 'Failed to fetch active SMA preferences: $e';
    }
  }

  Future<SmaPreference> createPreference(
    int userId,
    SmaPreference preference,
  ) async {
    try {
      debugPrint('Creating preference with data: ${preference.toJson()}');

      final response = await _apiService.post(
        '${ApiConstants.smaPreferences}/user/$userId',
        data: preference.toJson(),
      );

      debugPrint('createPreference response: $response');

      if (response['success'] == true) {
        final data = response['data'];
        if (data != null) {
          return SmaPreference.fromJson(data);
        }
      }
      final errorMsg = response['message'] ?? 'Failed to create preference';
      debugPrint('createPreference error: $errorMsg');
      throw errorMsg;
    } catch (e) {
      debugPrint('Exception createPreference: $e');
      throw 'Failed to create SMA preference: $e';
    }
  }

  Future<SmaPreference> updatePreference(
    int userId,
    int preferenceId,
    SmaPreference preference,
  ) async {
    try {
      final response = await _apiService.put(
        '${ApiConstants.smaPreferences}/$preferenceId/user/$userId',
        data: preference.toJson(),
      );

      debugPrint('updatePreference response: $response');

      if (response['success'] == true) {
        final data = response['data'];
        if (data != null) {
          return SmaPreference.fromJson(data);
        }
      }
      throw response['message'] ?? 'Failed to update preference';
    } catch (e) {
      throw 'Failed to update SMA preference: $e';
    }
  }

  Future<void> togglePreference(
    int userId,
    int preferenceId,
    bool active,
  ) async {
    try {
      final response = await _apiService.patch(
        '${ApiConstants.smaPreferences}/$preferenceId/user/$userId/toggle?active=$active',
      );

      debugPrint('togglePreference response: $response');

      if (response['success'] != true) {
        throw response['message'] ?? 'Failed to toggle preference';
      }
    } catch (e) {
      throw 'Failed to toggle SMA preference: $e';
    }
  }

  Future<void> deletePreference(int userId, int preferenceId) async {
    try {
      final response = await _apiService.delete(
        '${ApiConstants.smaPreferences}/$preferenceId/user/$userId',
      );

      debugPrint('deletePreference response: $response');

      if (response['success'] != true) {
        throw response['message'] ?? 'Failed to delete preference';
      }
    } catch (e) {
      throw 'Failed to delete SMA preference: $e';
    }
  }
}
