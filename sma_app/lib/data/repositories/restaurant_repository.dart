import 'package:flutter/foundation.dart';
import '../models/restaurant_model.dart';
import '../models/menu_item_model.dart';
import '../services/api_service.dart';
import '../services/cache_service.dart';
import '../../core/constants/api_constants.dart';

class RestaurantRepository {
  final ApiService _apiService = ApiService();
  CacheService? _cache;
  bool _cacheEnabled = true;
  final Duration _cacheTtl = const Duration(hours: 24);
  bool _cacheInitialized = false;

  static const String _restaurantsKey = 'restaurants';
  static const String _locationsKey = 'locations';
  static const String _menuKeyPrefix = 'menu_';

  RestaurantRepository({bool cacheEnabled = true}) {
    _cacheEnabled = cacheEnabled;
  }

  Future<void> _ensureCacheInitialized() async {
    if (!_cacheInitialized) {
      _cache = await CacheService.getInstance();
      _cacheInitialized = true;
    }
  }

  Future<List<Restaurant>> getRestaurants({
    String? location,
    String? search,
    bool forceRefresh = false,
  }) async {
    await _ensureCacheInitialized();

    final cacheKey = '$_restaurantsKey${location ?? ''}${search ?? ''}';

    if (_cacheEnabled && !forceRefresh && _cache != null) {
      final cached = _cache!.get(cacheKey);
      if (cached != null && !_cache!.isExpired(cacheKey, ttl: _cacheTtl)) {
        debugPrint('Returning cached restaurants');
        try {
          final data = cached['data'] as List?;
          if (data != null) {
            return data.map((json) => Restaurant.fromJson(json)).toList();
          }
        } catch (e) {
          debugPrint('Cache parse error: $e');
        }
      }
    }

    try {
      final queryParams = <String, dynamic>{};
      if (location != null && location.isNotEmpty) {
        queryParams['location'] = location;
      }
      if (search != null && search.isNotEmpty) {
        queryParams['search'] = search;
      }

      final response = await _apiService.get(
        ApiConstants.restaurants,
        queryParameters: queryParams,
      );

      if (response['success'] == true) {
        final restaurants = (response['data'] as List)
            .map((json) => Restaurant.fromJson(json))
            .toList();

        if (_cacheEnabled && _cache != null) {
          await _cache!.set(
              cacheKey,
              {
                'data': response['data'],
              },
              ttl: _cacheTtl);
          debugPrint('Cached restaurants: ${restaurants.length} items');
        }

        return restaurants;
      }
      return [];
    } catch (e) {
      debugPrint('Network error, trying cache: $e');

      if (_cacheEnabled && _cache != null) {
        final cached = _cache!.get(cacheKey);
        if (cached != null) {
          try {
            final data = cached['data'] as List?;
            if (data != null) {
              debugPrint('Returning stale cache data');
              return data.map((json) => Restaurant.fromJson(json)).toList();
            }
          } catch (e) {
            debugPrint('Cache parse error: $e');
          }
        }
      }

      throw 'Failed to fetch restaurants: $e';
    }
  }

  Future<Restaurant?> getRestaurantById(int id,
      {bool forceRefresh = false}) async {
    final cacheKey = 'restaurant_$id';
    await _ensureCacheInitialized();

    if (_cacheEnabled && !forceRefresh && _cache != null) {
      final cached = _cache!.get(cacheKey);
      if (cached != null && !_cache!.isExpired(cacheKey, ttl: _cacheTtl)) {
        try {
          return Restaurant.fromJson(cached['data']);
        } catch (e) {
          debugPrint('Cache parse error: $e');
        }
      }
    }

    try {
      final response = await _apiService.get('${ApiConstants.restaurants}/$id');

      if (response['success'] == true) {
        final restaurant = Restaurant.fromJson(response['data']);

        if (_cacheEnabled && _cache != null) {
          await _cache!.set(
              cacheKey,
              {
                'data': response['data'],
              },
              ttl: _cacheTtl);
        }

        return restaurant;
      }
      return null;
    } catch (e) {
      if (_cacheEnabled && _cache != null) {
        final cached = _cache!.get(cacheKey);
        if (cached != null) {
          try {
            return Restaurant.fromJson(cached['data']);
          } catch (e) {
            debugPrint('Cache parse error: $e');
          }
        }
      }
      throw 'Failed to fetch restaurant: $e';
    }
  }

