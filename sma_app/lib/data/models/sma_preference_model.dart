import 'package:flutter/material.dart';

enum DietType { none, veg, nonVeg, vegan, eggetarian }

extension DietTypeExtension on DietType {
  String get displayName {
    switch (this) {
      case DietType.none:
        return 'No Preference';
      case DietType.veg:
        return 'Vegetarian';
      case DietType.nonVeg:
        return 'Non-Vegetarian';
      case DietType.vegan:
        return 'Vegan';
      case DietType.eggetarian:
        return 'Eggetarian';
    }
  }
}

enum MealType { breakfast, lunch, dinner, snacks }

extension MealTypeExtension on MealType {
  String get displayName {
    switch (this) {
      case MealType.breakfast:
        return 'Breakfast';
      case MealType.lunch:
        return 'Lunch';
      case MealType.dinner:
        return 'Dinner';
      case MealType.snacks:
        return 'Snacks';
    }
  }

  IconData get icon {
    switch (this) {
      case MealType.breakfast:
        return Icons.wb_sunny;
      case MealType.lunch:
        return Icons.restaurant;
      case MealType.dinner:
        return Icons.nightlight;
      case MealType.snacks:
        return Icons.cookie;
    }
  }

  String get defaultTime {
    switch (this) {
      case MealType.breakfast:
        return '8:00 AM';
      case MealType.lunch:
        return '1:00 PM';
      case MealType.dinner:
        return '8:00 PM';
      case MealType.snacks:
        return '5:00 PM';
    }
  }
}

class MealPreference {
  final MealType mealType;
  final String scheduledTime;

  MealPreference({
    required this.mealType,
    required this.scheduledTime,
  });

  Map<String, dynamic> toJson() => {
        'mealType': mealType.name,
        'scheduledTime': scheduledTime,
      };

  factory MealPreference.fromJson(Map<String, dynamic> json) => MealPreference(
        mealType: MealType.values.firstWhere(
          (m) => m.name == json['mealType'],
          orElse: () => MealType.lunch,
        ),
        scheduledTime: json['scheduledTime'] ?? '12:00',
      );
}

class RecommendationHistory {
  final int id;
  final String menuItemName;
  final String restaurantName;
  final String mealType;
  final int? calories;
  final DateTime recommendedAt;

  RecommendationHistory({
    required this.id,
    required this.menuItemName,
    required this.restaurantName,
    required this.mealType,
    this.calories,
    required this.recommendedAt,
  });
}

class SmaPreference {
  final int? id;
  final String userId;
  final bool isEnabled;
  final MealType mealType;
  final String scheduledTime;
  final bool includeWeekends;
  final DietType dietType;
  final List<String> cuisinePreferences;
  final int maxCalories;
  final int minProtein;
  final int maxCarbs;
  final int maxFats;
  final int minFiber;
  final int maxFiber;
  final int? budgetLimit;
  final bool isActive;

  SmaPreference({
    this.id,
    required this.userId,
    this.isEnabled = true,
    this.mealType = MealType.lunch,
    this.scheduledTime = '1:00 PM',
    this.includeWeekends = true,
    this.dietType = DietType.none,
    this.cuisinePreferences = const [],
    this.maxCalories = 600,
    this.minProtein = 30,
    this.maxCarbs = 80,
    this.maxFats = 30,
    this.minFiber = 10,
    this.maxFiber = 50,
    this.budgetLimit,
    this.isActive = true,
  });

  factory SmaPreference.fromJson(Map<String, dynamic> json) {
    return SmaPreference(
      id: json['id'],
      userId: json['userId'] ?? '',
      isEnabled: json['isEnabled'] ?? true,
      mealType: MealType.values.firstWhere(
        (m) => m.name == (json['mealType'] ?? 'lunch'),
        orElse: () => MealType.lunch,
      ),
      scheduledTime: json['scheduledTime'] ?? '1:00 PM',
      includeWeekends: json['includeWeekends'] ?? true,
      dietType: DietType.values.firstWhere(
        (d) => d.name == (json['dietType'] ?? 'none'),
        orElse: () => DietType.none,
      ),
      cuisinePreferences: (json['cuisinePreferences'] as List?)
              ?.map((e) => e.toString())
              .toList() ??
          [],
      maxCalories: json['maxCalories'] ?? 600,
      minProtein: json['minProtein'] ?? 30,
      maxCarbs: json['maxCarbs'] ?? 80,
      maxFats: json['maxFats'] ?? 30,
      minFiber: json['minFiber'] ?? 10,
      maxFiber: json['maxFiber'] ?? 50,
      budgetLimit: json['budgetLimit'],
      isActive: json['isActive'] ?? true,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'mealType': mealType.name,
      'scheduledTime': _parseTimeForBackend(scheduledTime),
      'includeWeekends': includeWeekends,
      'dietType': dietType.name,
      'minCalories': 0,
      'maxCalories': maxCalories,
      'minProtein': minProtein,
      'maxProtein': minProtein + 20,
      'minCarbs': 0,
      'maxCarbs': maxCarbs,
      'minFat': 0,
      'maxFat': maxFats,
      'minFiber': minFiber,
      'maxFiber': maxFiber,
      'maxBudget': budgetLimit,
    };
  }

  String _parseTimeForBackend(String time) {
    try {
      final parts = time.split(':');
      int hour = int.parse(parts[0]);
      final isPM = time.toUpperCase().contains('PM') && hour < 12;
      if (isPM) hour += 12;
      return '${hour.toString().padLeft(2, '0')}:00';
    } catch (e) {
      return '13:00';
    }
  }

  SmaPreference copyWith({
    int? id,
    String? userId,
    bool? isEnabled,
    MealType? mealType,
    String? scheduledTime,
    bool? includeWeekends,
    DietType? dietType,
    List<String>? cuisinePreferences,
    int? maxCalories,
    int? minProtein,
    int? maxCarbs,
    int? maxFats,
    int? minFiber,
    int? maxFiber,
    int? budgetLimit,
    bool? isActive,
  }) {
    return SmaPreference(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      isEnabled: isEnabled ?? this.isEnabled,
      mealType: mealType ?? this.mealType,
      scheduledTime: scheduledTime ?? this.scheduledTime,
      includeWeekends: includeWeekends ?? this.includeWeekends,
      dietType: dietType ?? this.dietType,
      cuisinePreferences: cuisinePreferences ?? this.cuisinePreferences,
      maxCalories: maxCalories ?? this.maxCalories,
      minProtein: minProtein ?? this.minProtein,
      maxCarbs: maxCarbs ?? this.maxCarbs,
      maxFats: maxFats ?? this.maxFats,
      minFiber: minFiber ?? this.minFiber,
      maxFiber: maxFiber ?? this.maxFiber,
      budgetLimit: budgetLimit ?? this.budgetLimit,
      isActive: isActive ?? this.isActive,
    );
  }
}
