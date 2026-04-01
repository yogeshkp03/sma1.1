package com.smartmeal.dto.response;

import com.smartmeal.model.enums.DietType;
import com.smartmeal.model.enums.MealType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmaPreferenceResponse {
    
    private Long id;
    private MealType mealType;
    private LocalTime scheduledTime;
    private DietType dietType;
    private Integer minCalories;
    private Integer maxCalories;
    private BigDecimal minProtein;
    private BigDecimal maxProtein;
    private BigDecimal minCarbs;
    private BigDecimal maxCarbs;
    private BigDecimal minFat;
    private BigDecimal maxFat;
    private BigDecimal minFiber;
    private BigDecimal maxFiber;
    private BigDecimal maxBudget;
    private AddressResponse deliveryAddress;
    private Boolean isActive;
    private Set<DayOfWeek> daysOfWeek;
    private Boolean includeWeekends;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastTriggeredAt;
}
