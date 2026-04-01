package com.smartmeal.controller;

import com.smartmeal.dto.response.ApiResponse;
import com.smartmeal.dto.response.MenuItemResponse;
import com.smartmeal.model.enums.DietType;
import com.smartmeal.service.MenuItemService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/menu")
public class MenuItemController {
    
    private final MenuItemService menuItemService;
    
    public MenuItemController(MenuItemService menuItemService) {
        this.menuItemService = menuItemService;
    }
    
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<?>> getMenuByRestaurant(
            @PathVariable Long restaurantId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String category) {
        
        if (page >= 0 && size > 0) {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<MenuItemResponse> pagedResult = menuItemService.getMenuByRestaurantPaginated(restaurantId, category, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(pagedResult.getContent(), 
                "page", pagedResult.getNumber(),
                "size", pagedResult.getSize(),
                "totalElements", pagedResult.getTotalElements(),
                "totalPages", pagedResult.getTotalPages()));
        }
        
        List<MenuItemResponse> menuItems = menuItemService.getMenuByRestaurant(restaurantId, category);
        return ResponseEntity.ok(ApiResponse.success(menuItems));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MenuItemResponse>> getMenuItemById(@PathVariable Long id) {
        MenuItemResponse menuItem = menuItemService.getMenuItemById(id);
        if (menuItem == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Menu item not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(menuItem));
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> searchMenuItems(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) DietType dietType,
            @RequestParam(required = false) Integer minCalories,
            @RequestParam(required = false) Integer maxCalories,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) BigDecimal minProtein,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        if (page >= 0 && size > 0) {
            Pageable pageable = PageRequest.of(page, size);
            Page<MenuItemResponse> pagedResult = menuItemService.findByFiltersPaginated(
                location, dietType, minCalories, maxCalories, maxPrice, minProtein, pageable);
            
            return ResponseEntity.ok(ApiResponse.success(pagedResult.getContent(), 
                "page", pagedResult.getNumber(),
                "size", pagedResult.getSize(),
                "totalElements", pagedResult.getTotalElements(),
                "totalPages", pagedResult.getTotalPages()));
        }
        
        List<MenuItemResponse> menuItems;
        
        if (location != null && !location.isEmpty()) {
            menuItems = menuItemService.getMenuByLocation(location);
        } else {
            menuItems = menuItemService.findByFilters(dietType, minCalories, maxCalories, maxPrice, minProtein);
        }
        
        return ResponseEntity.ok(ApiResponse.success(menuItems));
    }
}
