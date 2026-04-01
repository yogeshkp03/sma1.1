import 'menu_item_model.dart';

class CartItem {
  final int id;
  final MenuItem menuItem;
  final int quantity;
  final String? specialInstructions;

  CartItem({
    required this.id,
    required this.menuItem,
    required this.quantity,
    this.specialInstructions,
  });

  double get totalPrice => menuItem.price * quantity;

  factory CartItem.fromJson(Map<String, dynamic> json) {
    return CartItem(
      id: json['id'] ?? 0,
      menuItem: MenuItem.fromJson(json['menuItem'] ?? {}),
      quantity: json['quantity'] ?? 1,
      specialInstructions: json['specialInstructions'],
    );
  }
}

class Cart {
  final int userId;
  final List<CartItem> items;
  final double subtotal;
  final double deliveryFee;
  final double total;
  final int itemCount;

  Cart({
    required this.userId,
    required this.items,
    required this.subtotal,
    required this.deliveryFee,
    required this.total,
    required this.itemCount,
  });

  factory Cart.fromJson(Map<String, dynamic> json) {
    return Cart(
      userId: json['userId'] ?? 0,
      items: (json['items'] as List? ?? [])
          .map((e) => CartItem.fromJson(e))
          .toList(),
      subtotal: (json['subtotal'] ?? 0).toDouble(),
      deliveryFee: (json['deliveryFee'] ?? 0).toDouble(),
      total: (json['total'] ?? 0).toDouble(),
      itemCount: json['itemCount'] ?? 0,
    );
  }
}
