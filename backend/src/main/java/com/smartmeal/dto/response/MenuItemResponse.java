package com.smartmeal.dto.response;

import com.smartmeal.model.enums.DietType;
import com.smartmeal.model.enums.MacroLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemResponse {
    
    private Long id;
    private Long restaurantId;
    private String restaurantName;
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private DietType dietType;
    private Integer calories;
    private BigDecimal proteinGrams;
    private BigDecimal carbsGrams;
    private BigDecimal fatGrams;
    private MacroLevel carbsLevel;
    private MacroLevel fatLevel;
    private String category;
    private Boolean isAvailable;
}
