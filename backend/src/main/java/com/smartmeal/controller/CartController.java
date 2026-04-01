package com.smartmeal.controller;

import com.smartmeal.dto.request.CartItemRequest;
import com.smartmeal.dto.request.CheckoutRequest;
import com.smartmeal.dto.response.ApiResponse;
import com.smartmeal.dto.response.CartItemResponse;
import com.smartmeal.dto.response.CartResponse;
import com.smartmeal.dto.response.OrderResponse;
import com.smartmeal.service.CartService;
import com.smartmeal.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    
    private final CartService cartService;
    private final OrderService orderService;
    
    public CartController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@PathVariable Long userId) {
        CartResponse cart = cartService.getCart(userId);
        return ResponseEntity.ok(ApiResponse.success(cart));
    }
    
    @PostMapping("/{userId}/add")
    public ResponseEntity<ApiResponse<CartItemResponse>> addToCart(
            @PathVariable Long userId,
            @Valid @RequestBody CartItemRequest request) {
        CartItemResponse item = cartService.addToCart(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart", item));
    }
    
    @PutMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ApiResponse<CartItemResponse>> updateCartItem(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @RequestParam Integer quantity) {
        CartItemResponse item = cartService.updateCartItem(userId, itemId, quantity);
        if (item == null) {
            return ResponseEntity.ok(ApiResponse.success("Item removed from cart", null));
        }
        return ResponseEntity.ok(ApiResponse.success(item));
    }
    
    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<ApiResponse<Void>> removeFromCart(
            @PathVariable Long userId,
            @PathVariable Long itemId) {
        cartService.removeFromCart(userId, itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", null));
    }
    
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
    }
    
    @PostMapping("/{userId}/checkout")
    public ResponseEntity<ApiResponse<OrderResponse>> checkout(
            @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest request) {
        try {
            OrderResponse order = orderService.createOrder(userId, request);
            return ResponseEntity.ok(ApiResponse.success("Order placed successfully", order));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
