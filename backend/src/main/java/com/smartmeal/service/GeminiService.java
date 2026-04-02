package com.smartmeal.service;

import com.smartmeal.model.*;
import com.smartmeal.model.enums.MealType;
import com.smartmeal.model.enums.DietType;
import com.smartmeal.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class GeminiService {

    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    private final SmaPreferenceRepository smaPreferenceRepository;
    private final RecommendationHistoryRepository historyRepository;
    private final WebClient webClient;

    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private static final String API_KEY = System.getenv("GEMINI_API_KEY");

    public GeminiService(MenuItemRepository menuItemRepository,
                        RestaurantRepository restaurantRepository,
                        SmaPreferenceRepository smaPreferenceRepository,
                        RecommendationHistoryRepository historyRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
        this.smaPreferenceRepository = smaPreferenceRepository;
        this.historyRepository = historyRepository;
        
        this.webClient = WebClient.builder()
            .baseUrl(GEMINI_API_URL)
            .build();
    }

    public MenuItem getAiRecommendation(Long userId, MealType mealType) {
        Optional<SmaPreference> prefOpt = smaPreferenceRepository.findByUserIdAndMealType(userId, mealType);
        
        if (prefOpt.isEmpty()) {
            return getFallbackRecommendation(mealType);
        }

        SmaPreference pref = prefOpt.get();
        
        List<MenuItem> candidates = menuItemRepository.findByFilters(
            pref.getDietType(),
            pref.getMinCalories(),
            pref.getMaxCalories(),
            pref.getMaxBudget(),
            pref.getMinProtein()
        );

        if (candidates.isEmpty()) {
            candidates = menuItemRepository.findAll();
        }

        if (candidates.isEmpty()) {
            return getFallbackRecommendation(mealType);
        }

        return getGeminiRecommendation(candidates, pref, mealType);
    }

    private MenuItem getGeminiRecommendation(List<MenuItem> candidates, SmaPreference pref, MealType mealType) {
        if (API_KEY == null || API_KEY.isEmpty()) {
            System.out.println("Gemini API key not configured, using fallback");
            return selectBestMatch(candidates, pref, mealType);
        }

        try {
            String prompt = buildPrompt(candidates, pref, mealType);
            String aiResponse = callGeminiApi(prompt);
            return parseAiResponse(aiResponse, candidates);
        } catch (Exception e) {
            System.err.println("Gemini API call failed: " + e.getMessage());
            return selectBestMatch(candidates, pref, mealType);
        }
    }

    private String buildPrompt(List<MenuItem> candidates, SmaPreference pref, MealType mealType) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a meal recommendation assistant. ");
        sb.append("Based on the user's preferences, recommend the best menu item from the list below.\n\n");
        
        sb.append("User Preferences:\n");
        sb.append("- Meal Type: ").append(mealType.getDisplayName()).append("\n");
        if (pref.getDietType() != null) {
            sb.append("- Diet Type: ").append(pref.getDietType()).append("\n");
        }
        if (pref.getMinCalories() != null) {
            sb.append("- Minimum Calories: ").append(pref.getMinCalories()).append("\n");
        }
        if (pref.getMaxCalories() != null) {
            sb.append("- Maximum Calories: ").append(pref.getMaxCalories()).append("\n");
        }
        if (pref.getMinProtein() != null) {
            sb.append("- Minimum Protein (g): ").append(pref.getMinProtein()).append("\n");
        }
        if (pref.getMaxBudget() != null) {
            sb.append("- Maximum Budget: $").append(pref.getMaxBudget()).append("\n");
        }
        
        sb.append("\nAvailable Menu Items:\n");
        for (int i = 0; i < candidates.size(); i++) {
            MenuItem item = candidates.get(i);
            sb.append(i + 1).append(". ").append(item.getName());
            sb.append(" (Restaurant: ").append(item.getRestaurant() != null ? item.getRestaurant().getName() : "N/A").append(")");
            if (item.getCalories() != null) sb.append(" - ").append(item.getCalories()).append(" cal");
            if (item.getProteinGrams() != null) sb.append(" - ").append(item.getProteinGrams()).append("g protein");
            if (item.getPrice() != null) sb.append(" - $").append(item.getPrice());
            if (item.getDietType() != null) sb.append(" - ").append(item.getDietType());
            sb.append("\n");
        }
        
        sb.append("\nBased on these preferences and available items, recommend the best choice. ");
        sb.append("Respond ONLY with the number of the menu item (1-").append(candidates.size()).append("). ");
        sb.append("If no suitable item exists, respond with the number of the closest match.");
        
        return sb.toString();
    }

    private String callGeminiApi(String prompt) {
        Map<String, Object> requestBody = Map.of(
            "contents", new Object[] {
                Map.of("parts", new Object[] {
                    Map.of("text", prompt)
                })
            },
            "generationConfig", Map.of(
                "temperature", 0.7,
                "maxOutputTokens", 50,
                "topP", 0.9,
                "topK", 40
            )
        );

        String response = webClient.post()
            .uri(uriBuilder -> uriBuilder
                .queryParam("key", API_KEY)
                .build())
            .bodyValue(requestBody)
            .header("Content-Type", "application/json")
            .retrieve()
            .bodyToMono(String.class)
            .block();

        return response;
    }

    private MenuItem parseAiResponse(String aiResponse, List<MenuItem> candidates) {
        try {
            String text = extractTextFromResponse(aiResponse);
            if (text == null || text.isEmpty()) {
                return selectBestMatch(candidates, null, null);
            }

            text = text.replaceAll("[^0-9]", "").trim();
            if (text.isEmpty()) {
                return selectBestMatch(candidates, null, null);
            }

            int index = Integer.parseInt(text) - 1;
            if (index >= 0 && index < candidates.size()) {
                MenuItem selected = candidates.get(index);
                saveRecommendationHistory(null, selected, null);
                return selected;
            }
        } catch (Exception e) {
            System.err.println("Failed to parse AI response: " + e.getMessage());
        }
        
        return selectBestMatch(candidates, null, null);
    }

    private String extractTextFromResponse(String response) {
        try {
            String[] lines = response.split("\n");
            for (String line : lines) {
                if (line.contains("\"text\"")) {
                    int start = line.indexOf("\"text\"") + 7;
                    int end = line.lastIndexOf("\"");
                    if (end > start) {
                        return line.substring(start, end);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error extracting text: " + e.getMessage());
        }
        return null;
    }

    private MenuItem selectBestMatch(List<MenuItem> candidates, SmaPreference pref, MealType mealType) {
        if (candidates.isEmpty()) return null;
        
        double maxScore = -1;
        MenuItem best = candidates.get(0);

        for (MenuItem item : candidates) {
            double score = calculateMatchScore(item, pref, mealType);
            if (score > maxScore) {
                maxScore = score;
                best = item;
            }
        }
        
        return best;
    }

    private double calculateMatchScore(MenuItem item, SmaPreference pref, MealType mealType) {
        double score = 0.0;

        if (pref != null) {
            if (pref.getDietType() != null && item.getDietType() == pref.getDietType()) {
                score += 30;
            }

            if (pref.getMinProtein() != null && item.getProteinGrams() != null) {
                if (item.getProteinGrams().compareTo(pref.getMinProtein()) >= 0) {
                    score += 20;
                }
            }

            if (pref.getMaxBudget() != null && item.getPrice() != null) {
                if (item.getPrice().compareTo(pref.getMaxBudget()) <= 0) {
                    score += 25;
                }
            }

            if (pref.getMinCalories() != null && item.getCalories() != null) {
                if (item.getCalories() >= pref.getMinCalories()) {
                    score += 15;
                }
            }
        }

        score += new Random().nextDouble() * 10;

        return score;
    }

    private MenuItem getFallbackRecommendation(MealType mealType) {
        List<MenuItem> items = menuItemRepository.findAll();
        if (items.isEmpty()) {
            return null;
        }
        
        DietType preferredDiet = switch (mealType) {
            case MORNING_FUEL -> DietType.VEG;
            case POWER_HOUR -> DietType.NON_VEG;
            case TWILIGHT_FEAST -> DietType.VEG;
            case CRAVE_CORNER -> DietType.EGGETARIAN;
        };
        
        List<MenuItem> filtered = items.stream()
            .filter(i -> i.getDietType() == preferredDiet)
            .toList();
        
        if (filtered.isEmpty()) {
            filtered = items;
        }
        
        return filtered.get(new Random().nextInt(filtered.size()));
    }

    private void saveRecommendationHistory(Long userId, MenuItem item, MealType mealType) {
        if (item == null) return;
        
        RecommendationHistory history = RecommendationHistory.builder()
            .userId(userId)
            .menuItemId(item.getId())
            .menuItemName(item.getName())
            .restaurantId(item.getRestaurant() != null ? item.getRestaurant().getId() : null)
            .restaurantName(item.getRestaurant() != null ? item.getRestaurant().getName() : null)
            .mealType(mealType)
            .calories(item.getCalories())
            .recommendedAt(LocalDateTime.now())
            .build();
        
        historyRepository.save(history);
    }

    public List<MenuItem> getMultipleRecommendations(Long userId, MealType mealType, int count) {
        List<MenuItem> recommendations = new ArrayList<>();
        Set<Long> usedIds = new HashSet<>();
        
        for (int i = 0; i < count * 2 && recommendations.size() < count; i++) {
            MenuItem rec = getAiRecommendation(userId, mealType);
            if (rec != null && usedIds.add(rec.getId())) {
                recommendations.add(rec);
            }
        }
        
        return recommendations;
    }
}
