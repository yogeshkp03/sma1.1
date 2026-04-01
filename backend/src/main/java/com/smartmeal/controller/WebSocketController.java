package com.smartmeal.controller;

import com.smartmeal.model.enums.OrderStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class WebSocketController {

    @MessageMapping("/order-track")
    @SendTo("/topic/order-status")
    public Map<String, Object> trackOrder(Map<String, Object> orderUpdate) {
        return orderUpdate;
    }

    @MessageMapping("/subscribe-order")
    @SendTo("/topic/order-status")
    public Map<String, Object> subscribeToOrder(Map<String, Object> request) {
        return Map.of(
            "type", "subscribed",
            "orderId", request.get("orderId"),
            "message", "Subscribed to order updates"
        );
    }
}