import 'package:flutter/material.dart';
import '../data/models/user_model.dart';
import '../data/services/auth_service.dart';
import '../data/services/api_service.dart';

class AuthProvider extends ChangeNotifier {
  final AuthService _authService = AuthService();
  final ApiService _apiService = ApiService();

  AppUser? _user;
  int? _userId;
  bool _isLoading = false;
  String? _error;

  AppUser? get currentUser => _user;
  int? get userId => _userId;
  bool get isLoading => _isLoading;
  String? get error => _error;
  bool get isAuthenticated => _user != null;

  Future<void> checkAuthStatus() async {
    _isLoading = true;
    notifyListeners();

    try {
      final userId = await _authService.getSavedUserId();
      final email = await _authService.getSavedUserEmail();
      final name = await _authService.getSavedUserName();

      if (userId != null) {
        _userId = userId;
        _user = AppUser(
          id: userId,
          email: email ?? '',
          fullName: name,
        );
        await _apiService.loadToken();
      }

      _isLoading = false;
      notifyListeners();
    } catch (e) {
      _error = e.toString();
      _isLoading = false;
      notifyListeners();
    }
  }

  Future<Map<String, dynamic>> login(String email, String password) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      AuthResponse response = await _authService.signInWithEmailPassword(
        email,
        password,
      );
      _user = AppUser(
        id: response.userId,
        email: response.email,
        fullName: response.fullName,
      );
      _userId = response.userId;
      _isLoading = false;
      notifyListeners();
      return {'success': true};
    } catch (e) {
      String errorMsg = e.toString();
      _error = errorMsg;
      _isLoading = false;
      notifyListeners();
      return {'success': false, 'error': errorMsg};
    }
  }

  Future<Map<String, dynamic>> register(
      String email, String password, String name) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      AuthResponse response = await _authService.signUpWithEmailPassword(
        email,
        password,
        name,
      );
      _user = AppUser(
        id: response.userId,
        email: response.email,
        fullName: response.fullName,
      );
      _userId = response.userId;
      _isLoading = false;
      notifyListeners();
      return {'success': true};
    } catch (e) {
      String errorMsg = e.toString();
      _error = errorMsg;
      _isLoading = false;
      notifyListeners();
      return {'success': false, 'error': errorMsg};
    }
  }

  Future<void> logout() async {
    await _authService.signOut();
    _user = null;
    _userId = null;
    notifyListeners();
  }

  void clearError() {
    _error = null;
    notifyListeners();
  }
}
