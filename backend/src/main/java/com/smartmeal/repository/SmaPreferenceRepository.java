package com.smartmeal.repository;

import com.smartmeal.model.SmaPreference;
import com.smartmeal.model.User;
import com.smartmeal.model.enums.MealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SmaPreferenceRepository extends JpaRepository<SmaPreference, Long> {
    
    List<SmaPreference> findByUser(User user);
    
    List<SmaPreference> findByUserId(Long userId);
    
    List<SmaPreference> findByUserIdAndIsActiveTrue(Long userId);
    
    Optional<SmaPreference> findByIdAndUserId(Long id, Long userId);
    
    Optional<SmaPreference> findByUserIdAndMealType(Long userId, MealType mealType);
    
    @Query("SELECT s FROM SmaPreference s WHERE s.isActive = true AND " +
           "s.expiresAt > :currentTime AND " +
           "s.scheduledTime BETWEEN :startTime AND :endTime")
    List<SmaPreference> findActivePreferencesForScheduledTime(
            @Param("currentTime") LocalDateTime currentTime,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
    
    @Query("SELECT s FROM SmaPreference s WHERE s.isActive = true AND " +
           "s.expiresAt < :currentTime")
    List<SmaPreference> findExpiredPreferences(@Param("currentTime") LocalDateTime currentTime);
}