  Future<List<String>> getLocations({bool forceRefresh = false}) async {
    await _ensureCacheInitialized();

    if (_cacheEnabled && !forceRefresh && _cache != null) {
      final cached = _cache!.get(_locationsKey);
      if (cached != null && !_cache!.isExpired(_locationsKey, ttl: _cacheTtl)) {
        try {
          return List<String>.from(cached['data']);
        } catch (e) {
          debugPrint('Cache parse error: $e');
        }
      }
    }

    try {
      final response = await _apiService.get(ApiConstants.restaurantLocations);

      if (response['success'] == true) {
        final locations = List<String>.from(response['data']);

        if (_cacheEnabled && _cache != null) {
          await _cache!.set(
              _locationsKey,
              {
                'data': response['data'],
              },
              ttl: _cacheTtl);
        }

        return locations;
      }
      return [];
    } catch (e) {
      if (_cacheEnabled && _cache != null) {
        final cached = _cache!.get(_locationsKey);
        if (cached != null) {
          try {
            return List<String>.from(cached['data']);
          } catch (e) {
            debugPrint('Cache parse error: $e');
          }
        }
      }
      throw 'Failed to fetch locations: $e';
    }
  }

  Future<List<MenuItem>> getMenuByRestaurant(int restaurantId,
      {bool forceRefresh = false}) async {
    final cacheKey = '$_menuKeyPrefix$restaurantId';
    await _ensureCacheInitialized();

    if (_cacheEnabled && !forceRefresh && _cache != null) {
      final cached = _cache!.get(cacheKey);
      if (cached != null && !_cache!.isExpired(cacheKey, ttl: _cacheTtl)) {
        try {
          final data = cached['data'] as List?;
          if (data != null) {
            debugPrint('Returning cached menu for restaurant $restaurantId');
            return data.map((json) => MenuItem.fromJson(json)).toList();
          }
        } catch (e) {
          debugPrint('Cache parse error: $e');
        }
      }
    }

    try {
      final response = await _apiService.get(
        '${ApiConstants.menu}/restaurant/$restaurantId',
      );

      if (response['success'] == true) {
        final menuItems = (response['data'] as List)
            .map((json) => MenuItem.fromJson(json))
            .toList();

        if (_cacheEnabled && _cache != null) {
          await _cache!.set(
              cacheKey,
              {
                'data': response['data'],
              },
              ttl: _cacheTtl);
          debugPrint('Cached menu: ${menuItems.length} items');
        }

        return menuItems;
      }
      return [];
    } catch (e) {
      debugPrint('Network error, trying cache: $e');

      if (_cacheEnabled && _cache != null) {
        final cached = _cache!.get(cacheKey);
        if (cached != null) {
          try {
            final data = cached['data'] as List?;
            if (data != null) {
              debugPrint('Returning stale cache menu data');
              return data.map((json) => MenuItem.fromJson(json)).toList();
            }
          } catch (e) {
            debugPrint('Cache parse error: $e');
          }
        }
      }

      throw 'Failed to fetch menu: $e';
    }
  }

  Future<MenuItem?> getMenuItemById(int id) async {
    try {
      final response = await _apiService.get('${ApiConstants.menu}/$id');

      if (response['success'] == true) {
        return MenuItem.fromJson(response['data']);
      }
      return null;
    } catch (e) {
      throw 'Failed to fetch menu item: $e';
    }
  }

  Future<void> clearCache() async {
    if (_cache != null) {
      await _cache!.clear();
      debugPrint('Restaurant repository cache cleared');
    }
  }

  DateTime? getCacheTimestamp(String key) {
    return _cache?.getTimestamp(key);
  }

  bool hasCachedData(String key) {
    if (_cache == null) return false;
    return _cache!.get(key) != null;
  }
}
