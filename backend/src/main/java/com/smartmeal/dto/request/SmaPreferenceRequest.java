package com.smartmeal.dto.request;

import com.smartmeal.model.enums.DietType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Set;

@Data
public class SmaPreferenceRequest {
    
    @NotNull(message = "Meal type is required")
    private String mealType;
    
    @NotNull(message = "Scheduled time is required")
    private LocalTime scheduledTime;
    
    private DietType dietType;
    
    @Min(value = 0, message = "Min calories cannot be negative")
    private Integer minCalories;
    
    @Max(value = 2000, message = "Max calories cannot exceed 2000")
    private Integer maxCalories;
    
    @DecimalMin(value = "0", message = "Min protein cannot be negative")
    private BigDecimal minProtein;
    
    @DecimalMax(value = "500", message = "Max protein cannot exceed 500g")
    private BigDecimal maxProtein;
    
    @DecimalMin(value = "0", message = "Min carbs cannot be negative")
    private BigDecimal minCarbs;
    
    @DecimalMax(value = "1000", message = "Max carbs cannot exceed 1000g")
    private BigDecimal maxCarbs;
    
    @DecimalMin(value = "0", message = "Min fat cannot be negative")
    private BigDecimal minFat;
    
    @DecimalMax(value = "500", message = "Max fat cannot exceed 500g")
    private BigDecimal maxFat;
    
    @DecimalMin(value = "0", message = "Min fiber cannot be negative")
    private BigDecimal minFiber;
    
    @DecimalMax(value = "500", message = "Max fiber cannot exceed 500g")
    private BigDecimal maxFiber;
    
    @DecimalMin(value = "0", message = "Max budget cannot be negative")
    private BigDecimal maxBudget;
    
    private Long deliveryAddressId;
    
    private Set<DayOfWeek> daysOfWeek;
    
    private Boolean includeWeekends = true;
}
