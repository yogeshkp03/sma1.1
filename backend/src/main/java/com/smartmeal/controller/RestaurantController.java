package com.smartmeal.controller;

import com.smartmeal.dto.response.ApiResponse;
import com.smartmeal.dto.response.RestaurantResponse;
import com.smartmeal.service.RestaurantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    
    private final RestaurantService restaurantService;
    
    public RestaurantController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllRestaurants(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        
        Page<RestaurantResponse> pagedResult = restaurantService.getRestaurantsFiltered(
            location, search, cuisine, minRating, maxPrice, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(pagedResult.getContent(), 
            "page", (Object) pagedResult.getNumber(),
            "size", (Object) pagedResult.getSize(),
            "totalElements", (Object) pagedResult.getTotalElements(),
            "totalPages", (Object) pagedResult.getTotalPages()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RestaurantResponse>> getRestaurantById(@PathVariable Long id) {
        RestaurantResponse restaurant = restaurantService.getRestaurantById(id);
        if (restaurant == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Restaurant not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(restaurant));
    }
    
    @GetMapping("/locations")
    public ResponseEntity<ApiResponse<List<String>>> getAllLocations() {
        List<String> locations = restaurantService.getAllLocations();
        return ResponseEntity.ok(ApiResponse.success(locations));
    }
    
    @GetMapping("/cuisines")
    public ResponseEntity<ApiResponse<List<String>>> getAllCuisines() {
        List<String> cuisines = restaurantService.getAllCuisines();
        return ResponseEntity.ok(ApiResponse.success(cuisines));
    }
}
