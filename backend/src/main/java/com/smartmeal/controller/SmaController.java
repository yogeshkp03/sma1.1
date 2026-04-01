package com.smartmeal.controller;

import com.smartmeal.dto.response.ApiResponse;
import com.smartmeal.model.enums.MealType;
import com.smartmeal.service.GeminiService;
import com.smartmeal.service.SmaPreferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/sma")
public class SmaController {

    private final SmaPreferenceService smaPreferenceService;
    private final GeminiService geminiService;

    public SmaController(SmaPreferenceService smaPreferenceService, GeminiService geminiService) {
        this.smaPreferenceService = smaPreferenceService;
        this.geminiService = geminiService;
    }

    @PostMapping("/skip-meal")
    public ResponseEntity<ApiResponse<Map<String, Object>>> skipMeal(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam MealType mealType,
            @RequestParam(required = false) String reason) {
        
        smaPreferenceService.skipScheduledMeal(userId, mealType, reason);
        
        return ResponseEntity.ok(ApiResponse.success(Map.of(
            "skipped", true,
            "message", "Meal skipped. Next recommendation will be at your next scheduled time."
        )));
    }

    @PostMapping("/postpone-meal")
    public ResponseEntity<ApiResponse<Map<String, Object>>> postponeMeal(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam MealType mealType,
            @RequestParam(defaultValue = "30") int minutes) {
        
        smaPreferenceService.postponeMeal(userId, mealType, minutes);
        
        return ResponseEntity.ok(ApiResponse.success(Map.of(
            "postponed", true,
            "newTime", minutes,
            "message", "Meal postponed by " + minutes + " minutes"
        )));
    }

    @PostMapping("/feedback")
    public ResponseEntity<ApiResponse<Map<String, Object>>> provideFeedback(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Long recommendationId,
            @RequestParam String feedback,
            @RequestParam(required = false) String comment) {
        
        smaPreferenceService.recordFeedback(userId, recommendationId, feedback, comment);
        
        return ResponseEntity.ok(ApiResponse.success(Map.of(
            "recorded", true,
            "message", "Thank you for your feedback!"
        )));
    }

    @GetMapping("/weekly-summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getWeeklySummary(
            @RequestHeader("X-User-Id") Long userId) {
        
        Map<String, Object> summary = smaPreferenceService.getWeeklySummary(userId);
        
        return ResponseEntity.ok(ApiResponse.success(summary));
    }
}