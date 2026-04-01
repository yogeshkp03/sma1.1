package com.smartmeal.service;

import com.smartmeal.dto.request.CheckoutRequest;
import com.smartmeal.dto.response.*;
import com.smartmeal.model.*;
import com.smartmeal.model.enums.OrderStatus;
import com.smartmeal.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final AddressService addressService;
    
    public OrderService(OrderRepository orderRepository,
                       CartItemRepository cartItemRepository,
                       UserRepository userRepository,
                       RestaurantRepository restaurantRepository,
                       AddressService addressService) {
        this.orderRepository = orderRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.addressService = addressService;
    }
    
    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public Page<OrderResponse> getUserOrdersPaginated(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::mapToResponse);
    }
    
    public OrderResponse getOrderById(Long orderId, Long userId) {
        return orderRepository.findById(orderId)
                .filter(order -> order.getUser().getId().equals(userId))
                .map(this::mapToResponse)
                .orElse(null);
    }
    
    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, Long userId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        order.setStatus(newStatus);
        
        if (newStatus == OrderStatus.DELIVERED) {
            order.setActualDelivery(LocalDateTime.now());
        }
        
        order = orderRepository.save(order);
        return mapToResponse(order);
    }
    
    @Transactional
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getUser().getId().equals(userId))
                .orElseThrow(() -> new RuntimeException("Order not found"));
        
        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot cancel order in current status: " + order.getStatus());
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);
        
        return mapToResponse(order);
    }
    
    public List<OrderResponse> getSmaTriggeredOrders(Long userId) {
        return orderRepository.findByUserIdAndIsSmaTriggeredTrue(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public OrderResponse createOrder(Long userId, CheckoutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Address address = addressService.getAddressEntityById(request.getAddressId());
        if (address == null) {
            throw new RuntimeException("Address not found");
        }
        
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }
        
        Long restaurantId = request.getRestaurantId();
        Restaurant restaurant;
        if (restaurantId != null) {
            restaurant = restaurantRepository.findById(restaurantId)
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        } else {
            Long firstRestaurantId = cartItems.get(0).getMenuItem().getRestaurant().getId();
            restaurant = restaurantRepository.findById(firstRestaurantId)
                    .orElseThrow(() -> new RuntimeException("Restaurant not found"));
        }
        
        BigDecimal subtotal = cartItems.stream()
                .map(item -> item.getMenuItem().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal deliveryFee = restaurant.getDeliveryFee() != null ? 
                restaurant.getDeliveryFee() : new BigDecimal("30.00");
        
        BigDecimal total = subtotal.add(deliveryFee);
        
        Order order = Order.builder()
                .user(user)
                .restaurant(restaurant)
                .address(address)
                .totalAmount(total)
                .deliveryFee(deliveryFee)
                .discountApplied(BigDecimal.ZERO)
                .status(OrderStatus.PENDING)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus("PENDING")
                .isSmaTriggered(false)
                .estimatedDelivery(LocalDateTime.now().plusMinutes(restaurant.getDeliveryTimeMinutes()))
                .orderItems(new ArrayList<>())
                .build();
        
        order = orderRepository.save(order);
        
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuItem(cartItem.getMenuItem())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(cartItem.getMenuItem().getPrice())
                    .subtotal(cartItem.getMenuItem().getPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                    .build();
            order.getOrderItems().add(orderItem);
        }
        
        order = orderRepository.save(order);
        
        cartItemRepository.deleteAllByUserId(userId);
        
        return mapToResponse(order);
    }
    
    private OrderResponse mapToResponse(Order order) {
        RestaurantResponse restaurantResponse = RestaurantResponse.builder()
                .id(order.getRestaurant().getId())
                .name(order.getRestaurant().getName())
                .imageUrl(order.getRestaurant().getImageUrl())
                .location(order.getRestaurant().getLocation())
                .build();
        
        AddressResponse addressResponse = AddressResponse.builder()
                .id(order.getAddress().getId())
                .addressLine(order.getAddress().getAddressLine())
                .landmark(order.getAddress().getLandmark())
                .location(order.getAddress().getLocation())
                .build();
        
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::mapToOrderItemResponse)
                .collect(Collectors.toList());
        
        return OrderResponse.builder()
                .id(order.getId())
                .restaurant(restaurantResponse)
                .deliveryAddress(addressResponse)
                .totalAmount(order.getTotalAmount())
                .deliveryFee(order.getDeliveryFee())
                .discountApplied(order.getDiscountApplied())
                .status(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .isSmaTriggered(order.getIsSmaTriggered())
                .createdAt(order.getCreatedAt())
                .estimatedDelivery(order.getEstimatedDelivery())
                .actualDelivery(order.getActualDelivery())
                .orderItems(itemResponses)
                .build();
    }
    
    private OrderItemResponse mapToOrderItemResponse(OrderItem item) {
        MenuItemResponse menuItemResponse = MenuItemResponse.builder()
                .id(item.getMenuItem().getId())
                .name(item.getMenuItem().getName())
                .price(item.getMenuItem().getPrice())
                .imageUrl(item.getMenuItem().getImageUrl())
                .dietType(item.getMenuItem().getDietType())
                .build();
        
        return OrderItemResponse.builder()
                .id(item.getId())
                .menuItem(menuItemResponse)
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .subtotal(item.getSubtotal())
                .build();
    }
}
