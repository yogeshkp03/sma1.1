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
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalRecommendations", totalRecommendations);
        result.put("mealTypeDistribution", mealTypeCounts);
        result.put("averageCalories", Math.round(avgCalories));
        result.put("mostActiveMealType", mealTypeCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None"));
        return result;
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
            if (h.getRecommendedAt() != null) {
                String day = h.getRecommendedAt().toLocalDate().toString();
                caloriesByDay.merge(day, h.getCalories() != null ? h.getCalories() : 0, Integer::sum);
            }
        });
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalMeals", totalMeals);
        result.put("totalCalories", totalCalories);
        result.put("averageCaloriesPerDay", totalMeals > 0 ? totalCalories / 7 : 0);
        result.put("caloriesByDay", caloriesByDay);
        return result;
    }

    public List<Map<String, Object>> getPopularRecommendations() {
        List<RecommendationHistory> allHistory = historyRepository.findAll();
        
        return allHistory.stream()
                .collect(Collectors.groupingBy(h -> h.getMenuItemName() != null ? h.getMenuItemName() : "Unknown", Collectors.counting()))
                .entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(10)
                .map(e -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("itemName", e.getKey());
                    item.put("orderCount", e.getValue());
                    return item;
                })
                .collect(Collectors.toList());
    }

    public Map<String, Object> getDietPreferencesAnalysis(Long userId) {
        List<RecommendationHistory> history = historyRepository.findByUserId(userId);
        
        long vegCount = history.stream()
                .filter(h -> h.getRestaurantName() != null)
                .count();
        
        Map<String, Object> result = new HashMap<>();
        result.put("totalMeals", history.size());
        result.put("vegetarianMeals", vegCount);
        result.put("nonVegetarianMeals", history.size() - vegCount);
        result.put("dietPreference", vegCount > (history.size() / 2) ? "Vegetarian" : "Non-Vegetarian");
        return result;
    }
}
