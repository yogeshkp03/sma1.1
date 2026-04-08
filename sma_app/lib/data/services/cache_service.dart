import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';

class CacheService {
  static const String _prefix = 'sma_cache_';
  static const Duration _defaultTtl = Duration(hours: 24);

  static CacheService? _instance;
  SharedPreferences? _prefs;

  CacheService._();

  static Future<CacheService> getInstance() async {
    if (_instance == null) {
      _instance = CacheService._();
      _instance!._prefs = await SharedPreferences.getInstance();
    }
    return _instance!;
  }

  Future<void> set(String key, dynamic data,
      {Duration ttl = _defaultTtl}) async {
    final prefs = _prefs ?? await SharedPreferences.getInstance();
    final cacheData = {
      'data': data,
      'timestamp': DateTime.now().millisecondsSinceEpoch,
      'ttl': ttl.inMilliseconds,
    };
    await prefs.setString('$_prefix$key', jsonEncode(cacheData));
  }

  dynamic get(String key) {
    final prefs = _prefs;
    if (prefs == null) return null;

    final jsonStr = prefs.getString('$_prefix$key');
    if (jsonStr == null) return null;

    try {
      return jsonDecode(jsonStr);
    } catch (e) {
      debugPrint('Cache get error: $e');
      return null;
    }
  }

  bool isExpired(String key, {Duration ttl = _defaultTtl}) {
    final cacheData = get(key);
    if (cacheData == null) return true;

    final timestamp = cacheData['timestamp'] as int?;
    final storedTtl = (cacheData['ttl'] as int?) ?? _defaultTtl.inMilliseconds;
    final effectiveTtl = ttl.inMilliseconds != _defaultTtl.inMilliseconds
        ? ttl.inMilliseconds
        : storedTtl;

    if (timestamp == null) return true;

    final age = DateTime.now().millisecondsSinceEpoch - timestamp;
    return age > effectiveTtl;
  }

  DateTime? getTimestamp(String key) {
    final cacheData = get(key);
    if (cacheData == null) return null;

    final timestamp = cacheData['timestamp'] as int?;
    if (timestamp == null) return null;

    return DateTime.fromMillisecondsSinceEpoch(timestamp);
  }

  Future<void> remove(String key) async {
    final prefs = _prefs ?? await SharedPreferences.getInstance();
    await prefs.remove('$_prefix$key');
  }

  Future<void> clear() async {
    final prefs = _prefs ?? await SharedPreferences.getInstance();
    final keys = prefs.getKeys().where((k) => k.startsWith(_prefix));
    for (final key in keys) {
      await prefs.remove(key);
    }
    debugPrint('Cache cleared');
  }

  Future<void> clearAll() async {
    final prefs = _prefs ?? await SharedPreferences.getInstance();
    await prefs.clear();
    debugPrint('All SharedPreferences cleared');
  }

  Set<String> getAllKeys() {
    final prefs = _prefs;
    if (prefs == null) return {};
    return prefs
        .getKeys()
        .where((k) => k.startsWith(_prefix))
        .map((k) => k.replaceFirst(_prefix, ''))
        .toSet();
  }

  int getCacheSize() {
    final prefs = _prefs;
    if (prefs == null) return 0;
    final keys = prefs.getKeys().where((k) => k.startsWith(_prefix));
    int size = 0;
    for (final key in keys) {
      final value = prefs.getString(key);
      if (value != null) {
        size += value.length;
      }
    }
    return size;
  }

  String getCacheSizeFormatted() {
    final bytes = getCacheSize();
    if (bytes < 1024) return '$bytes B';
    if (bytes < 1024 * 1024) return '${(bytes / 1024).toStringAsFixed(1)} KB';
    return '${(bytes / (1024 * 1024)).toStringAsFixed(1)} MB';
  }
}
