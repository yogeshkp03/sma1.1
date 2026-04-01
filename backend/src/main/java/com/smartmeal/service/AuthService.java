package com.smartmeal.service;

import com.smartmeal.dto.request.AuthRequest;
import com.smartmeal.dto.request.LoginRequest;
import com.smartmeal.dto.request.RegisterRequest;
import com.smartmeal.dto.response.AuthResponse;
import com.smartmeal.dto.response.UserResponse;
import com.smartmeal.model.User;
import com.smartmeal.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private Long jwtExpiration;
    
    private final UserRepository userRepository;
    
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @PostConstruct
    public void init() {
        System.out.println("AuthService initialized");
    }
    
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        
        if (existingUser.isEmpty()) {
            throw new RuntimeException("User not registered");
        }
        
        User user = existingUser.get();
        
        if (user.getPassword() == null || !user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        String token = generateToken(user);
        
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }
    
    @Transactional
    public AuthResponse authenticate(AuthRequest request) {
        Optional<User> existingUser = userRepository.findByFirebaseUid(request.getFirebaseUid());
        
        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
            if (request.getDisplayName() != null) {
                user.setFullName(request.getDisplayName());
            }
            if (request.getProfileImageUrl() != null) {
                user.setProfileImageUrl(request.getProfileImageUrl());
            }
        } else {
            user = User.builder()
                    .firebaseUid(request.getFirebaseUid())
                    .email(request.getEmail())
                    .fullName(request.getDisplayName())
                    .phone(request.getPhone())
                    .profileImageUrl(request.getProfileImageUrl())
                    .build();
        }
        
        user = userRepository.save(user);
        
        String token = generateToken(user);
        
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .firebaseUid(user.getFirebaseUid())
                .build();
    }
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        Optional<User> existingUser = userRepository.findByEmail(request.getEmail());
        
        if (existingUser.isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        
        User user = User.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .build();
        
        user = userRepository.save(user);
        
        String token = generateToken(user);
        
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }
    
    public Optional<User> getUserByFirebaseUid(String firebaseUid) {
        return userRepository.findByFirebaseUid(firebaseUid);
    }
    
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
    
    public UserResponse getUserResponse(Long userId) {
        return userRepository.findById(userId)
                .map(this::mapToUserResponse)
                .orElse(null);
    }
    
    private String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.getEmail())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }
    
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firebaseUid(user.getFirebaseUid())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .profileImageUrl(user.getProfileImageUrl())
                .build();
    }
}
