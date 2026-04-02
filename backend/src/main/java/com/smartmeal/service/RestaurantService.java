package com.smartmeal.service;

import com.smartmeal.dto.response.RestaurantResponse;
import com.smartmeal.model.Restaurant;
import com.smartmeal.repository.RestaurantRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RestaurantService {
    
    private final RestaurantRepository restaurantRepository;
    
    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }
    
    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findByIsActiveTrue()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public Page<RestaurantResponse> getAllRestaurantsPaginated(Pageable pageable) {
        return restaurantRepository.findByIsActiveTrue(pageable)
                .map(this::mapToResponse);
    }
    
    public List<RestaurantResponse> getRestaurantsByLocation(String location) {
        return restaurantRepository.findByLocationAndIsActiveTrue(location)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public Page<RestaurantResponse> getRestaurantsByLocationPaginated(String location, Pageable pageable) {
        return restaurantRepository.findByLocationAndIsActiveTrue(location, pageable)
                .map(this::mapToResponse);
    }
    
    public RestaurantResponse getRestaurantById(Long id) {
        return restaurantRepository.findById(id)
                .map(this::mapToResponse)
                .orElse(null);
    }
    
    public Restaurant getRestaurantEntityById(Long id) {
        return restaurantRepository.findById(id).orElse(null);
    }
    
    public List<RestaurantResponse> searchRestaurants(String keyword, String location) {
        List<Restaurant> restaurants;
        if (location != null && !location.isEmpty()) {
            restaurants = restaurantRepository.searchRestaurantsByLocation(keyword, location);
        } else {
            restaurants = restaurantRepository.searchRestaurants(keyword);
        }
        return restaurants.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public Page<RestaurantResponse> searchRestaurantsPaginated(String keyword, String location, Pageable pageable) {
        Page<Restaurant> restaurants;
        if (location != null && !location.isEmpty()) {
            restaurants = restaurantRepository.searchRestaurantsByLocation(keyword, location, pageable);
        } else {
            restaurants = restaurantRepository.searchRestaurants(keyword, pageable);
        }
        return restaurants.map(this::mapToResponse);
    }
    
    public List<String> getAllLocations() {
        return restaurantRepository.findAllLocations();
    }
    
    public List<String> getAllCuisines() {
        return restaurantRepository.findAllCuisines();
    }
    
    public Page<RestaurantResponse> getRestaurantsFiltered(String location, String search, 
            String cuisine, Double minRating, Double maxPrice, Pageable pageable) {
        return restaurantRepository.findByFilters(location, search, cuisine, minRating, maxPrice, pageable)
                .map(this::mapToResponse);
    }
    
    @Transactional
    public Restaurant saveRestaurant(Restaurant restaurant) {
        return restaurantRepository.save(restaurant);
    }
    
    private RestaurantResponse mapToResponse(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .imageUrl(restaurant.getImageUrl())
                .rating(restaurant.getRating())
                .deliveryTimeMinutes(restaurant.getDeliveryTimeMinutes())
                .deliveryFee(restaurant.getDeliveryFee())
                .minOrder(restaurant.getMinOrder())
                .location(restaurant.getLocation())
                .address(restaurant.getAddress())
                .cuisine(restaurant.getCuisine())
                .isActive(restaurant.getIsActive())
                .menuItemCount(restaurant.getMenuItems() != null ? (long) restaurant.getMenuItems().size() : 0)
                .build();
    }
}
