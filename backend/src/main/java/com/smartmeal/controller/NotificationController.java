package com.smartmeal.controller;

import com.smartmeal.dto.response.ApiResponse;
import com.smartmeal.model.User;
import com.smartmeal.repository.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final UserRepository userRepository;

    public NotificationController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/register-token")
    public ResponseEntity<ApiResponse<Void>> registerToken(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody FcmTokenRequest request) {
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
        }

        user.setFcmToken(request.getToken());
        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse<Void>(true, "FCM token registered successfully", null));
    }

    @PostMapping("/update-preferences")
    public ResponseEntity<ApiResponse<Void>> updatePreferences(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody NotificationPreferencesRequest request) {
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
        }

        user.setNotificationsEnabled(request.getEnabled());
        userRepository.save(user);

        return ResponseEntity.ok(new ApiResponse<Void>(true, "Preferences updated successfully", null));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Boolean>> getNotificationStatus(
            @RequestHeader("X-User-Id") Long userId) {
        
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
        }

        boolean enabled = user.getNotificationsEnabled() != null && user.getNotificationsEnabled();
        return ResponseEntity.ok(ApiResponse.success(enabled));
    }
}

@Data
class FcmTokenRequest {
    @NotBlank(message = "FCM token is required")
    private String token;
}

@Data
class NotificationPreferencesRequest {
    private Boolean enabled;
}