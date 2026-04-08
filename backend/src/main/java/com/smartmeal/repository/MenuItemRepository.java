package com.smartmeal.repository;

import com.smartmeal.model.MenuItem;
import com.smartmeal.model.Restaurant;
import com.smartmeal.model.enums.DietType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    
    List<MenuItem> findByRestaurant(Restaurant restaurant);
    
    List<MenuItem> findByRestaurantAndIsAvailableTrue(Restaurant restaurant);
    
    List<MenuItem> findByDietType(DietType dietType);
    
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.isAvailable = true")
    List<MenuItem> findAvailableByRestaurantId(@Param("restaurantId") Long restaurantId);
    
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.isAvailable = true")
    Page<MenuItem> findAvailableByRestaurantId(@Param("restaurantId") Long restaurantId, Pageable pageable);
    
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.category = :category AND m.isAvailable = true")
    List<MenuItem> findByRestaurantIdAndCategory(@Param("restaurantId") Long restaurantId, @Param("category") String category);
    
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id = :restaurantId AND m.category = :category AND m.isAvailable = true")
    Page<MenuItem> findByRestaurantIdAndCategory(@Param("restaurantId") Long restaurantId, @Param("category") String category, Pageable pageable);
    
    @Query("SELECT m FROM MenuItem m WHERE m.isAvailable = true AND " +
           "(:dietType IS NULL OR m.dietType = :dietType) AND " +
           "(:minCalories IS NULL OR m.calories >= :minCalories) AND " +
           "(:maxCalories IS NULL OR m.calories <= :maxCalories) AND " +
           "(:maxCarbs IS NULL OR m.carbsGrams <= :maxCarbs) AND " +
           "(:maxFat IS NULL OR m.fatGrams <= :maxFat) AND " +
           "(:maxPrice IS NULL OR m.price <= :maxPrice) AND " +
           "(:minProtein IS NULL OR m.proteinGrams >= :minProtein)")
    List<MenuItem> findByFilters(@Param("dietType") DietType dietType,
                                   @Param("minCalories") Integer minCalories,
                                   @Param("maxCalories") Integer maxCalories,
                                   @Param("maxCarbs") BigDecimal maxCarbs,
                                   @Param("maxFat") BigDecimal maxFat,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("minProtein") BigDecimal minProtein);
    
    @Query("SELECT m FROM MenuItem m WHERE m.isAvailable = true AND " +
           "(:location IS NULL OR m.restaurant.location = :location) AND " +
           "(:dietType IS NULL OR m.dietType = :dietType) AND " +
           "(:minCalories IS NULL OR m.calories >= :minCalories) AND " +
           "(:maxCalories IS NULL OR m.calories <= :maxCalories) AND " +
           "(:maxCarbs IS NULL OR m.carbsGrams <= :maxCarbs) AND " +
           "(:maxFat IS NULL OR m.fatGrams <= :maxFat) AND " +
           "(:maxPrice IS NULL OR m.price <= :maxPrice) AND " +
           "(:minProtein IS NULL OR m.proteinGrams >= :minProtein)")
    Page<MenuItem> findByFilters(@Param("location") String location,
                                   @Param("dietType") DietType dietType,
                                   @Param("minCalories") Integer minCalories,
                                   @Param("maxCalories") Integer maxCalories,
                                   @Param("maxCarbs") BigDecimal maxCarbs,
                                   @Param("maxFat") BigDecimal maxFat,
                                   @Param("maxPrice") BigDecimal maxPrice,
                                   @Param("minProtein") BigDecimal minProtein,
                                   Pageable pageable);
    
    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.location = :location AND m.isAvailable = true")
    List<MenuItem> findByLocation(@Param("location") String location);
}
