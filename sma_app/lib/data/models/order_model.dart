class OrderModel {
  final int id;
  final String? restaurantName;
  final double totalAmount;
  final String status;
  final String paymentMethod;
  final String paymentStatus;
  final bool isSmaTriggered;
  final String createdAt;
  final String? estimatedDelivery;
  final List<OrderItemModel> items;

  OrderModel({
    required this.id,
    this.restaurantName,
    required this.totalAmount,
    required this.status,
    required this.paymentMethod,
    required this.paymentStatus,
    this.isSmaTriggered = false,
    required this.createdAt,
    this.estimatedDelivery,
    required this.items,
  });

  factory OrderModel.fromJson(Map<String, dynamic> json) {
    return OrderModel(
      id: json['id'] ?? 0,
      restaurantName: json['restaurant']?['name'],
      totalAmount: (json['totalAmount'] ?? 0).toDouble(),
      status: json['status'] ?? 'PENDING',
      paymentMethod: json['paymentMethod'] ?? 'COD',
      paymentStatus: json['paymentStatus'] ?? 'PENDING',
      isSmaTriggered: json['isSmaTriggered'] ?? false,
      createdAt: json['createdAt'] ?? DateTime.now().toIso8601String(),
      estimatedDelivery: json['estimatedDelivery'],
      items: (json['orderItems'] as List<dynamic>?)
              ?.map((e) => OrderItemModel.fromJson(e))
              .toList() ??
          [],
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'restaurantName': restaurantName,
        'totalAmount': totalAmount,
        'status': status,
        'paymentMethod': paymentMethod,
        'paymentStatus': paymentStatus,
        'isSmaTriggered': isSmaTriggered,
        'createdAt': createdAt,
        'estimatedDelivery': estimatedDelivery,
        'items': items.map((e) => e.toJson()).toList(),
      };

  String get formattedDate {
    try {
      final date = DateTime.parse(createdAt);
      return '${date.day}/${date.month}/${date.year} ${date.hour}:${date.minute.toString().padLeft(2, '0')}';
    } catch (e) {
      return createdAt;
    }
  }
}

class OrderItemModel {
  final int id;
  final String name;
  final double price;
  final int quantity;

  OrderItemModel({
    required this.id,
    required this.name,
    required this.price,
    required this.quantity,
  });

  factory OrderItemModel.fromJson(Map<String, dynamic> json) {
    return OrderItemModel(
      id: json['id'] ?? 0,
      name: json['menuItem']?['name'] ?? json['name'] ?? '',
      price: (json['unitPrice'] ?? json['price'] ?? 0).toDouble(),
      quantity: json['quantity'] ?? 1,
    );
  }

  Map<String, dynamic> toJson() => {
        'id': id,
        'name': name,
        'price': price,
        'quantity': quantity,
      };
}
