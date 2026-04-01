package com.smartmeal.model;

import com.smartmeal.model.enums.DietType;
import com.smartmeal.model.enums.MacroLevel;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private BigDecimal price;
    
    private String imageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DietType dietType;
    
    private Integer calories;
    
    private BigDecimal proteinGrams;
    
    private BigDecimal carbsGrams;
    
    private BigDecimal fatGrams;
    
    @Enumerated(EnumType.STRING)
    private MacroLevel carbsLevel;
    
    @Enumerated(EnumType.STRING)
    private MacroLevel fatLevel;
    
    private String category;
    
    private Boolean isAvailable;
    
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isAvailable == null) isAvailable = true;
    }
}
