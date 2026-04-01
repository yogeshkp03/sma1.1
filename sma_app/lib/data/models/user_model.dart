class AppUser {
  final int id;
  final String? firebaseUid;
  final String email;
  final String? fullName;
  final String? phone;
  final String? profileImageUrl;

  AppUser({
    required this.id,
    this.firebaseUid,
    required this.email,
    this.fullName,
    this.phone,
    this.profileImageUrl,
  });

  factory AppUser.fromJson(Map<String, dynamic> json) {
    return AppUser(
      id: json['id'] ?? 0,
      firebaseUid: json['firebaseUid'] ?? '',
      email: json['email'] ?? '',
      fullName: json['fullName'],
      phone: json['phone'],
      profileImageUrl: json['profileImageUrl'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'firebaseUid': firebaseUid,
      'email': email,
      'fullName': fullName,
      'phone': phone,
      'profileImageUrl': profileImageUrl,
    };
  }
}

class AuthResponse {
  final String token;
  final int userId;
  final String email;
  final String? fullName;
  final String? firebaseUid;

  AuthResponse({
    required this.token,
    required this.userId,
    required this.email,
    this.fullName,
    this.firebaseUid,
  });

  factory AuthResponse.fromJson(Map<String, dynamic> json) {
    return AuthResponse(
      token: json['token'] ?? '',
      userId: json['userId'] ?? 0,
      email: json['email'] ?? '',
      fullName: json['fullName'],
      firebaseUid: json['firebaseUid'],
    );
  }
}
