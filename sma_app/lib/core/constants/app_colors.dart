import 'package:flutter/material.dart';

class AppColors {
  static const Color primary = Color(0xFFFF6B35);
  static const Color primaryDark = Color(0xFFE55A2B);
  static const Color secondary = Color(0xFF004E64);
  static const Color accent = Color(0xFF25A18E);

  static const Color background = Color(0xFFF8F9FA);
  static const Color surface = Color(0xFFFFFFFF);
  static const Color error = Color(0xFFDC3545);
  static const Color success = Color(0xFF28A745);

  static const Color textPrimary = Color(0xFF212529);
  static const Color textSecondary = Color(0xFF6C757D);
  static const Color textLight = Color(0xFFFFFFFF);

  static const Color divider = Color(0xFFE9ECEF);
  static const Color shadow = Color(0x1A000000);

  // Diet type colors
  static const Color vegColor = Color(0xFF28A745);
  static const Color nonVegColor = Color(0xFFDC3545);
  static const Color eggColor = Color(0xFFFFA500);
  static const Color veganColor = Color(0xFF6F42C1);
}

class MealTypeIcons {
  static const Map<String, String> icons = {
    'MORNING_FUEL': '🌅',
    'POWER_HOUR': '⚡',
    'TWILIGHT_FEAST': '🌙',
    'CRAVE_CORNER': '🍿',
  };

  static const Map<String, String> displayNames = {
    'MORNING_FUEL': 'Morning Fuel',
    'POWER_HOUR': 'Power Hour',
    'TWILIGHT_FEAST': 'Twilight Feast',
    'CRAVE_CORNER': 'Crave Corner',
  };

  static const Map<String, String> descriptions = {
    'MORNING_FUEL': 'Breakfast (7 AM - 10 AM)',
    'POWER_HOUR': 'Lunch (12 PM - 2 PM)',
    'TWILIGHT_FEAST': 'Dinner (7 PM - 10 PM)',
    'CRAVE_CORNER': 'Snacks (4 PM - 6 PM)',
  };
}
