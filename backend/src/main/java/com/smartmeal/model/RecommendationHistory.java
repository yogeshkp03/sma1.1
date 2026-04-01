package com.smartmeal.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommendation_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecommendationHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    private Long menuItemId;
    
    @Column(nullable = false)
    private String menuItemName;
    
    private Long restaurantId;
    
    @Column(nullable = false)
    private String restaurantName;
    
    @Enumerated(EnumType.STRING)
    private com.smartmeal.model.enums.MealType mealType;
    
    private Integer calories;
    
    private LocalDateTime recommendedAt;
    
    private Boolean addedToCart;
    
    private LocalDateTime cartAddedAt;
}
