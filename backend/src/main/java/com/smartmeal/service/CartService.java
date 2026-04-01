package com.smartmeal.service;

import com.smartmeal.dto.request.CartItemRequest;
import com.smartmeal.dto.response.CartItemResponse;
import com.smartmeal.dto.response.CartResponse;
import com.smartmeal.dto.response.MenuItemResponse;
import com.smartmeal.model.CartItem;
import com.smartmeal.model.MenuItem;
import com.smartmeal.model.User;
import com.smartmeal.repository.CartItemRepository;
import com.smartmeal.repository.MenuItemRepository;
import com.smartmeal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {
    
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;
    
    public CartService(CartItemRepository cartItemRepository,
                       MenuItemRepository menuItemRepository,
                       UserRepository userRepository) {
        this.cartItemRepository = cartItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.userRepository = userRepository;
    }
    
    public CartResponse getCart(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);
        
        List<CartItemResponse> itemResponses = items.stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());
        
        BigDecimal subtotal = itemResponses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal deliveryFee = items.isEmpty() ? BigDecimal.ZERO : new BigDecimal("30.00");
        BigDecimal total = subtotal.add(deliveryFee);
        
        return CartResponse.builder()
                .userId(userId)
                .items(itemResponses)
                .subtotal(subtotal)
                .deliveryFee(deliveryFee)
                .total(total)
                .itemCount(items.size())
                .build();
    }
    
    @Transactional
    public CartItemResponse addToCart(Long userId, CartItemRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        MenuItem menuItem = menuItemRepository.findById(request.getMenuItemId())
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
        
        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndMenuItemId(userId, request.getMenuItemId());
        
        CartItem cartItem;
        if (existingItem.isPresent()) {
            cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
        } else {
            cartItem = CartItem.builder()
                    .user(user)
                    .menuItem(menuItem)
                    .quantity(request.getQuantity())
                    .specialInstructions(request.getSpecialInstructions())
                    .build();
        }
        
        cartItem = cartItemRepository.save(cartItem);
        return mapToCartItemResponse(cartItem);
    }
    
    @Transactional
    public CartItemResponse updateCartItem(Long userId, Long itemId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("Cart item does not belong to user");
        }
        
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
            return null;
        }
        
        cartItem.setQuantity(quantity);
        cartItem = cartItemRepository.save(cartItem);
        return mapToCartItemResponse(cartItem);
    }
    
    @Transactional
    public void removeFromCart(Long userId, Long itemId) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));
        
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new RuntimeException("Cart item does not belong to user");
        }
        
        cartItemRepository.delete(cartItem);
    }
    
    @Transactional
    public void clearCart(Long userId) {
        cartItemRepository.deleteAllByUserId(userId);
    }
    
    public boolean isInCart(Long userId, Long menuItemId) {
        return cartItemRepository.findByUserIdAndMenuItemId(userId, menuItemId).isPresent();
    }
    
    private CartItemResponse mapToCartItemResponse(CartItem item) {
        MenuItemResponse menuItemResponse = MenuItemResponse.builder()
                .id(item.getMenuItem().getId())
                .restaurantId(item.getMenuItem().getRestaurant().getId())
                .restaurantName(item.getMenuItem().getRestaurant().getName())
                .name(item.getMenuItem().getName())
                .description(item.getMenuItem().getDescription())
                .price(item.getMenuItem().getPrice())
                .imageUrl(item.getMenuItem().getImageUrl())
                .dietType(item.getMenuItem().getDietType())
                .calories(item.getMenuItem().getCalories())
                .proteinGrams(item.getMenuItem().getProteinGrams())
                .carbsGrams(item.getMenuItem().getCarbsGrams())
                .fatGrams(item.getMenuItem().getFatGrams())
                .category(item.getMenuItem().getCategory())
                .isAvailable(item.getMenuItem().getIsAvailable())
                .build();
        
        BigDecimal subtotal = item.getMenuItem().getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));
        
        return CartItemResponse.builder()
                .id(item.getId())
                .menuItem(menuItemResponse)
                .quantity(item.getQuantity())
                .specialInstructions(item.getSpecialInstructions())
                .subtotal(subtotal)
                .addedAt(item.getAddedAt())
                .build();
    }
}
