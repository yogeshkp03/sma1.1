with open(r'D:\sma\sma_app\lib\data\services\auth_service.dart', 'r') as f:
    content = f.read()

old = """} catch (e) {
      throw 'Registration failed: $e';
    }
  }

  Future<void> signOut()"""

new = """} catch (e) {
      print('Register error: $e');
      throw 'Registration failed: $e';
    }
  }

  Future<void> signOut()"""

content = content.replace(old, new)

with open(r'D:\sma\sma_app\lib\data\services\auth_service.dart', 'w') as f:
    f.write(content)

print('Done')
