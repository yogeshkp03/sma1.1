package com.smartmeal.model;

import com.smartmeal.model.enums.DietType;
import com.smartmeal.model.enums.MealType;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;

@Entity
@Table(name = "sma_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmaPreference {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType;
    
    @Column(nullable = false)
    private LocalTime scheduledTime;
    
    @Enumerated(EnumType.STRING)
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_address_id")
    private Address deliveryAddress;
    
    private Boolean isActive;
    
    @ElementCollection
    @CollectionTable(name = "sma_days", joinColumns = @JoinColumn(name = "sma_preference_id"))
    @Column(name = "day_of_week")
    @Enumerated(EnumType.STRING)
    private Set<DayOfWeek> daysOfWeek;
    
    private Boolean includeWeekends;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime expiresAt;
    
    private LocalDateTime lastTriggeredAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
        if (includeWeekends == null) includeWeekends = true;
        if (expiresAt == null) expiresAt = createdAt.plusDays(7);
    }
}
