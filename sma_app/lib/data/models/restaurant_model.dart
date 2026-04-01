import 'menu_item_model.dart';

class Restaurant {
  final int id;
  final String name;
  final String? description;
  final String? imageUrl;
  final double rating;
  final int deliveryTimeMin;
  final int deliveryTimeMax;
  final double deliveryFee;
  final double minOrder;
  final String? address;
  final String? cuisineType;
  final bool isVegOnly;
  final bool isActive;
  final List<MenuItem> menuItems;

  Restaurant({
    required this.id,
    required this.name,
    this.description,
    this.imageUrl,
    required this.rating,
    required this.deliveryTimeMin,
    required this.deliveryTimeMax,
    required this.deliveryFee,
    required this.minOrder,
    this.address,
    this.cuisineType,
    this.isVegOnly = false,
    this.isActive = true,
    this.menuItems = const [],
  });

  int get averageCost {
    if (menuItems.isEmpty) return 300;
    return (menuItems.map((m) => m.price).reduce((a, b) => a + b) /
            menuItems.length *
            2)
        .round();
  }

  factory Restaurant.fromJson(Map<String, dynamic> json) {
    return Restaurant(
      id: json['id'] ?? 0,
      name: json['name'] ?? '',
      description: json['description'],
      imageUrl: json['imageUrl'],
      rating: (json['rating'] ?? 0).toDouble(),
      deliveryTimeMin: json['deliveryTimeMin'] ?? 30,
      deliveryTimeMax: json['deliveryTimeMax'] ?? 45,
      deliveryFee: (json['deliveryFee'] ?? 0).toDouble(),
      minOrder: (json['minOrder'] ?? 0).toDouble(),
      address: json['address'],
      cuisineType: json['cuisineType'] ?? json['cuisine'],
      isVegOnly: json['isVegOnly'] ?? false,
      isActive: json['isActive'] ?? true,
      menuItems: (json['menuItems'] as List<dynamic>?)
              ?.map((m) => MenuItem.fromJson(m))
              .toList() ??
          [],
    );
  }
}
