package com.smartmeal.controller;

import com.smartmeal.dto.request.AddressRequest;
import com.smartmeal.dto.response.AddressResponse;
import com.smartmeal.dto.response.ApiResponse;
import com.smartmeal.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {
    
    private final AddressService addressService;
    
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<AddressResponse>>> getAddresses(@PathVariable Long userId) {
        List<AddressResponse> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(addresses));
    }
    
    @GetMapping("/{id}/user/{userId}")
    public ResponseEntity<ApiResponse<AddressResponse>> getAddress(@PathVariable Long id, 
                                                                    @PathVariable Long userId) {
        AddressResponse address = addressService.getAddressById(id, userId);
        if (address == null) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Address not found"));
        }
        return ResponseEntity.ok(ApiResponse.success(address));
    }
    
    @PostMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<AddressResponse>> saveAddress(
            @PathVariable Long userId,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse address = addressService.saveAddress(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Address saved successfully", address));
    }
    
    @DeleteMapping("/{id}/user/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(@PathVariable Long id, 
                                                           @PathVariable Long userId) {
        addressService.deleteAddress(id, userId);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully", null));
    }
}
