package com.smartmeal.service;

import com.smartmeal.model.RecommendationHistory;
import com.smartmeal.repository.RecommendationHistoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final RecommendationHistoryRepository historyRepository;

    public AnalyticsService(RecommendationHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    public Map<String, Object> getUserRecommendationStats(Long userId) {
        List<RecommendationHistory> history = historyRepository.findByUserId(userId);
        
        int totalRecommendations = history.size();
        Map<String, Long> mealTypeCounts = history.stream()
                .collect(Collectors.groupingBy(h -> h.getMealType() != null ? h.getMealType().name() : "UNKNOWN", Collectors.counting()));
        
        double avgCalories = history.stream()
                .filter(h -> h.getCalories() != null)
                .mapToInt(h -> h.getCalories())
                .average()
                .orElse(0);
        
        return Map.of(
            "totalRecommendations", totalRecommendations,
            "mealTypeDistribution", mealTypeCounts,
            "averageCalories", Math.round(avgCalories),
            "mostActiveMealType", mealTypeCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("None")
        );
    }

    public Map<String, Object> getWeeklyNutritionSummary(Long userId) {
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        
        List<RecommendationHistory> history = historyRepository
                .findByUserIdAndRecommendedAtAfter(userId, weekAgo);
        
        int totalMeals = history.size();
        int totalCalories = history.stream()
                .filter(h -> h.getCalories() != null)
                .mapToInt(h -> h.getCalories())
                .sum();
        
        Map<String, Integer> caloriesByDay = new HashMap<>();
        history.forEach(h -> {
            String day = h.getRecommendedAt().toLocalDate().toString();
            caloriesByDay.merge(day, h.getCalories() != null ? h.getCalories() : 0, Integer::sum);
        });
        
        return Map.of(
            "totalMeals", totalMeals,
            "totalCalories", totalCalories,
            "averageCaloriesPerDay", totalMeals > 0 ? totalCalories / 7 : 0,
            "caloriesByDay", caloriesByDay
        );
    }

    public List<Map<String, Object>> getPopularRecommendations() {
        List<RecommendationHistory> allHistory = historyRepository.findAll();
        
        return allHistory.stream()
                .collect(Collectors.groupingBy(h -> h.getMenuItemName(), Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(10)
                .map(e -> Map.of("itemName", e.getKey(), "orderCount", e.getValue()))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getDietPreferencesAnalysis(Long userId) {
        List<RecommendationHistory> history = historyRepository.findByUserId(userId);
        
        long vegCount = history.stream()
                .filter(h -> h.getRestaurantName() != null)
                .count();
        
        return Map.of(
            "totalMeals", history.size(),
            "vegetarianMeals", vegCount,
            "nonVegetarianMeals", history.size() - vegCount,
            "dietPreference", vegCount > (history.size() / 2) ? "Vegetarian" : "Non-Vegetarian"
        );
    }
}