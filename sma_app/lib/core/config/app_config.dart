import 'package:flutter/foundation.dart';

class AppConfig {
  static const String appName = 'Smart Meal Autopilot';
  static const String appVersion = '1.0.0';

  static const Duration connectTimeout = Duration(seconds: 30);
  static const Duration receiveTimeout = Duration(seconds: 30);
  static const int defaultPageSize = 20;

  static const bool isDebugMode = kDebugMode;

  static const List<String> allowedOrigins = [
    'http://localhost:5173',
    'http://localhost:8080',
    'http://10.0.2.2:8080',
  ];
}
