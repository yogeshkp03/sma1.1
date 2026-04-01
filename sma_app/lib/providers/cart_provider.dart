import 'package:flutter/material.dart';
import '../data/models/cart_model.dart';
import '../data/models/menu_item_model.dart';
import '../data/models/restaurant_model.dart';

class CartProvider extends ChangeNotifier {
  final List<CartItem> _items = [];
  Restaurant? _restaurant;
  bool _isLoading = false;
  String? _error;

  List<CartItem> get items => _items;
  Restaurant? get restaurant => _restaurant;
  bool get isLoading => _isLoading;
  String? get error => _error;
  int get itemCount => _items.fold(0, (sum, item) => sum + item.quantity);
  double get subtotal => _items.fold(0, (sum, item) => sum + item.totalPrice);
  double get totalAmount => subtotal + 45;
  double get total => totalAmount;

  Future<void> loadCart() async {
    _isLoading = true;
    notifyListeners();
    await Future.delayed(const Duration(milliseconds: 300));
    _isLoading = false;
    notifyListeners();
  }

  void addItem({
    required MenuItem menuItem,
    required Restaurant restaurant,
    required int quantity,
  }) {
    if (_restaurant != null &&
        _restaurant!.id != restaurant.id &&
        _items.isNotEmpty) {
      _items.clear();
    }
    _restaurant = restaurant;

    final existingIndex =
        _items.indexWhere((item) => item.menuItem.id == menuItem.id);
    if (existingIndex >= 0) {
      final existing = _items[existingIndex];
      _items[existingIndex] = CartItem(
        id: existing.id,
        menuItem: existing.menuItem,
        quantity: existing.quantity + quantity,
        specialInstructions: existing.specialInstructions,
      );
    } else {
      _items.add(CartItem(
        id: DateTime.now().millisecondsSinceEpoch,
        menuItem: menuItem,
        quantity: quantity,
      ));
    }
    notifyListeners();
  }

  void updateQuantity(int menuItemId, int quantity) {
    final index = _items.indexWhere((item) => item.menuItem.id == menuItemId);
    if (index >= 0) {
      if (quantity <= 0) {
        _items.removeAt(index);
      } else {
        final item = _items[index];
        _items[index] = CartItem(
          id: item.id,
          menuItem: item.menuItem,
          quantity: quantity,
          specialInstructions: item.specialInstructions,
        );
      }
      notifyListeners();
    }
  }

  void removeItem(int menuItemId) {
    _items.removeWhere((item) => item.menuItem.id == menuItemId);
    notifyListeners();
  }

  void clearCart() {
    _items.clear();
    _restaurant = null;
    notifyListeners();
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }
}
