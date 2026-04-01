import 'dart:async';
import 'package:flutter/material.dart';
import 'package:connectivity_plus/connectivity_plus.dart';

class NetworkConnectivityService {
  static final NetworkConnectivityService _instance =
      NetworkConnectivityService._internal();
  factory NetworkConnectivityService() => _instance;
  NetworkConnectivityService._internal();

  final Connectivity _connectivity = Connectivity();
  StreamSubscription<List<ConnectivityResult>>? _subscription;

  final StreamController<bool> _connectionController =
      StreamController<bool>.broadcast();
  Stream<bool> get connectionStream => _connectionController.stream;

  bool _isConnected = true;
  bool get isConnected => _isConnected;

  Future<void> initialize() async {
    final results = await _connectivity.checkConnectivity();
    _updateConnectionStatus(results);

    _subscription =
        _connectivity.onConnectivityChanged.listen(_updateConnectionStatus);
  }

  void _updateConnectionStatus(List<ConnectivityResult> results) {
    final wasConnected = _isConnected;
    _isConnected =
        results.isNotEmpty && !results.contains(ConnectivityResult.none);

    if (wasConnected != _isConnected) {
      _connectionController.add(_isConnected);
    }
  }

  Future<bool> checkConnection() async {
    final results = await _connectivity.checkConnectivity();
    _isConnected =
        results.isNotEmpty && !results.contains(ConnectivityResult.none);
    return _isConnected;
  }

  void dispose() {
    _subscription?.cancel();
    _connectionController.close();
  }
}

class NetworkAwareWidget extends StatefulWidget {
  final Widget child;
  final Widget? offlineWidget;

  const NetworkAwareWidget({
    super.key,
    required this.child,
    this.offlineWidget,
  });

  @override
  State<NetworkAwareWidget> createState() => _NetworkAwareWidgetState();
}

class _NetworkAwareWidgetState extends State<NetworkAwareWidget> {
  final NetworkConnectivityService _networkService =
      NetworkConnectivityService();
  bool _isOnline = true;

  @override
  void initState() {
    super.initState();
    _checkConnection();
    _networkService.connectionStream.listen((isConnected) {
      if (mounted) {
        setState(() {
          _isOnline = isConnected;
        });
      }
    });
  }

  Future<void> _checkConnection() async {
    final isConnected = await _networkService.checkConnection();
    if (mounted) {
      setState(() {
        _isOnline = isConnected;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    if (!_isOnline && widget.offlineWidget != null) {
      return widget.offlineWidget!;
    }
    return widget.child;
  }
}
