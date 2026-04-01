with open(r'D:\sma\sma_app\lib\data\services\auth_service.dart', 'r') as f:
    content = f.read()

old = """final data = {
          'email': email,
          'password': password,
          'fullName': name,
        };
      print('Register request data: $data');

      final response = await _apiService.post(
        ApiConstants.register,
        data: data,
      );

      print('Register response status: ${response.statusCode}');
      print('Register response: ${response.data}');

      if (response.data['success'] == true) {"""

new = """final data = {
          'email': email,
          'password': password,
          'fullName': name,
        };

      final fullUrl = '${ApiConstants.baseUrl}${ApiConstants.register}';
      print('=== REGISTER DEBUG ===');
      print('Full URL: $fullUrl');
      print('Data: $data');
      print('======================');

      final response = await _apiService.post(
        ApiConstants.register,
        data: data,
      );

      print('Response status: ${response.statusCode}');
      print('Response data: ${response.data}');

      if (response.data['success'] == true) {"""

content = content.replace(old, new)

with open(r'D:\sma\sma_app\lib\data\services\auth_service.dart', 'w') as f:
    f.write(content)

print('Done')
