package com.smartmeal.service;

import com.smartmeal.dto.request.AddressRequest;
import com.smartmeal.dto.response.AddressResponse;
import com.smartmeal.model.Address;
import com.smartmeal.model.User;
import com.smartmeal.repository.AddressRepository;
import com.smartmeal.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {
    
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    
    public AddressService(AddressRepository addressRepository, UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }
    
    public List<AddressResponse> getAddressesByUserId(Long userId) {
        return addressRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    public AddressResponse getAddressById(Long id, Long userId) {
        return addressRepository.findByIdAndUserId(id, userId)
                .map(this::mapToResponse)
                .orElse(null);
    }
    
    @Transactional
    public AddressResponse saveAddress(Long userId, AddressRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Address address;
        if (Boolean.TRUE.equals(request.getIsDefault())) {
            addressRepository.clearDefaultForUser(userId);
        }
        
        address = Address.builder()
                .user(user)
                .label(request.getLabel())
                .addressLine(request.getAddressLine())
                .landmark(request.getLandmark())
                .location(request.getLocation())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .isDefault(request.getIsDefault())
                .build();
        
        address = addressRepository.save(address);
        return mapToResponse(address);
    }
    
    @Transactional
    public void deleteAddress(Long id, Long userId) {
        Address address = addressRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Address not found or access denied"));
        addressRepository.delete(address);
    }
    
    public Address getAddressEntityById(Long id) {
        return addressRepository.findById(id).orElse(null);
    }
    
    private AddressResponse mapToResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .label(address.getLabel())
                .addressLine(address.getAddressLine())
                .landmark(address.getLandmark())
                .location(address.getLocation())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .isDefault(address.getIsDefault())
                .build();
    }
}
