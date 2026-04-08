class ApiConstants {
  static const String baseUrl = String.fromEnvironment('API_BASE_URL',
      defaultValue: 'http://localhost:8080/api');

  // Auth endpoints
  static const String login = '/auth/login';
  static const String register = '/auth/register';
  static const String user = '/auth/user';

  // Restaurant endpoints
  static const String restaurants = '/restaurants';
  static const String restaurantLocations = '/restaurants/locations';

  // Menu endpoints
  static const String menu = '/menu';

  // Cart endpoints
  static const String cart = '/cart';

  // SMA endpoints
  static const String smaPreferences = '/sma/preferences';

  // Orders endpoints
  static const String orders = '/orders';

  // Address endpoints
  static const String addresses = '/addresses';

  // Recommendations endpoints
  static const String recommendations = '/v1/recommendations';
}

class AppConfig {
  static const String appName = 'Smart Meal Autopilot';
  static const String appVersion = '1.0.0';

  // Timeouts
  static const Duration connectTimeout = Duration(seconds: 30);
  static const Duration receiveTimeout = Duration(seconds: 30);

  // Pagination
  static const int defaultPageSize = 20;
}
