package com.smartmeal.controller;

import com.smartmeal.model.RecommendationHistory;
import com.smartmeal.model.MenuItem;
import com.smartmeal.model.SmaPreference;
import com.smartmeal.model.enums.MealType;
import com.smartmeal.service.AnalyticsService;
import com.smartmeal.service.GeminiService;
import com.smartmeal.repository.SmaPreferenceRepository;
import com.smartmeal.repository.RecommendationHistoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {

    private final GeminiService geminiService;
    private final SmaPreferenceRepository preferenceRepository;
    private final RecommendationHistoryRepository historyRepository;
    private final AnalyticsService analyticsService;

    public RecommendationController(GeminiService geminiService,
                                   SmaPreferenceRepository preferenceRepository,
                                   RecommendationHistoryRepository historyRepository,
                                   AnalyticsService analyticsService) {
        this.geminiService = geminiService;
        this.preferenceRepository = preferenceRepository;
        this.historyRepository = historyRepository;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/meal")
    public ResponseEntity<?> getMealRecommendation(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam MealType mealType) {
        
        try {
            MenuItem recommendation = geminiService.getAiRecommendation(userId, mealType);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", Map.of(
                    "menuItem", recommendation,
                    "mealType", mealType,
                    "message", "AI-recommended " + mealType.getDisplayName() + " based on your preferences"
                )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(@RequestHeader("X-User-Id") Long userId) {
        List<RecommendationHistory> history = historyRepository.findTop10ByUserIdOrderByRecommendedAtDesc(userId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", history
        ));
    }

    @GetMapping("/sma-status")
    public ResponseEntity<?> getSmaStatus(@RequestHeader("X-User-Id") Long userId) {
        List<SmaPreference> prefs = preferenceRepository.findByUserId(userId);
        boolean isEnabled = prefs.stream().anyMatch(p -> Boolean.TRUE.equals(p.getIsActive()));
        
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", Map.of(
                "isEnabled", isEnabled,
                "preferencesCount", prefs.size()
            )
        ));
    }
    
    @GetMapping("/analytics/stats")
    public ResponseEntity<?> getRecommendationStats(@RequestHeader("X-User-Id") Long userId) {
        Map<String, Object> stats = analyticsService.getUserRecommendationStats(userId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", stats
        ));
    }
    
    @GetMapping("/analytics/nutrition-summary")
    public ResponseEntity<?> getNutritionSummary(@RequestHeader("X-User-Id") Long userId) {
        Map<String, Object> summary = analyticsService.getWeeklyNutritionSummary(userId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", summary
        ));
    }
    
    @GetMapping("/analytics/popular")
    public ResponseEntity<?> getPopularRecommendations() {
        List<Map<String, Object>> popular = analyticsService.getPopularRecommendations();
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", popular
        ));
    }
}
