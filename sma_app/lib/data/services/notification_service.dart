import 'dart:io';
import 'package:flutter/material.dart';
import 'package:flutter_local_notifications/flutter_local_notifications.dart';
import 'package:shared_preferences/shared_preferences.dart';

class NotificationService {
  static final NotificationService _instance = NotificationService._internal();
  factory NotificationService() => _instance;
  NotificationService._internal();

  final FlutterLocalNotificationsPlugin _notifications =
      FlutterLocalNotificationsPlugin();
  bool _isInitialized = false;

  Future<void> initialize() async {
    if (_isInitialized) return;

    const androidSettings =
        AndroidInitializationSettings('@mipmap/ic_launcher');
    const iosSettings = DarwinInitializationSettings(
      requestAlertPermission: true,
      requestBadgePermission: true,
      requestSoundPermission: true,
    );

    const initSettings = InitializationSettings(
      android: androidSettings,
      iOS: iosSettings,
    );

    await _notifications.initialize(
      initSettings,
      onDidReceiveNotificationResponse: _onNotificationTapped,
    );

    _isInitialized = true;
  }

  void _onNotificationTapped(NotificationResponse response) {
    final payload = response.payload;
    if (payload == null) return;

    debugPrint('Notification tapped with payload: $payload');
  }

  Future<bool> requestPermission() async {
    if (Platform.isAndroid) {
      final androidPlugin =
          _notifications.resolvePlatformSpecificImplementation<
              AndroidFlutterLocalNotificationsPlugin>();
      final granted = await androidPlugin?.requestNotificationsPermission();
      return granted ?? false;
    } else if (Platform.isIOS) {
      final iosPlugin = _notifications.resolvePlatformSpecificImplementation<
          IOSFlutterLocalNotificationsPlugin>();
      final granted = await iosPlugin?.requestPermissions(
        alert: true,
        badge: true,
        sound: true,
      );
      return granted ?? false;
    }
    return true;
  }

  Future<void> saveFcmToken(String token) async {
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('fcm_token', token);
  }

  Future<String?> getFcmToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('fcm_token');
  }

  Future<void> showMealRecommendation({
    required String mealType,
    required String itemName,
    required String restaurantName,
  }) async {
    const androidDetails = AndroidNotificationDetails(
      'meal_recommendation',
      'Meal Recommendations',
      channelDescription: 'AI meal recommendations',
      importance: Importance.high,
      priority: Priority.high,
      icon: '@mipmap/ic_launcher',
    );

    const iosDetails = DarwinNotificationDetails(
      presentAlert: true,
      presentBadge: true,
      presentSound: true,
    );

    const details = NotificationDetails(
      android: androidDetails,
      iOS: iosDetails,
    );

    await _notifications.show(
      0,
      '🍽️ Time for $mealType!',
      '$itemName from $restaurantName has been added to your cart',
      details,
      payload: 'meal_recommendation',
    );
  }

  Future<void> showOrderStatus({
    required String orderId,
    required String status,
    String? message,
  }) async {
    String title;
    String body;

    switch (status.toUpperCase()) {
      case 'CONFIRMED':
        title = '✅ Order Confirmed';
        body = 'Your order #$orderId has been confirmed';
        break;
      case 'PREPARING':
        title = '👨‍🍳 Preparing Your Order';
        body = 'Your order is being prepared';
        break;
      case 'OUT_FOR_DELIVERY':
        title = '🚴 Out for Delivery';
        body = 'Your order is on its way';
        break;
      case 'DELIVERED':
        title = '✅ Order Delivered';
        body = 'Your order has been delivered. Enjoy!';
        break;
      case 'CANCELLED':
        title = '❌ Order Cancelled';
        body = 'Your order #$orderId has been cancelled';
        break;
      default:
        title = 'Order Update';
        body = message ?? 'Your order status has been updated';
    }

    const androidDetails = AndroidNotificationDetails(
      'order_status',
      'Order Status',
      channelDescription: 'Order status updates',
      importance: Importance.high,
      priority: Priority.high,
      icon: '@mipmap/ic_launcher',
    );

    const iosDetails = DarwinNotificationDetails(
      presentAlert: true,
      presentBadge: true,
      presentSound: true,
    );

    const details = NotificationDetails(
      android: androidDetails,
      iOS: iosDetails,
    );

    await _notifications.show(
      orderId.hashCode,
      title,
      body,
      details,
      payload: 'order_status:$orderId',
    );
  }

  Future<void> showSmaReminder({
    required String mealType,
    required String scheduledTime,
  }) async {
    const androidDetails = AndroidNotificationDetails(
      'sma_reminder',
      'SMA Reminders',
      channelDescription: 'Smart Meal Autopilot reminders',
      importance: Importance.high,
      priority: Priority.high,
      icon: '@mipmap/ic_launcher',
    );

    const iosDetails = DarwinNotificationDetails(
      presentAlert: true,
      presentBadge: true,
      presentSound: true,
    );

    const details = NotificationDetails(
      android: androidDetails,
      iOS: iosDetails,
    );

    await _notifications.show(
      1,
      '⏰ $mealType Time!',
      'Your scheduled meal will be auto-added in 1 hour at $scheduledTime',
      details,
      payload: 'sma_reminder',
    );
  }

  Future<void> cancelAllNotifications() async {
    await _notifications.cancelAll();
  }

  Future<void> cancelNotification(int id) async {
    await _notifications.cancel(id);
  }
}
