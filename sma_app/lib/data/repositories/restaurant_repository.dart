import '../models/restaurant_model.dart';
import '../models/menu_item_model.dart';
import '../services/api_service.dart';
import '../../core/constants/api_constants.dart';

class RestaurantRepository {
  final ApiService _apiService = ApiService();

  Future<List<Restaurant>> getRestaurants({
    String? location,
    String? search,
  }) async {
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

      if (response.data['success'] == true) {
        return (response.data['data'] as List)
            .map((json) => Restaurant.fromJson(json))
            .toList();
      }
      return [];
    } catch (e) {
      throw 'Failed to fetch restaurants: $e';
    }
  }

  Future<Restaurant?> getRestaurantById(int id) async {
    try {
      final response = await _apiService.get('${ApiConstants.restaurants}/$id');

      if (response.data['success'] == true) {
        return Restaurant.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      throw 'Failed to fetch restaurant: $e';
    }
  }

  Future<List<String>> getLocations() async {
    try {
      final response = await _apiService.get(ApiConstants.restaurantLocations);

      if (response.data['success'] == true) {
        return List<String>.from(response.data['data']);
      }
      return [];
    } catch (e) {
      throw 'Failed to fetch locations: $e';
    }
  }

  Future<List<MenuItem>> getMenuByRestaurant(int restaurantId) async {
    try {
      final response = await _apiService.get(
        '${ApiConstants.menu}/restaurant/$restaurantId',
      );

      if (response.data['success'] == true) {
        return (response.data['data'] as List)
            .map((json) => MenuItem.fromJson(json))
            .toList();
      }
      return [];
    } catch (e) {
      throw 'Failed to fetch menu: $e';
    }
  }

  Future<MenuItem?> getMenuItemById(int id) async {
    try {
      final response = await _apiService.get('${ApiConstants.menu}/$id');

      if (response.data['success'] == true) {
        return MenuItem.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      throw 'Failed to fetch menu item: $e';
    }
  }
}
