package com.smartmeal.controller;

import com.smartmeal.dto.response.ApiResponse;
import com.smartmeal.dto.response.OrderResponse;
import com.smartmeal.model.enums.OrderStatus;
import com.smartmeal.repository.OrderRepository;
import com.smartmeal.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<?>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page >= 0 && size > 0) {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<OrderResponse> pagedOrders = orderService.getUserOrdersPaginated(userId, pageable);

            return ResponseEntity.ok(ApiResponse.success(pagedOrders.getContent(),
                "page", pagedOrders.getNumber(),
                "size", pagedOrders.getSize(),
                "totalElements", pagedOrders.getTotalElements(),
                "totalPages", pagedOrders.getTotalPages()));
        }

        List<OrderResponse> orders = orderService.getUserOrders(userId);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderDetail(
            @PathVariable Long orderId,
            @RequestHeader("X-User-Id") Long userId) {

        OrderResponse order = orderService.getOrderById(orderId, userId);
        if (order == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Order not found"));
        }

        return ResponseEntity.ok(ApiResponse.success(order));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponse<OrderResponse>> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam OrderStatus status) {

        try {
            OrderResponse updatedOrder = orderService.updateOrderStatus(orderId, userId, status);
            return ResponseEntity.ok(ApiResponse.success("Order status updated", updatedOrder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader("X-User-Id") Long userId) {

        try {
            OrderResponse cancelledOrder = orderService.cancelOrder(orderId, userId);
            return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", cancelledOrder));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}/sma")
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getSmaOrders(@PathVariable Long userId) {
        List<OrderResponse> orders = orderService.getSmaTriggeredOrders(userId);
        return ResponseEntity.ok(ApiResponse.success(orders));
    }
}