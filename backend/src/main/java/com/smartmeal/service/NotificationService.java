package com.smartmeal.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void sendMealRecommendationNotification(Long userId, String userFcmToken, 
            String mealType, String itemName, String restaurantName) {
        if (userFcmToken == null || userFcmToken.isEmpty()) {
            logger.warn("FCM token not available for user {}", userId);
            return;
        }

        try {
            Message message = Message.builder()
                    .token(userFcmToken)
                    .notification(Notification.builder()
                            .setTitle("🍽️ Time for " + mealType + "!")
                            .setBody(itemName + " from " + restaurantName + " has been added to your cart")
                            .build())
                    .putData("type", "meal_recommendation")
                    .putData("userId", userId.toString())
                    .putData("mealType", mealType)
                    .putData("itemName", itemName)
                    .putData("restaurantName", restaurantName)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Successfully sent meal recommendation notification to user {}: {}", userId, response);
        } catch (Exception e) {
            logger.error("Failed to send notification to user {}: {}", userId, e.getMessage());
        }
    }

    public void sendOrderStatusNotification(Long userId, String userFcmToken, 
            String orderId, String status, String message) {
        if (userFcmToken == null || userFcmToken.isEmpty()) {
            logger.warn("FCM token not available for user {}", userId);
            return;
        }

        try {
            Message.Builder messageBuilder = Message.builder()
                    .token(userFcmToken)
                    .putData("type", "order_status")
                    .putData("orderId", orderId)
                    .putData("status", status);

            String title;
            String body;

            switch (status.toUpperCase()) {
                case "CONFIRMED":
                    title = "✅ Order Confirmed";
                    body = "Your order #" + orderId + " has been confirmed";
                    break;
                case "PREPARING":
                    title = "👨‍🍳 Preparing Your Order";
                    body = "Your order is being prepared";
                    break;
                case "OUT_FOR_DELIVERY":
                    title = "🚴 Out for Delivery";
                    body = "Your order is on its way";
                    break;
                case "DELIVERED":
                    title = "✅ Order Delivered";
                    body = "Your order has been delivered. Enjoy!";
                    break;
                case "CANCELLED":
                    title = "❌ Order Cancelled";
                    body = "Your order #" + orderId + " has been cancelled";
                    break;
                default:
                    title = "Order Update";
                    body = message != null ? message : "Your order status has been updated";
            }

            messageBuilder.notification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build());

            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            logger.info("Successfully sent order status notification to user {}: {}", userId, response);
        } catch (Exception e) {
            logger.error("Failed to send order status notification to user {}: {}", userId, e.getMessage());
        }
    }

    public void sendSmaTriggerNotification(Long userId, String userFcmToken, 
            String mealType, String scheduledTime) {
        if (userFcmToken == null || userFcmToken.isEmpty()) {
            logger.warn("FCM token not available for user {}", userId);
            return;
        }

        try {
            Message message = Message.builder()
                    .token(userFcmToken)
                    .notification(Notification.builder()
                            .setTitle("⏰ " + mealType + " Time!")
                            .setBody("Your scheduled meal will be auto-added in 1 hour at " + scheduledTime)
                            .build())
                    .putData("type", "sma_reminder")
                    .putData("userId", userId.toString())
                    .putData("mealType", mealType)
                    .putData("scheduledTime", scheduledTime)
                    .build();

            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Successfully sent SMA reminder to user {}: {}", userId, response);
        } catch (Exception e) {
            logger.error("Failed to send SMA reminder to user {}: {}", userId, e.getMessage());
        }
    }

    public void sendGenericNotification(String token, String title, String body, java.util.Map<String, String> data) {
        if (token == null || token.isEmpty()) {
            return;
        }

        try {
            Message.Builder messageBuilder = Message.builder()
                    .token(token)
                    .notification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build());

            if (data != null) {
                for (java.util.Map.Entry<String, String> entry : data.entrySet()) {
                    messageBuilder.putData(entry.getKey(), entry.getValue());
                }
            }

            FirebaseMessaging.getInstance().send(messageBuilder.build());
        } catch (Exception e) {
            logger.error("Failed to send generic notification: {}", e.getMessage());
        }
    }
}