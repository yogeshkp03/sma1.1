package com.smartmeal.service;

import com.smartmeal.model.*;
import com.smartmeal.model.enums.DietType;
import com.smartmeal.model.enums.MealType;
import com.smartmeal.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocalRecommendationService {

    private static final int VARIETY_WINDOW_DAYS = 7;
    private static final int HISTORY_WEIGHT_DAYS = 30;
    private static final double DECAY_RATE = 0.9;

    private static final double PROTEIN_WEIGHT = 20;
    private static final double CALORIE_WEIGHT = 15;
    private static final double MACRO_LIMITS_WEIGHT = 10;
    private static final double HISTORY_WEIGHT = 25;
    private static final double RESTAURANT_AFFINITY_WEIGHT = 15;
    private static final double POPULARITY_WEIGHT = 10;
    private static final double VARIETY_WEIGHT = 20;
    private static final double RANDOM_WEIGHT = 5;

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final SmaPreferenceRepository smaPreferenceRepository;
    private final RecommendationHistoryRepository historyRepository;

    public LocalRecommendationService(MenuItemRepository menuItemRepository,
                                       RestaurantRepository restaurantRepository,
                                       SmaPreferenceRepository smaPreferenceRepository,
                                       RecommendationHistoryRepository historyRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.smaPreferenceRepository = smaPreferenceRepository;
        this.historyRepository = historyRepository;
    }

    public MenuItem getRecommendation(Long userId, MealType mealType) {
        List<SmaPreference> preferences = smaPreferenceRepository.findByUserIdAndMealType(userId, mealType);

        if (preferences.isEmpty()) {
            return getFallbackRecommendation(userId, mealType);
        }

        SmaPreference pref = preferences.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .findFirst()
                .orElse(preferences.get(0));

        List<MenuItem> allItems = menuItemRepository.findAllWithRestaurant();

        List<MenuItem> filtered = filterByHardConstraints(allItems, pref);

        if (filtered.isEmpty()) {
            return getFallbackRecommendation(userId, mealType);
        }

        List<RecommendationHistory> recentHistory = getRecentHistory(userId, HISTORY_WEIGHT_DAYS);
        Map<Long, Double> menuItemScores = getPopularityScores();
        Map<Long, Long> restaurantOrderCounts = calculateRestaurantAffinity(userId);

        Set<Long> recentItemIds = getRecentItemIds(userId, VARIETY_WINDOW_DAYS);
        Set<Long> preferredRestaurantIds = getPreferredRestaurantIds(restaurantOrderCounts);

        MenuItem best = null;
        double maxScore = -1;

        for (MenuItem item : filtered) {
            double score = calculateScore(item, pref, recentHistory, menuItemScores, 
                                        restaurantOrderCounts, preferredRestaurantIds, recentItemIds);
            if (score > maxScore) {
                maxScore = score;
                best = item;
            }
        }

        if (best != null) {
            saveRecommendationHistory(userId, best, mealType);
        }

        return best;
    }
    
    public List<MenuItem> getTopNRecommendations(Long userId, MealType mealType, int count) {
        List<SmaPreference> preferences = smaPreferenceRepository.findByUserIdAndMealType(userId, mealType);
        
        if (preferences.isEmpty()) {
            return getNFallbackRecommendations(userId, mealType, count);
        }
        
        SmaPreference pref = preferences.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .findFirst()
                .orElse(preferences.get(0));
        
        List<MenuItem> allItems = menuItemRepository.findAllWithRestaurant();
        List<MenuItem> filtered = filterByHardConstraints(allItems, pref);
        
        if (filtered.isEmpty()) {
            return getNFallbackRecommendations(userId, mealType, count);
        }
        
        List<RecommendationHistory> recentHistory = getRecentHistory(userId, HISTORY_WEIGHT_DAYS);
        Map<Long, Double> menuItemScores = getPopularityScores();
        Map<Long, Long> restaurantOrderCounts = calculateRestaurantAffinity(userId);
        Set<Long> recentItemIds = getRecentItemIds(userId, VARIETY_WINDOW_DAYS);
        Set<Long> preferredRestaurantIds = getPreferredRestaurantIds(restaurantOrderCounts);
        
        // Score all items and sort by score descending
        List<ScoredItem> scoredItems = new ArrayList<>();
        Set<Long> addedItemIds = new HashSet<>();
        
        for (MenuItem item : filtered) {
            if (addedItemIds.contains(item.getId())) {
                continue;
            }
            double score = calculateScore(item, pref, recentHistory, menuItemScores, 
                                        restaurantOrderCounts, preferredRestaurantIds, recentItemIds);
            scoredItems.add(new ScoredItem(item, score));
            addedItemIds.add(item.getId());
        }
        
        // Sort by score and take top N
        scoredItems.sort((a, b) -> Double.compare(b.score, a.score));
        
        List<MenuItem> topItems = scoredItems.stream()
                .limit(count)
                .map(ScoredItem::getItem)
                .collect(Collectors.toList());
        
        // Save history for all top items
        for (MenuItem item : topItems) {
            saveRecommendationHistory(userId, item, mealType);
        }
        
        return topItems;
    }
    
    private List<MenuItem> getNFallbackRecommendations(Long userId, MealType mealType, int count) {
        List<MenuItem> allItems = menuItemRepository.findAllWithRestaurant();
        
        // Simple randomization for fallback
        Collections.shuffle(allItems);
        
        List<MenuItem> fallbackItems = allItems.stream()
                .limit(count)
                .collect(Collectors.toList());
        
        for (MenuItem item : fallbackItems) {
            saveRecommendationHistory(userId, item, mealType);
        }
        
        return fallbackItems;
    }
    
    private static class ScoredItem {
        private final MenuItem item;
        private final double score;
        
        ScoredItem(MenuItem item, double score) {
            this.item = item;
            this.score = score;
        }
        
        MenuItem getItem() { return item; }
        double getScore() { return score; }
    }

    private List<MenuItem> filterByHardConstraints(List<MenuItem> items, SmaPreference pref) {
        DietType dietType = pref.getDietType();
        BigDecimal maxBudget = pref.getMaxBudget();

        return items.stream()
            .filter(item -> item.getIsAvailable() == null || item.getIsAvailable())
            .filter(item -> isDietTypeCompatible(item.getDietType(), dietType))
            .filter(item -> maxBudget == null || 
                          (item.getPrice() != null && item.getPrice().compareTo(maxBudget) <= 0))
            .filter(item -> pref.getMinCalories() == null || 
                          (item.getCalories() != null && item.getCalories() >= pref.getMinCalories()))
            .filter(item -> pref.getMaxCalories() == null || 
                          (item.getCalories() == null || item.getCalories() <= pref.getMaxCalories()))
            .filter(item -> pref.getMaxCarbs() == null || 
                          (item.getCarbsGrams() == null || item.getCarbsGrams().compareTo(pref.getMaxCarbs()) <= 0))
            .filter(item -> pref.getMaxFat() == null || 
                          (item.getFatGrams() == null || item.getFatGrams().compareTo(pref.getMaxFat()) <= 0))
            .collect(Collectors.toList());
    }

    private boolean isDietTypeCompatible(DietType itemDietType, DietType preferredDietType) {
        // If no diet restriction, include all items
        if (preferredDietType == DietType.NONE) {
            return true;
        }
        
        // Exact match
        if (itemDietType == preferredDietType) {
            return true;
        }
        
        // Vegetarian includes vegan items (vegan is stricter)
        if (preferredDietType == DietType.VEG && itemDietType == DietType.VEGAN) {
            return true;
        }
        
        return false;
    }

    private double calculateScore(MenuItem item, SmaPreference pref, 
                                  List<RecommendationHistory> history,
                                  Map<Long, Double> popularityScores,
                                  Map<Long, Long> restaurantOrderCounts,
                                  Set<Long> preferredRestaurantIds,
                                  Set<Long> recentItemIds) {
        double score = 0;

        score += calculateProteinScore(item, pref);
        score += calculateCalorieScore(item, pref);
        score += calculateMacroLimitsScore(item, pref);
        score += calculateHistoryScore(item, history);
        score += calculateRestaurantAffinityScore(item, restaurantOrderCounts, preferredRestaurantIds);
        score += calculatePopularityScore(item, popularityScores);
        score += calculateVarietyScore(item, recentItemIds);
        score += calculateRandomScore();

        return score;
    }

    private double calculateProteinScore(MenuItem item, SmaPreference pref) {
        if (pref.getMinProtein() == null || item.getProteinGrams() == null) {
            return PROTEIN_WEIGHT * 0.5;
        }
        if (item.getProteinGrams().compareTo(pref.getMinProtein()) >= 0) {
            return PROTEIN_WEIGHT;
        }
        return 0;
    }

    private double calculateCalorieScore(MenuItem item, SmaPreference pref) {
        if (pref.getMinCalories() == null && pref.getMaxCalories() == null) {
            return CALORIE_WEIGHT * 0.5;
        }
        if (item.getCalories() == null) {
            return CALORIE_WEIGHT * 0.3;
        }

        int cal = item.getCalories();
        boolean meetsMin = pref.getMinCalories() == null || cal >= pref.getMinCalories();
        boolean withinMax = pref.getMaxCalories() == null || cal <= pref.getMaxCalories();

        if (meetsMin && withinMax) {
            return CALORIE_WEIGHT;
        }
        if (meetsMin || withinMax) {
            return CALORIE_WEIGHT * 0.5;
        }
        return 0;
    }

    private double calculateMacroLimitsScore(MenuItem item, SmaPreference pref) {
        double macroScore = 0;
        int checks = 0;

        if (pref.getMaxCarbs() != null && item.getCarbsGrams() != null) {
            checks++;
            if (item.getCarbsGrams().compareTo(pref.getMaxCarbs()) <= 0) {
                macroScore += 1;
            }
        } else {
            checks++;
            macroScore += 0.5;
        }

        if (pref.getMaxFat() != null && item.getFatGrams() != null) {
            checks++;
            if (item.getFatGrams().compareTo(pref.getMaxFat()) <= 0) {
                macroScore += 1;
            }
        } else {
            checks++;
            macroScore += 0.5;
        }

        return checks > 0 ? (macroScore / checks) * MACRO_LIMITS_WEIGHT : MACRO_LIMITS_WEIGHT * 0.5;
    }

    private double calculateHistoryScore(MenuItem item, List<RecommendationHistory> history) {
        if (history == null || history.isEmpty()) {
            return HISTORY_WEIGHT * 0.5;
        }

        LocalDateTime now = LocalDateTime.now();
        double totalScore = 0;
        int matchCount = 0;

        for (RecommendationHistory h : history) {
            if (h.getMenuItemId() != null && h.getMenuItemId().equals(item.getId())) {
                long daysAgo = ChronoUnit.DAYS.between(h.getRecommendedAt(), now);
                double decayFactor = Math.pow(DECAY_RATE, daysAgo / 7.0);
                double weight = h.getAddedToCart() != null && h.getAddedToCart() ? 1.5 : 1.0;
                totalScore += decayFactor * weight;
                matchCount++;
            }
        }

        if (matchCount == 0) {
            return HISTORY_WEIGHT * 0.3;
        }

        double normalizedScore = Math.min(totalScore / (history.size() * 0.5), 1.0);
        return normalizedScore * HISTORY_WEIGHT;
    }

    private double calculateRestaurantAffinityScore(MenuItem item, 
                                                     Map<Long, Long> restaurantOrderCounts,
                                                     Set<Long> preferredRestaurantIds) {
        if (item.getRestaurant() == null) {
            return RESTAURANT_AFFINITY_WEIGHT * 0.5;
        }

        Long restaurantId = item.getRestaurant().getId();
        Long orderCount = restaurantOrderCounts.getOrDefault(restaurantId, 0L);
        boolean isPreferred = preferredRestaurantIds.contains(restaurantId);

        if (isPreferred && orderCount > 0) {
            long totalOrders = restaurantOrderCounts.values().stream().mapToLong(Long::longValue).sum();
            double affinityRatio = (double) orderCount / Math.max(totalOrders, 1);
            return Math.min(affinityRatio * RESTAURANT_AFFINITY_WEIGHT * 2, RESTAURANT_AFFINITY_WEIGHT);
        }

        return RESTAURANT_AFFINITY_WEIGHT * 0.3;
    }

    private double calculatePopularityScore(MenuItem item, Map<Long, Double> popularityScores) {
        if (item.getId() == null || !popularityScores.containsKey(item.getId())) {
            return POPULARITY_WEIGHT * 0.5;
        }
        double normalizedPopularity = Math.min(popularityScores.get(item.getId()), 1.0);
        return normalizedPopularity * POPULARITY_WEIGHT;
    }

    private double calculateVarietyScore(MenuItem item, Set<Long> recentItemIds) {
        if (item.getId() == null || !recentItemIds.contains(item.getId())) {
            return VARIETY_WEIGHT;
        }
        return 0;
    }

    private double calculateRandomScore() {
        return Math.random() * RANDOM_WEIGHT;
    }

    private List<RecommendationHistory> getRecentHistory(Long userId, int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        return historyRepository.findByUserIdAndRecommendedAtAfter(userId, cutoff);
    }

    private Set<Long> getRecentItemIds(Long userId, int days) {
        LocalDateTime start = LocalDateTime.now().minusDays(days);
        LocalDateTime end = LocalDateTime.now();
        List<RecommendationHistory> history = historyRepository
            .findByUserIdAndRecommendedAtBetween(userId, start, end);
        return history.stream()
            .map(RecommendationHistory::getMenuItemId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    private Map<Long, Double> getPopularityScores() {
        List<Object[]> popularityData = historyRepository.getMostRecommendedItems();
        Map<Long, Double> scores = new HashMap<>();

        if (popularityData == null || popularityData.isEmpty()) {
            return scores;
        }

        long maxCount = 0;
        for (Object[] row : popularityData) {
            Long count = ((Number) row[1]).longValue();
            maxCount = Math.max(maxCount, count);
        }

        for (Object[] row : popularityData) {
            Long menuItemId = (Long) row[0];
            Long count = ((Number) row[1]).longValue();
            scores.put(menuItemId, (double) count / maxCount);
        }

        return scores;
    }

    private Map<Long, Long> calculateRestaurantAffinity(Long userId) {
        List<RecommendationHistory> history = historyRepository
            .findByUserIdAndRecommendedAtAfter(userId, LocalDateTime.now().minusDays(HISTORY_WEIGHT_DAYS));

        Map<Long, Long> restaurantCounts = new HashMap<>();
        for (RecommendationHistory h : history) {
            if (h.getRestaurantId() != null) {
                restaurantCounts.merge(h.getRestaurantId(), 1L, Long::sum);
            }
        }
        return restaurantCounts;
    }

    private Set<Long> getPreferredRestaurantIds(Map<Long, Long> restaurantOrderCounts) {
        if (restaurantOrderCounts.isEmpty()) {
            return Collections.emptySet();
        }

        long totalOrders = restaurantOrderCounts.values().stream().mapToLong(Long::longValue).sum();
        double threshold = totalOrders * 0.2;

        return restaurantOrderCounts.entrySet().stream()
            .filter(e -> e.getValue() >= threshold)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    private MenuItem getFallbackRecommendation(Long userId, MealType mealType) {
        List<MenuItem> items = menuItemRepository.findAll();
        if (items.isEmpty()) {
            return null;
        }

        List<RecommendationHistory> history = historyRepository.findTop10ByUserIdOrderByRecommendedAtDesc(userId);
        Set<Long> recentIds = history.stream()
            .map(RecommendationHistory::getMenuItemId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        List<MenuItem> available = items.stream()
            .filter(i -> i.getIsAvailable() == null || i.getIsAvailable())
            .filter(i -> !recentIds.contains(i.getId()))
            .collect(Collectors.toList());

        if (available.isEmpty()) {
            available = items.stream()
                .filter(i -> i.getIsAvailable() == null || i.getIsAvailable())
                .collect(Collectors.toList());
        }

        MenuItem selected = available.get(new Random().nextInt(available.size()));
        saveRecommendationHistory(userId, selected, mealType);
        return selected;
    }

    private void saveRecommendationHistory(Long userId, MenuItem item, MealType mealType) {
        if (item == null) return;

        RecommendationHistory history = RecommendationHistory.builder()
            .userId(userId)
            .menuItemId(item.getId())
            .menuItemName(item.getName())
            .restaurantId(item.getRestaurant() != null ? item.getRestaurant().getId() : null)
            .restaurantName(item.getRestaurant() != null ? item.getRestaurant().getName() : "Unknown")
            .mealType(mealType)
            .calories(item.getCalories())
            .recommendedAt(LocalDateTime.now())
            .build();

        historyRepository.save(history);
    }

    public List<MenuItem> getMultipleRecommendations(Long userId, MealType mealType, int count) {
        List<MenuItem> recommendations = new ArrayList<>();
        Set<Long> usedIds = new HashSet<>();

        for (int i = 0; i < count * 3 && recommendations.size() < count; i++) {
            MenuItem rec = getRecommendation(userId, mealType);
            if (rec != null && usedIds.add(rec.getId())) {
                recommendations.add(rec);
            }
        }

        return recommendations;
    }
}
