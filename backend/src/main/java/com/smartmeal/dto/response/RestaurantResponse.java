package com.smartmeal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {
    
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal rating;
    private Integer deliveryTimeMinutes;
    private BigDecimal deliveryFee;
    private BigDecimal minOrder;
    private String location;
    private String address;
    private String cuisine;
    private Boolean isActive;
    private Long menuItemCount;
    private List<String> categories;
}
