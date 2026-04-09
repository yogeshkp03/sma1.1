import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../../core/constants/api_constants.dart';

class ApiService {
  static const Duration _timeout = Duration(seconds: 30);
  String? _authToken;

  ApiService();

  Map<String, String> _buildHeaders({Map<String, dynamic>? extraHeaders}) {
    final headers = <String, String>{
      'Content-Type': 'application/json',
    };
    if (_authToken != null) {
      headers['Authorization'] = 'Bearer $_authToken';
    }
    if (extraHeaders != null) {
      extraHeaders.forEach((key, value) {
        headers[key] = value.toString();
      });
    }
    return headers;
  }

  Future<void> loadToken() async {
    final prefs = await SharedPreferences.getInstance();
    _authToken = prefs.getString('auth_token');
  }

  Future<void> setToken(String token) async {
    _authToken = token;
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('auth_token', token);
  }

  Future<void> clearToken() async {
    _authToken = null;
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove('auth_token');
  }

  bool get isAuthenticated => _authToken != null;

  Future<Map<String, dynamic>> get(
    String path, {
    Map<String, dynamic>? queryParameters,
    Map<String, dynamic>? headers,
  }) async {
    var uri = Uri.parse('${ApiConstants.baseUrl}$path');
    if (queryParameters != null && queryParameters.isNotEmpty) {
      uri = uri.replace(queryParameters: queryParameters);
    }
    final response = await http
        .get(uri, headers: _buildHeaders(extraHeaders: headers))
        .timeout(_timeout);
    return _parseResponse(response);
  }

  Future<Map<String, dynamic>> post(
    String path, {
    dynamic data,
  }) async {
    final response = await http
        .post(
          Uri.parse('${ApiConstants.baseUrl}$path'),
          headers: _buildHeaders(),
          body: data != null ? jsonEncode(data) : null,
        )
        .timeout(_timeout);
    return _parseResponse(response);
  }

  Future<Map<String, dynamic>> put(
    String path, {
    dynamic data,
  }) async {
    final response = await http
        .put(
          Uri.parse('${ApiConstants.baseUrl}$path'),
          headers: _buildHeaders(),
          body: data != null ? jsonEncode(data) : null,
        )
        .timeout(_timeout);
    return _parseResponse(response);
  }

  Future<Map<String, dynamic>> patch(
    String path, {
    dynamic data,
  }) async {
    final request =
        http.Request('PATCH', Uri.parse('${ApiConstants.baseUrl}$path'));
    request.headers.addAll(_buildHeaders());
    if (data != null) {
      request.body = jsonEncode(data);
    }
    final streamedResponse = await request.send().timeout(_timeout);
    final response = await http.Response.fromStream(streamedResponse);
    return _parseResponse(response);
  }

  Future<Map<String, dynamic>> delete(String path) async {
    final response = await http
        .delete(
          Uri.parse('${ApiConstants.baseUrl}$path'),
          headers: _buildHeaders(),
        )
        .timeout(_timeout);
    return _parseResponse(response);
  }

  Map<String, dynamic> _parseResponse(http.Response response) {
    if (response.body.isEmpty) {
      return {
        'success': response.statusCode >= 200 && response.statusCode < 300,
        'statusCode': response.statusCode,
      };
    }

    final Map<String, dynamic> body =
        jsonDecode(response.body) as Map<String, dynamic>;

    // Override success based on HTTP status code if body doesn't have it
    if (response.statusCode >= 200 && response.statusCode < 300) {
      body['success'] = body['success'] ?? true;
      body['statusCode'] = response.statusCode;
    } else {
      body['success'] = body['success'] ?? false;
      body['statusCode'] = response.statusCode;
      if (body['message'] == null) {
        body['message'] = 'Request failed with status ${response.statusCode}';
      }
    }

    return body;
  }
}

class ApiResponse<T> {
  final bool success;
  final String message;
  final T? data;

  ApiResponse({required this.success, required this.message, this.data});

  factory ApiResponse.fromJson(
    Map<String, dynamic> json,
    T Function(dynamic)? fromJsonT,
  ) {
    return ApiResponse(
      success: json['success'] ?? false,
      message: json['message'] ?? '',
      data: json['data'] != null && fromJsonT != null
          ? fromJsonT(json['data'])
          : null,
    );
  }
}
