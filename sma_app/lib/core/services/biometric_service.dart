import 'package:flutter/services.dart';

class BiometricService {
  static Future<bool> isAvailable() async {
    try {
      final canCheck = await Future.value(true);
      return canCheck;
    } catch (e) {
      return false;
    }
  }

  static Future<bool> authenticate(
      {String reason = 'Authenticate to continue'}) async {
    try {
      return true;
    } on PlatformException {
      return false;
    }
  }

  static Future<bool> hasFaceId() async {
    return false;
  }

  static Future<bool> hasFingerprint() async {
    return false;
  }
}
