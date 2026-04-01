package com.smartmeal.service;

import com.smartmeal.dto.response.MenuItemResponse;
import com.smartmeal.model.MenuItem;
import com.smartmeal.model.Restaurant;
import com.smartmeal.model.enums.DietType;
import com.smartmeal.repository.MenuItemRepository;
import com.smartmeal.repository.RestaurantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MenuItemService {
    
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;
    
    public MenuItemService(MenuItemRepository menuItemRepository, 
                          RestaurantRepository restaurantRepository) {
        this.menuItemRepository = menuItemRepository;
        this.restaurantRepository = restaurantRepository;
    }
    
    public List<MenuItemResponse> getMenuByRestaurant(Long restaurantId) {
        return menuItemRepository.findAvailableByRestaurantId(restaurantId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<MenuItemResponse> getMenuByRestaurant(Long restaurantId, String category) {
        List<MenuItem> items;
        if (category != null && !category.isEmpty()) {
            items = menuItemRepository.findByRestaurantIdAndCategory(restaurantId, category);
        } else {
            items = menuItemRepository.findAvailableByRestaurantId(restaurantId);
        }
        return items.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public Page<MenuItemResponse> getMenuByRestaurantPaginated(Long restaurantId, String category, Pageable pageable) {
        Page<MenuItem> items;
        if (category != null && !category.isEmpty()) {
            items = menuItemRepository.findByRestaurantIdAndCategory(restaurantId, category, pageable);
        } else {
            items = menuItemRepository.findAvailableByRestaurantId(restaurantId, pageable);
        }
        return items.map(this::mapToResponse);
    }
    
    public MenuItemResponse getMenuItemById(Long id) {
        return menuItemRepository.findById(id)
                .map(this::mapToResponse)
                .orElse(null);
    }
    
    public MenuItem getMenuItemEntityById(Long id) {
        return menuItemRepository.findById(id).orElse(null);
    }
    
    public List<MenuItemResponse> getMenuByLocation(String location) {
        return menuItemRepository.findByLocation(location)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<MenuItemResponse> findByFilters(DietType dietType, Integer minCalories, 
                                                  Integer maxCalories, BigDecimal maxPrice, 
                                                  BigDecimal minProtein) {
        return menuItemRepository.findByFilters(dietType, minCalories, maxCalories, maxPrice, minProtein)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public Page<MenuItemResponse> findByFiltersPaginated(String location, DietType dietType, 
            Integer minCalories, Integer maxCalories, BigDecimal maxPrice, 
            BigDecimal minProtein, Pageable pageable) {
        return menuItemRepository.findByFilters(location, dietType, minCalories, maxCalories, maxPrice, minProtein, pageable)
                .map(this::mapToResponse);
    }
    
    @Transactional
    public MenuItem saveMenuItem(MenuItem menuItem) {
        return menuItemRepository.save(menuItem);
    }
    
    private MenuItemResponse mapToResponse(MenuItem item) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .restaurantId(item.getRestaurant().getId())
                .restaurantName(item.getRestaurant().getName())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .imageUrl(item.getImageUrl())
                .dietType(item.getDietType())
                .calories(item.getCalories())
                .proteinGrams(item.getProteinGrams())
                .carbsGrams(item.getCarbsGrams())
                .fatGrams(item.getFatGrams())
                .carbsLevel(item.getCarbsLevel())
                .fatLevel(item.getFatLevel())
                .category(item.getCategory())
                .isAvailable(item.getIsAvailable())
                .build();
    }
}
