package com.smartmeal.dto.response;

import com.smartmeal.model.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    private Long id;
    private RestaurantResponse restaurant;
    private AddressResponse deliveryAddress;
    private BigDecimal totalAmount;
    private BigDecimal deliveryFee;
    private BigDecimal discountApplied;
    private OrderStatus status;
    private String paymentMethod;
    private String paymentStatus;
    private Boolean isSmaTriggered;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedDelivery;
    private LocalDateTime actualDelivery;
    private List<OrderItemResponse> orderItems;
}
