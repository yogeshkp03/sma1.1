import 'package:flutter/foundation.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../services/api_service.dart';
import '../models/user_model.dart';
import '../../core/constants/api_constants.dart';

class AuthService {
  final ApiService _apiService = ApiService();

  Future<AuthResponse> signInWithEmailPassword(
    String email,
    String password,
  ) async {
    try {
      final response = await _apiService.post(
        ApiConstants.login,
        data: {
          'email': email,
          'password': password,
        },
      );

      if (response.data['success'] == true) {
        AuthResponse authResponse = AuthResponse.fromJson(
          response.data['data'],
        );
        await _apiService.setToken(authResponse.token);

        final prefs = await SharedPreferences.getInstance();
        await prefs.setInt('user_id', authResponse.userId);
        await prefs.setString('user_email', authResponse.email);
        if (authResponse.fullName != null) {
          await prefs.setString('user_name', authResponse.fullName!);
        }

        return authResponse;
      } else {
        throw response.data['message'] ?? 'Login failed';
      }
    } catch (e) {
      throw 'Login failed: $e';
    }
  }

  Future<AuthResponse> signUpWithEmailPassword(
    String email,
    String password,
    String name,
  ) async {
    try {
      final data = {
        'email': email,
        'password': password,
        'fullName': name,
      };

      const fullUrl = '${ApiConstants.baseUrl}${ApiConstants.register}';
      debugPrint('=== REGISTER DEBUG ===');
      debugPrint('Full URL: $fullUrl');
      debugPrint('Data: $data');
      debugPrint('======================');

      final response = await _apiService.post(
        ApiConstants.register,
        data: data,
      );

      debugPrint('Response status: ${response.statusCode}');
      debugPrint('Response data: ${response.data}');

      if (response.data['success'] == true) {
        AuthResponse authResponse = AuthResponse.fromJson(
          response.data['data'],
        );
        await _apiService.setToken(authResponse.token);

        final prefs = await SharedPreferences.getInstance();
        await prefs.setInt('user_id', authResponse.userId);
        await prefs.setString('user_email', authResponse.email);
        if (authResponse.fullName != null) {
          await prefs.setString('user_name', authResponse.fullName!);
        }

        return authResponse;
      } else {
        throw response.data['message'] ?? 'Registration failed';
      }
    } catch (e) {
      debugPrint('Register error: $e');
      throw 'Registration failed: $e';
    }
  }

  Future<void> signOut() async {
    await _apiService.clearToken();

    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('user_id');
    await prefs.remove('user_email');
    await prefs.remove('user_name');
  }

  Future<int?> getSavedUserId() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getInt('user_id');
  }

  Future<String?> getSavedUserEmail() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('user_email');
  }

  Future<String?> getSavedUserName() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('user_name');
  }
}
