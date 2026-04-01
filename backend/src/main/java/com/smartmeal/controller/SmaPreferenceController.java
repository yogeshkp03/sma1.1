package com.smartmeal.controller;

import com.smartmeal.dto.request.SmaPreferenceRequest;
import com.smartmeal.dto.response.ApiResponse;
import com.smartmeal.dto.response.SmaPreferenceResponse;
import com.smartmeal.service.SmaPreferenceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/sma/preferences")
public class SmaPreferenceController {
    
    private final SmaPreferenceService smaPreferenceService;
    
    public SmaPreferenceController(SmaPreferenceService smaPreferenceService) {
        this.smaPreferenceService = smaPreferenceService;
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<SmaPreferenceResponse>>> getPreferences(
            @PathVariable Long userId) {
        List<SmaPreferenceResponse> preferences = smaPreferenceService.getPreferencesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(preferences));
    }
    
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<ApiResponse<List<SmaPreferenceResponse>>> getActivePreferences(
            @PathVariable Long userId) {
        List<SmaPreferenceResponse> preferences = smaPreferenceService.getActivePreferencesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(preferences));
    }
    
    @GetMapping("/{id}/user/{userId}")
    public ResponseEntity<ApiResponse<SmaPreferenceResponse>> getPreference(
            @PathVariable Long id, @PathVariable Long userId) {
        SmaPreferenceResponse preference = smaPreferenceService.getPreferenceById(id, userId);
        if (preference == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Preference not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(preference));
    }
    
    @PostMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<SmaPreferenceResponse>> createPreference(
            @PathVariable Long userId,
            @Valid @RequestBody SmaPreferenceRequest request) {
        SmaPreferenceResponse preference = smaPreferenceService.createPreference(userId, request);
        return ResponseEntity.ok(ApiResponse.success("SMA preference saved for 7 days", preference));
    }
    
    @PutMapping("/{id}/user/{userId}")
    public ResponseEntity<ApiResponse<SmaPreferenceResponse>> updatePreference(
            @PathVariable Long id,
            @PathVariable Long userId,
            @Valid @RequestBody SmaPreferenceRequest request) {
        SmaPreferenceResponse preference = smaPreferenceService.updatePreference(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success("Preference updated successfully", preference));
    }
    
    @PatchMapping("/{id}/user/{userId}/toggle")
    public ResponseEntity<ApiResponse<Void>> togglePreference(
            @PathVariable Long id,
            @PathVariable Long userId,
            @RequestParam boolean active) {
        smaPreferenceService.togglePreference(userId, id, active);
        String message = active ? "Preference activated" : "Preference paused";
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }
    
    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<ApiResponse<Void>> deletePreference(
            @PathVariable Long id, 
            @PathVariable Long userId) {
        smaPreferenceService.deletePreference(userId, id);
        return ResponseEntity.ok(ApiResponse.success("Preference deleted successfully", null));
    }
}
