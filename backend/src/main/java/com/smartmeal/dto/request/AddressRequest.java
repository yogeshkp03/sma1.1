package com.smartmeal.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddressRequest {
    
    private String label;
    
    @NotBlank(message = "Address line is required")
    private String addressLine;
    
    private String landmark;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private Double latitude;
    
    private Double longitude;
    
    private Boolean isDefault = false;
}
