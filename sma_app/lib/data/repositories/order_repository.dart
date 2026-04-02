import 'package:shared_preferences/shared_preferences.dart';
import '../../core/constants/api_constants.dart';
import '../../data/models/order_model.dart';
import '../services/api_service.dart';

class OrderRepository {
  final ApiService _apiService = ApiService();

  Future<int> _getUserId() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getInt('user_id') ?? 1;
  }

  Future<List<OrderModel>> getOrders({int page = 0, int size = 10}) async {
    try {
      final userId = await _getUserId();
      final response = await _apiService.get(
        '${ApiConstants.orders}/user',
        queryParameters: {'page': page, 'size': size},
        headers: {'X-User-Id': userId.toString()},
      );

      if (response['success'] == true) {
        final List<dynamic> data = response['data'] ?? [];
        return data.map((json) => OrderModel.fromJson(json)).toList();
      }
      return [];
    } catch (e) {
      throw Exception('Failed to load orders: $e');
    }
  }

  Future<OrderModel> getOrderDetail(int orderId) async {
    try {
      final userId = await _getUserId();
      final response = await _apiService.get(
        '${ApiConstants.orders}/$orderId',
        headers: {'X-User-Id': userId.toString()},
      );

      if (response['success'] == true) {
        return OrderModel.fromJson(response['data']);
      }
      throw Exception('Failed to load order details');
    } catch (e) {
      throw Exception('Failed to load order: $e');
    }
  }

  Future<OrderModel> cancelOrder(int orderId) async {
    try {
      final response = await _apiService.post(
        '${ApiConstants.orders}/$orderId/cancel',
      );

      if (response['success'] == true) {
        return OrderModel.fromJson(response['data']);
      }
      throw Exception(response['message'] ?? 'Failed to cancel order');
    } catch (e) {
      throw Exception('Failed to cancel order: $e');
    }
  }

  Future<List<OrderModel>> getSmaOrders() async {
    try {
      final userId = await _getUserId();
      final response = await _apiService.get(
        '${ApiConstants.orders}/user/$userId/sma',
        headers: {'X-User-Id': userId.toString()},
      );

      if (response['success'] == true) {
        final List<dynamic> data = response['data'] ?? [];
        return data.map((json) => OrderModel.fromJson(json)).toList();
      }
      return [];
    } catch (e) {
      throw Exception('Failed to load SMA orders: $e');
    }
  }
}
