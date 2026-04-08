package com.smartmeal.scheduler;

import com.smartmeal.model.*;
import com.smartmeal.model.enums.MealType;
import com.smartmeal.repository.*;
import com.smartmeal.service.LocalRecommendationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.*;

@Component
public class SmaScheduler {

    private final UserRepository userRepository;
    private final SmaPreferenceRepository preferenceRepository;
    private final CartItemRepository cartItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final LocalRecommendationService recommendationService;

    private static final int BUFFER_MINUTES = 60;

    public SmaScheduler(UserRepository userRepository,
                       SmaPreferenceRepository preferenceRepository,
                       CartItemRepository cartItemRepository,
                       MenuItemRepository menuItemRepository,
                       LocalRecommendationService recommendationService) {
        this.userRepository = userRepository;
        this.preferenceRepository = preferenceRepository;
        this.cartItemRepository = cartItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.recommendationService = recommendationService;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkScheduledMeals() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        DayOfWeek currentDay = now.getDayOfWeek();
        boolean isWeekend = currentDay == DayOfWeek.SATURDAY || currentDay == DayOfWeek.SUNDAY;

        List<User> activeUsers = userRepository.findAll();

        for (User user : activeUsers) {
            List<SmaPreference> userPrefs = preferenceRepository.findByUserId(user.getId());

            for (SmaPreference pref : userPrefs) {
                if (!Boolean.TRUE.equals(pref.getIsActive())) {
                    continue;
                }

                if (isWeekend && !Boolean.TRUE.equals(pref.getIncludeWeekends())) {
                    continue;
                }

                LocalTime scheduledTime = pref.getScheduledTime();
                LocalTime triggerTime = scheduledTime.minusMinutes(BUFFER_MINUTES);

                if (isTimeWithinMinute(currentTime, triggerTime)) {
                    processMealRecommendation(user, pref);
                }
            }
        }
    }

    private boolean isTimeWithinMinute(LocalTime current, LocalTime target) {
        int currentMinute = current.getHour() * 60 + current.getMinute();
        int targetMinute = target.getHour() * 60 + target.getMinute();
        return Math.abs(currentMinute - targetMinute) <= 1;
    }

    private void processMealRecommendation(User user, SmaPreference pref) {
        try {
            if (wasRecentlyTriggered(pref)) {
                return;
            }

            MealType mealType = pref.getMealType();
            MenuItem recommendation = recommendationService.getRecommendation(user.getId(), mealType);
            
            if (recommendation != null) {
                addToCart(user, recommendation);
                
                pref.setLastTriggeredAt(LocalDateTime.now());
                preferenceRepository.save(pref);

                System.out.println("SMA: Added " + recommendation.getName() + " to cart for user " + user.getId());
            }
        } catch (Exception e) {
            System.err.println("SMA Error for user " + user.getId() + ": " + e.getMessage());
        }
    }

    private boolean wasRecentlyTriggered(SmaPreference pref) {
        if (pref.getLastTriggeredAt() == null) {
            return false;
        }
        LocalDateTime lastTriggered = pref.getLastTriggeredAt();
        return lastTriggered.plusHours(2).isAfter(LocalDateTime.now());
    }

    private void addToCart(User user, MenuItem menuItem) {
        Optional<CartItem> existingItem = cartItemRepository.findByUserIdAndMenuItemId(user.getId(), menuItem.getId());
        
        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
        } else {
            CartItem cartItem = CartItem.builder()
                .user(user)
                .menuItem(menuItem)
                .quantity(1)
                .specialInstructions("SMA auto-added")
                .build();
            cartItemRepository.save(cartItem);
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void dailyCleanup() {
        System.out.println("SMA: Running daily cleanup");
    }
}
