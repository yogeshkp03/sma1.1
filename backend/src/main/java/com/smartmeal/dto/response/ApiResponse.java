package com.smartmeal.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    @Builder.Default
    private Map<String, Object> metadata = new HashMap<>();
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message("Success")
                .data(data)
                .metadata(new HashMap<>())
                .build();
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .metadata(new HashMap<>())
                .build();
    }
    
    public static <T> ApiResponse<T> success(T data, Object... metadataPairs) {
        Map<String, Object> metadata = new HashMap<>();
        for (int i = 0; i < metadataPairs.length; i += 2) {
            if (i + 1 < metadataPairs.length) {
                metadata.put(String.valueOf(metadataPairs[i]), metadataPairs[i + 1]);
            }
        }
        return ApiResponse.<T>builder()
                .success(true)
                .message("Success")
                .data(data)
                .metadata(metadata)
                .build();
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .metadata(new HashMap<>())
                .build();
    }
    
    public ApiResponse<T> withMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }
}
