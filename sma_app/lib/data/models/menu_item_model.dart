class MenuItem {
  final int id;
  final int? restaurantId;
  final String? restaurantName;
  final String name;
  final String? description;
  final double price;
  final String? imageUrl;
  final bool isVeg;
  final int? calories;
  final double? proteinGrams;
  final double? carbsGrams;
  final double? fatGrams;
  final String? carbsLevel;
  final String? fatLevel;
  final String? category;
  final bool isAvailable;
  final bool isRecommended;

  MenuItem({
    required this.id,
    this.restaurantId,
    this.restaurantName,
    required this.name,
    this.description,
    required this.price,
    this.imageUrl,
    required this.isVeg,
    this.calories,
    this.proteinGrams,
    this.carbsGrams,
    this.fatGrams,
    this.carbsLevel,
    this.fatLevel,
    this.category,
    this.isAvailable = true,
    this.isRecommended = false,
  });

  factory MenuItem.fromJson(Map<String, dynamic> json) {
    return MenuItem(
      id: json['id'] ?? 0,
      restaurantId: json['restaurantId'],
      restaurantName: json['restaurantName'],
      name: json['name'] ?? '',
      description: json['description'],
      price: (json['price'] ?? 0).toDouble(),
      imageUrl: json['imageUrl'],
      isVeg: (json['isVeg'] ??
          json['dietType']?.toString().toUpperCase() == 'VEG'),
      calories: json['calories'],
      proteinGrams: (json['proteinGrams'] ?? json['protein'] ?? 0).toDouble(),
      carbsGrams: (json['carbsGrams'] ?? json['carbs'] ?? 0).toDouble(),
      fatGrams: (json['fatGrams'] ?? json['fats'] ?? 0).toDouble(),
      carbsLevel: json['carbsLevel'],
      fatLevel: json['fatLevel'],
      category: json['category'],
      isAvailable: json['isAvailable'] ?? true,
      isRecommended: json['isRecommended'] ?? false,
    );
  }
}
