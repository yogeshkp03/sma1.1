package com.smartmeal.service;

import com.smartmeal.model.MenuItem;
import com.smartmeal.model.Restaurant;
import com.smartmeal.model.SmaPreference;
import com.smartmeal.model.enums.DietType;
import com.smartmeal.model.enums.MealType;
import com.smartmeal.repository.MenuItemRepository;
import com.smartmeal.repository.RecommendationHistoryRepository;
import com.smartmeal.repository.RestaurantRepository;
import com.smartmeal.repository.SmaPreferenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeminiServiceTest {

    @Mock
    private MenuItemRepository menuItemRepository;
    
    @Mock
    private RestaurantRepository restaurantRepository;
    
    @Mock
    private SmaPreferenceRepository smaPreferenceRepository;
    
    @Mock
    private RecommendationHistoryRepository historyRepository;

    private GeminiService geminiService;

    @BeforeEach
    void setUp() {
        geminiService = new GeminiService(
            menuItemRepository,
            restaurantRepository,
            smaPreferenceRepository,
            historyRepository
        );
    }

    @Test
    void testGetAiRecommendation_NoPreference_ReturnsFallback() {
        when(smaPreferenceRepository.findByUserIdAndMealType(1L, MealType.MORNING_FUEL))
            .thenReturn(Optional.empty());
        when(menuItemRepository.findAll()).thenReturn(Collections.emptyList());

        MenuItem result = geminiService.getAiRecommendation(1L, MealType.MORNING_FUEL);

        assertNull(result);
    }

    @Test
    void testGetAiRecommendation_WithPreference_ReturnsMenuItem() {
        Restaurant restaurant = createTestRestaurant();
        MenuItem menuItem = createTestMenuItem(restaurant);
        
        SmaPreference preference = SmaPreference.builder()
            .id(1L)
            .userId(1L)
            .mealType(MealType.MORNING_FUEL)
            .dietType(DietType.VEG)
            .minCalories(200)
            .maxCalories(600)
            .minProtein(BigDecimal.valueOf(10))
            .maxBudget(BigDecimal.valueOf(300))
            .build();

        when(smaPreferenceRepository.findByUserIdAndMealType(1L, MealType.MORNING_FUEL))
            .thenReturn(Optional.of(preference));
        when(menuItemRepository.findByFilters(any(), any(), any(), any(), any()))
            .thenReturn(List.of(menuItem));
        when(historyRepository.save(any())).thenReturn(null);

        MenuItem result = geminiService.getAiRecommendation(1L, MealType.MORNING_FUEL);

        assertNotNull(result);
        assertEquals("Test Burger", result.getName());
    }

    @Test
    void testGetAiRecommendation_NoMatchingItems_FallsBackToAllItems() {
        Restaurant restaurant = createTestRestaurant();
        MenuItem menuItem = createTestMenuItem(restaurant);
        
        SmaPreference preference = SmaPreference.builder()
            .id(1L)
            .userId(1L)
            .mealType(MealType.MORNING_FUEL)
            .dietType(DietType.VEG)
            .build();

        when(smaPreferenceRepository.findByUserIdAndMealType(1L, MealType.MORNING_FUEL))
            .thenReturn(Optional.of(preference));
        when(menuItemRepository.findByFilters(any(), any(), any(), any(), any()))
            .thenReturn(Collections.emptyList());
        when(menuItemRepository.findAll()).thenReturn(List.of(menuItem));
        when(historyRepository.save(any())).thenReturn(null);

        MenuItem result = geminiService.getAiRecommendation(1L, MealType.MORNING_FUEL);

        assertNotNull(result);
    }

    @Test
    void testSelectBestMatch_EmptyList_ReturnsNull() {
        List<MenuItem> emptyList = Collections.emptyList();
        
        MenuItem result = geminiService.selectBestMatch(emptyList, null, MealType.MORNING_FUEL);
        
        assertNull(result);
    }

    @Test
    void testSelectBestMatch_SingleItem_ReturnsItem() {
        Restaurant restaurant = createTestRestaurant();
        MenuItem menuItem = createTestMenuItem(restaurant);
        
        MenuItem result = geminiService.selectBestMatch(List.of(menuItem), null, MealType.MORNING_FUEL);
        
        assertNotNull(result);
        assertEquals("Test Burger", result.getName());
    }

    @Test
    void testGetMultipleRecommendations_ReturnsUniqueItems() {
        Restaurant restaurant = createTestRestaurant();
        MenuItem menuItem1 = createTestMenuItem(restaurant);
        menuItem1.setId(1L);
        
        MenuItem menuItem2 = createTestMenuItem(restaurant);
        menuItem2.setId(2L);
        menuItem2.setName("Test Pizza");
        
        SmaPreference preference = SmaPreference.builder()
            .id(1L)
            .userId(1L)
            .mealType(MealType.MORNING_FUEL)
            .build();

        when(smaPreferenceRepository.findByUserIdAndMealType(1L, MealType.MORNING_FUEL))
            .thenReturn(Optional.of(preference));
        when(menuItemRepository.findByFilters(any(), any(), any(), any(), any()))
            .thenReturn(List.of(menuItem1, menuItem2));
        when(historyRepository.save(any())).thenReturn(null);

        List<MenuItem> results = geminiService.getMultipleRecommendations(1L, MealType.MORNING_FUEL, 2);
        
        assertTrue(results.size() <= 2);
    }

    private Restaurant createTestRestaurant() {
        return Restaurant.builder()
            .id(1L)
            .name("Test Restaurant")
            .location("Delhi")
            .cuisine("Fast Food")
            .build();
    }

    private MenuItem createTestMenuItem(Restaurant restaurant) {
        return MenuItem.builder()
            .id(1L)
            .restaurant(restaurant)
            .name("Test Burger")
            .description("Delicious burger")
            .price(BigDecimal.valueOf(200))
            .dietType(DietType.VEG)
            .calories(350)
            .proteinGrams(BigDecimal.valueOf(15))
            .carbsGrams(BigDecimal.valueOf(40))
            .fatGrams(BigDecimal.valueOf(12))
            .isAvailable(true)
            .build();
    }
}