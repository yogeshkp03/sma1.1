package com.smartmeal.service;

import com.smartmeal.dto.request.SmaPreferenceRequest;
import com.smartmeal.dto.response.AddressResponse;
import com.smartmeal.dto.response.SmaPreferenceResponse;
import com.smartmeal.model.Address;
import com.smartmeal.model.SmaPreference;
import com.smartmeal.model.User;
import com.smartmeal.model.enums.MealType;
import com.smartmeal.repository.SmaPreferenceRepository;
import com.smartmeal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SmaPreferenceService {
    
    private final SmaPreferenceRepository smaPreferenceRepository;
    private final UserRepository userRepository;
    private final AddressService addressService;
    
    public SmaPreferenceService(SmaPreferenceRepository smaPreferenceRepository,
                                UserRepository userRepository,
                                AddressService addressService) {
        this.smaPreferenceRepository = smaPreferenceRepository;
        this.userRepository = userRepository;
        this.addressService = addressService;
    }
    
    public List<SmaPreferenceResponse> getPreferencesByUserId(Long userId) {
        return smaPreferenceRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public List<SmaPreferenceResponse> getActivePreferencesByUserId(Long userId) {
        return smaPreferenceRepository.findByUserIdAndIsActiveTrue(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public SmaPreferenceResponse getPreferenceById(Long id, Long userId) {
        return smaPreferenceRepository.findByIdAndUserId(id, userId)
                .map(this::mapToResponse)
                .orElse(null);
    }
    
    @Transactional
    public SmaPreferenceResponse createPreference(Long userId, SmaPreferenceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Address address = null;
        if (request.getDeliveryAddressId() != null) {
            address = addressService.getAddressEntityById(request.getDeliveryAddressId());
        }
        
        SmaPreference preference = SmaPreference.builder()
                .user(user)
                .mealType(MealType.valueOf(request.getMealType()))
                .scheduledTime(request.getScheduledTime())
                .dietType(request.getDietType())
                .minCalories(request.getMinCalories())
                .maxCalories(request.getMaxCalories())
                .minProtein(request.getMinProtein())
                .maxProtein(request.getMaxProtein())
                .minCarbs(request.getMinCarbs())
                .maxCarbs(request.getMaxCarbs())
                .minFat(request.getMinFat())
                .maxFat(request.getMaxFat())
                .minFiber(request.getMinFiber())
                .maxFiber(request.getMaxFiber())
                .maxBudget(request.getMaxBudget())
                .deliveryAddress(address)
                .isActive(true)
                .daysOfWeek(request.getDaysOfWeek())
                .includeWeekends(request.getIncludeWeekends())
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        
        preference = smaPreferenceRepository.save(preference);
        return mapToResponse(preference);
    }
    
    @Transactional
    public SmaPreferenceResponse updatePreference(Long userId, Long preferenceId, SmaPreferenceRequest request) {
        SmaPreference preference = smaPreferenceRepository.findByIdAndUserId(preferenceId, userId)
                .orElseThrow(() -> new RuntimeException("Preference not found"));
        
        if (request.getMealType() != null) {
            preference.setMealType(MealType.valueOf(request.getMealType()));
        }
        if (request.getScheduledTime() != null) {
            preference.setScheduledTime(request.getScheduledTime());
        }
        if (request.getDietType() != null) {
            preference.setDietType(request.getDietType());
        }
        if (request.getMinCalories() != null) {
            preference.setMinCalories(request.getMinCalories());
        }
        if (request.getMaxCalories() != null) {
            preference.setMaxCalories(request.getMaxCalories());
        }
        if (request.getMinProtein() != null) {
            preference.setMinProtein(request.getMinProtein());
        }
        if (request.getMaxProtein() != null) {
            preference.setMaxProtein(request.getMaxProtein());
        }
        if (request.getMinFiber() != null) {
            preference.setMinFiber(request.getMinFiber());
        }
        if (request.getMaxFiber() != null) {
            preference.setMaxFiber(request.getMaxFiber());
        }
        if (request.getMaxBudget() != null) {
            preference.setMaxBudget(request.getMaxBudget());
        }
        if (request.getDeliveryAddressId() != null) {
            preference.setDeliveryAddress(addressService.getAddressEntityById(request.getDeliveryAddressId()));
        }
        if (request.getDaysOfWeek() != null) {
            preference.setDaysOfWeek(request.getDaysOfWeek());
        }
        if (request.getIncludeWeekends() != null) {
            preference.setIncludeWeekends(request.getIncludeWeekends());
        }
        
        preference.setExpiresAt(LocalDateTime.now().plusDays(7));
        
        preference = smaPreferenceRepository.save(preference);
        return mapToResponse(preference);
    }
    
    @Transactional
    public void togglePreference(Long userId, Long preferenceId, boolean active) {
        SmaPreference preference = smaPreferenceRepository.findByIdAndUserId(preferenceId, userId)
                .orElseThrow(() -> new RuntimeException("Preference not found"));
        
        preference.setIsActive(active);
        smaPreferenceRepository.save(preference);
    }
    
    @Transactional
    public void deletePreference(Long userId, Long preferenceId) {
        SmaPreference preference = smaPreferenceRepository.findByIdAndUserId(preferenceId, userId)
                .orElseThrow(() -> new RuntimeException("Preference not found"));
        
        smaPreferenceRepository.delete(preference);
    }
    
    public SmaPreference getPreferenceEntityById(Long id) {
        return smaPreferenceRepository.findById(id).orElse(null);
    }
    
    @Transactional
    public void skipScheduledMeal(Long userId, MealType mealType, String reason) {
        List<SmaPreference> preferences = smaPreferenceRepository.findByUserIdAndMealType(userId, mealType);
        SmaPreference preference = preferences.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .findFirst()
                .orElse(preferences.isEmpty() ? null : preferences.get(0));
        
        if (preference != null) {
            preference.setLastTriggeredAt(LocalDateTime.now().plusHours(24));
            smaPreferenceRepository.save(preference);
        }
    }
    
    @Transactional
    public void postponeMeal(Long userId, MealType mealType, int minutes) {
        List<SmaPreference> preferences = smaPreferenceRepository.findByUserIdAndMealType(userId, mealType);
        SmaPreference preference = preferences.stream()
                .filter(p -> Boolean.TRUE.equals(p.getIsActive()))
                .findFirst()
                .orElse(preferences.isEmpty() ? null : preferences.get(0));
        
        if (preference != null) {
            preference.setLastTriggeredAt(LocalDateTime.now().plusMinutes(minutes));
            smaPreferenceRepository.save(preference);
        }
    }
    
    @Transactional
    public void recordFeedback(Long userId, Long recommendationId, String feedback, String comment) {
        System.out.println("Recording feedback for user " + userId + ", recommendation " + recommendationId + 
            ", feedback: " + feedback + ", comment: " + comment);
    }
    
    public Map<String, Object> getWeeklySummary(Long userId) {
        List<SmaPreference> preferences = smaPreferenceRepository.findByUserId(userId);
        
        int totalMealsScheduled = preferences.size() * 7;
        int totalMealsTriggered = (int) preferences.stream()
                .filter(p -> p.getLastTriggeredAt() != null)
                .count();
        
        return Map.of(
            "totalMealsScheduled", totalMealsScheduled,
            "totalMealsTriggered", totalMealsTriggered,
            "preferencesCount", preferences.size(),
            "mostPreferredMealType", preferences.isEmpty() ? "None" : preferences.get(0).getMealType().name()
        );
    }
    
    private SmaPreferenceResponse mapToResponse(SmaPreference preference) {
        AddressResponse addressResponse = null;
        if (preference.getDeliveryAddress() != null) {
            Address addr = preference.getDeliveryAddress();
            addressResponse = AddressResponse.builder()
                    .id(addr.getId())
                    .label(addr.getLabel())
                    .addressLine(addr.getAddressLine())
                    .landmark(addr.getLandmark())
                    .location(addr.getLocation())
                    .latitude(addr.getLatitude())
                    .longitude(addr.getLongitude())
                    .isDefault(addr.getIsDefault())
                    .build();
        }
        
        return SmaPreferenceResponse.builder()
                .id(preference.getId())
                .mealType(preference.getMealType())
                .scheduledTime(preference.getScheduledTime())
                .dietType(preference.getDietType())
                .minCalories(preference.getMinCalories())
                .maxCalories(preference.getMaxCalories())
                .minProtein(preference.getMinProtein())
                .maxProtein(preference.getMaxProtein())
                .minCarbs(preference.getMinCarbs())
                .maxCarbs(preference.getMaxCarbs())
                .minFat(preference.getMinFat())
                .maxFat(preference.getMaxFat())
                .minFiber(preference.getMinFiber())
                .maxFiber(preference.getMaxFiber())
                .maxBudget(preference.getMaxBudget())
                .deliveryAddress(addressResponse)
                .isActive(preference.getIsActive())
                .daysOfWeek(preference.getDaysOfWeek())
                .includeWeekends(preference.getIncludeWeekends())
                .createdAt(preference.getCreatedAt())
                .expiresAt(preference.getExpiresAt())
                .lastTriggeredAt(preference.getLastTriggeredAt())
                .build();
    }
}
