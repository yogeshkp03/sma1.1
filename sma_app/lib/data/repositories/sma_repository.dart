import 'package:flutter/foundation.dart';
import '../models/sma_preference_model.dart';
import '../services/api_service.dart';
import '../services/cache_service.dart';
import '../../core/constants/api_constants.dart';

class SmaRepository {
  final ApiService _apiService = ApiService();
  CacheService? _cache;
  bool _cacheInitialized = false;

  static const Duration _preferencesCacheTtl = Duration(days: 365);

  Future<void> _ensureCacheInitialized() async {
    if (!_cacheInitialized) {
      _cache = await CacheService.getInstance();
      _cacheInitialized = true;
    }
  }

  Future<List<SmaPreference>> getPreferences(int userId,
      {bool forceRefresh = false}) async {
    await _ensureCacheInitialized();

    final cacheKey = 'sma_prefs_$userId';

    if (!forceRefresh && _cache != null) {
      final cached = _cache!.get(cacheKey);
      if (cached != null) {
        try {
          final data = cached['data'] as List?;
          if (data != null) {
            debugPrint('Returning cached preferences for user $userId');
            return data.map((json) => SmaPreference.fromJson(json)).toList();
          }
        } catch (e) {
          debugPrint('Cache parse error: $e');
        }
      }
    }

    try {
      final response = await _apiService.get(
        '${ApiConstants.smaPreferences}/user/$userId',
        headers: {'X-User-Id': userId.toString()},
      );

      debugPrint('getPreferences response: $response');

      if (response['success'] == true) {
        final data = response['data'];
        if (data != null && data is List) {
          final preferences =
              data.map((json) => SmaPreference.fromJson(json)).toList();

          if (_cache != null) {
            await _cache!.set(
                cacheKey,
                {
                  'data': data,
                },
                ttl: _preferencesCacheTtl);
            debugPrint('Cached preferences: ${preferences.length} items');
          }

          return preferences;
        }
      }
      if (response['message'] != null) {
        debugPrint('getPreferences error: ${response['message']}');
      }
      return [];
    } catch (e) {
      debugPrint('Network error, trying cache: $e');

      if (_cache != null) {
        final cached = _cache!.get(cacheKey);
        if (cached != null) {
          try {
            final data = cached['data'] as List?;
            if (data != null) {
              debugPrint('Returning stale cached preferences');
              return data.map((json) => SmaPreference.fromJson(json)).toList();
            }
          } catch (e) {
            debugPrint('Cache parse error: $e');
          }
        }
      }

      debugPrint('Exception getPreferences: $e');
      throw 'Failed to fetch SMA preferences: $e';
    }
  }

  Future<List<SmaPreference>> getActivePreferences(int userId,
      {bool forceRefresh = false}) async {
    await _ensureCacheInitialized();

    final cacheKey = 'sma_active_prefs_$userId';

    if (!forceRefresh && _cache != null) {
      final cached = _cache!.get(cacheKey);
      if (cached != null) {
        try {
          final data = cached['data'] as List?;
          if (data != null) {
            debugPrint('Returning cached active preferences for user $userId');
            return data.map((json) => SmaPreference.fromJson(json)).toList();
          }
        } catch (e) {
          debugPrint('Cache parse error: $e');
        }
      }
    }

    try {
      final response = await _apiService.get(
        '${ApiConstants.smaPreferences}/user/$userId/active',
        headers: {'X-User-Id': userId.toString()},
      );

      debugPrint('getActivePreferences response: $response');

      if (response['success'] == true) {
        final data = response['data'];
        if (data != null && data is List) {
          final preferences =
              data.map((json) => SmaPreference.fromJson(json)).toList();

          if (_cache != null) {
            await _cache!.set(
                cacheKey,
                {
                  'data': data,
                },
                ttl: _preferencesCacheTtl);
            debugPrint(
                'Cached active preferences: ${preferences.length} items');
          }

          return preferences;
        }
      }
      if (response['message'] != null) {
        debugPrint('getActivePreferences error: ${response['message']}');
      }
      return [];
    } catch (e) {
      debugPrint('Network error, trying cache: $e');

      if (_cache != null) {
        final cached = _cache!.get(cacheKey);
        if (cached != null) {
          try {
            final data = cached['data'] as List?;
            if (data != null) {
              debugPrint('Returning stale cached active preferences');
              return data.map((json) => SmaPreference.fromJson(json)).toList();
            }
          } catch (e) {
            debugPrint('Cache parse error: $e');
          }
        }
      }

      debugPrint('Exception getActivePreferences: $e');
      throw 'Failed to fetch active SMA preferences: $e';
    }
  }

  Future<SmaPreference> createPreference(
      int userId, SmaPreference preference) async {
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
          final savedPref = SmaPreference.fromJson(data);
          await _invalidatePreferencesCache(userId);
          return savedPref;
        }
      }
      final errorMsg = response['message'] ?? 'Failed to create preference';
      debugPrint('createPreference error: $errorMsg');
      throw errorMsg;
    } catch (e) {
      debugPrint('Exception createPreference: $e');
      String errorMsg = e.toString();
      if (errorMsg.contains('TimeoutException') ||
          errorMsg.contains('SocketException') ||
          errorMsg.contains('Connection')) {
        throw 'Unable to connect to server. Please check your internet connection.';
      }
      throw 'Failed to save preference: $e';
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
          final updatedPref = SmaPreference.fromJson(data);
          await _invalidatePreferencesCache(userId);
          return updatedPref;
        }
      }
      throw response['message'] ?? 'Failed to update preference';
    } catch (e) {
      throw 'Failed to update SMA preference: $e';
    }
  }

  Future<void> togglePreference(
      int userId, int preferenceId, bool active) async {
    try {
      final response = await _apiService.patch(
        '${ApiConstants.smaPreferences}/$preferenceId/user/$userId/toggle?active=$active',
      );

      debugPrint('togglePreference response: $response');

      if (response['success'] != true) {
        throw response['message'] ?? 'Failed to toggle preference';
      }

      await _invalidatePreferencesCache(userId);
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

      await _invalidatePreferencesCache(userId);
    } catch (e) {
      throw 'Failed to delete SMA preference: $e';
    }
  }

  Future<void> _invalidatePreferencesCache(int userId) async {
    await _ensureCacheInitialized();
    if (_cache != null) {
      await _cache!.remove('sma_prefs_$userId');
      await _cache!.remove('sma_active_prefs_$userId');
      debugPrint('Invalidated preferences cache for user $userId');
    }
  }

  Future<void> clearCache() async {
    if (_cache != null) {
      final keys = _cache!.getAllKeys().where((k) => k.startsWith('sma_'));
      for (final key in keys) {
        await _cache!.remove(key);
      }
      debugPrint('SMA repository cache cleared');
    }
  }
}
