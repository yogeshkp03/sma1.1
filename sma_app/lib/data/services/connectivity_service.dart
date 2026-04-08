import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:connectivity_plus/connectivity_plus.dart';

class ConnectivityService extends ChangeNotifier {
  static ConnectivityService? _instance;

  final Connectivity _connectivity = Connectivity();
  StreamSubscription<List<ConnectivityResult>>? _subscription;

  bool _isOnline = true;
  bool _isInitialized = false;

  bool get isOnline => _isOnline;
  bool get isOffline => !_isOnline;
  bool get isInitialized => _isInitialized;

  ConnectivityService._();

  static ConnectivityService get instance {
    _instance ??= ConnectivityService._();
    return _instance!;
  }

  Future<void> initialize() async {
    if (_isInitialized) return;

    try {
      final results = await _connectivity.checkConnectivity().timeout(
            const Duration(seconds: 5),
            onTimeout: () => [ConnectivityResult.none],
          );
      _updateStatus(results);
    } catch (e) {
      debugPrint('Connectivity init error: $e');
      _isOnline = true;
    }

    _isInitialized = true;

    try {
      _subscription = _connectivity.onConnectivityChanged.listen(
        (List<ConnectivityResult> results) {
          _updateStatus(results);
        },
        onError: (e) {
          debugPrint('Connectivity subscription error: $e');
        },
      );
    } catch (e) {
      debugPrint('Connectivity subscription setup error: $e');
    }

    notifyListeners();
  }

  void _updateStatus(List<ConnectivityResult> results) {
    if (results.isEmpty || results.contains(ConnectivityResult.none)) {
      _isOnline = false;
    } else {
      _isOnline = true;
    }
    notifyListeners();
  }

  @override
  void dispose() {
    _subscription?.cancel();
    super.dispose();
  }
}
