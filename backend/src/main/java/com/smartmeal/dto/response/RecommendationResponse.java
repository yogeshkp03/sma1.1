package com.smartmeal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    
    private MenuItemResponse menuItem;
    private BigDecimal matchScore;
    private String recommendationReason;
    private Boolean alreadyInCart;
}
