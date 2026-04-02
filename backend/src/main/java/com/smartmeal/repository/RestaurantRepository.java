package com.smartmeal.repository;

import com.smartmeal.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    
    List<Restaurant> findByLocation(String location);
    
    List<Restaurant> findByIsActiveTrue();
    
    Page<Restaurant> findByIsActiveTrue(Pageable pageable);
    
    List<Restaurant> findByLocationAndIsActiveTrue(String location);
    
    Page<Restaurant> findByLocationAndIsActiveTrue(String location, Pageable pageable);
    
    @Query("SELECT DISTINCT r.location FROM Restaurant r WHERE r.isActive = true ORDER BY r.location")
    List<String> findAllLocations();
    
    @Query("SELECT DISTINCT r.cuisine FROM Restaurant r WHERE r.isActive = true ORDER BY r.cuisine")
    List<String> findAllCuisines();
    
    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true AND " +
           "(:location IS NULL OR r.location = :location) AND " +
           "(:search IS NULL OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:cuisine IS NULL OR r.cuisine = :cuisine) AND " +
           "(:minRating IS NULL OR r.rating >= :minRating) AND " +
           "(:maxPrice IS NULL OR r.minOrder <= :maxPrice)")
    Page<Restaurant> findByFilters(@Param("location") String location,
                                    @Param("search") String search,
                                    @Param("cuisine") String cuisine,
                                    @Param("minRating") Double minRating,
                                    @Param("maxPrice") Double maxPrice,
                                    Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Restaurant> searchRestaurants(@Param("keyword") String keyword);
    
    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Restaurant> searchRestaurants(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true AND " +
           "(:location IS NULL OR r.location = :location) AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Restaurant> searchRestaurantsByLocation(@Param("keyword") String keyword, 
                                                  @Param("location") String location);
    
    @Query("SELECT r FROM Restaurant r WHERE r.isActive = true AND " +
           "(:location IS NULL OR r.location = :location) AND " +
           "(LOWER(r.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(r.cuisine) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Restaurant> searchRestaurantsByLocation(@Param("keyword") String keyword, 
                                                  @Param("location") String location,
                                                  Pageable pageable);
}
