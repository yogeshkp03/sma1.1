import '../models/cart_model.dart';
import '../services/api_service.dart';
import '../../core/constants/api_constants.dart';

class CartRepository {
  final ApiService _apiService = ApiService();

  Future<Cart> getCart(int userId) async {
    try {
      final response = await _apiService.get(
        '${ApiConstants.cart}/$userId',
        headers: {'X-User-Id': userId.toString()},
      );

      if (response['success'] == true) {
        return Cart.fromJson(response['data']);
      }
      throw response['message'] ?? 'Failed to fetch cart';
    } catch (e) {
      throw 'Failed to fetch cart: $e';
    }
  }

  Future<CartItem> addToCart(
    int userId,
    int menuItemId,
    int quantity, {
    String? instructions,
  }) async {
    try {
      final response = await _apiService.post(
        '${ApiConstants.cart}/$userId/add',
        data: {
          'menuItemId': menuItemId,
          'quantity': quantity,
          'userId': userId,
          'specialInstructions': instructions,
        },
      );

      if (response['success'] == true) {
        return CartItem.fromJson(response['data']);
      }
      throw response['message'] ?? 'Failed to add to cart';
    } catch (e) {
      throw 'Failed to add to cart: $e';
    }
  }

  Future<CartItem?> updateCartItem(int userId, int itemId, int quantity) async {
    try {
      final response = await _apiService.put(
        '${ApiConstants.cart}/$userId/items/$itemId',
        data: {'quantity': quantity},
      );

      if (response['success'] == true) {
        if (response['data'] == null) return null;
        return CartItem.fromJson(response['data']);
      }
      throw response['message'] ?? 'Failed to update cart';
    } catch (e) {
      throw 'Failed to update cart: $e';
    }
  }

  Future<void> removeFromCart(int userId, int itemId) async {
    try {
      final response = await _apiService.delete(
        '${ApiConstants.cart}/$userId/items/$itemId',
      );

      if (response['success'] != true) {
        throw response['message'] ?? 'Failed to remove from cart';
      }
    } catch (e) {
      throw 'Failed to remove from cart: $e';
    }
  }

  Future<void> clearCart(int userId) async {
    try {
      final response = await _apiService.delete(
        '${ApiConstants.cart}/$userId/clear',
      );

      if (response['success'] != true) {
        throw response['message'] ?? 'Failed to clear cart';
      }
    } catch (e) {
      throw 'Failed to clear cart: $e';
    }
  }
}
