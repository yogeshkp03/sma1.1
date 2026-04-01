package com.smartmeal.model;

import com.smartmeal.model.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;
    
    @Column(nullable = false)
    private BigDecimal totalAmount;
    
    private BigDecimal deliveryFee;
    
    private BigDecimal discountApplied;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    private String paymentMethod;
    
    private String paymentStatus;
    
    private String paymentTxnId;
    
    private Boolean isSmaTriggered;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sma_preference_id")
    private SmaPreference smaPreference;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime estimatedDelivery;
    
    private LocalDateTime actualDelivery;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> orderItems = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = OrderStatus.PENDING;
        if (isSmaTriggered == null) isSmaTriggered = false;
        if (paymentStatus == null) paymentStatus = "PENDING";
        if (discountApplied == null) discountApplied = BigDecimal.ZERO;
        if (deliveryFee == null) deliveryFee = BigDecimal.ZERO;
    }
}
