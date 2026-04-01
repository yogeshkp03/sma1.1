package com.smartmeal.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    
    @NotBlank(message = "Firebase UID is required")
    private String firebaseUid;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    private String displayName;
    
    private String phone;
    
    private String profileImageUrl;
}
