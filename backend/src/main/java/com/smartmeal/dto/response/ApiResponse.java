package com.smartmeal.dto.response;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse<T> {
    
    private boolean success;
    private String message;
    private T data;
    private Map<String, Object> metadata;
    
    public ApiResponse() {
        this.metadata = new HashMap<>();
    }
    
    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.metadata = new HashMap<>();
    }
    
    public ApiResponse(boolean success, String message, T data, Map<String, Object> metadata) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.metadata = metadata != null ? metadata : new HashMap<>();
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data, new HashMap<>());
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data, new HashMap<>());
    }
    
    public static <T> ApiResponse<T> success(T data, Map<String, Object> metadata) {
        return new ApiResponse<>(true, "Success", data, metadata);
    }
    
    public static <T> ApiResponse<T> success(T data, String metaKey, Object metaValue) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(metaKey, metaValue);
        return new ApiResponse<>(true, "Success", data, metadata);
    }
    
    public static <T> ApiResponse<T> success(T data, String key1, Object val1, String key2, Object val2) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(key1, val1);
        metadata.put(key2, val2);
        return new ApiResponse<>(true, "Success", data, metadata);
    }
    
    public static <T> ApiResponse<T> success(T data, String key1, Object val1, String key2, Object val2, String key3, Object val3) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(key1, val1);
        metadata.put(key2, val2);
        metadata.put(key3, val3);
        return new ApiResponse<>(true, "Success", data, metadata);
    }
    
    public static <T> ApiResponse<T> success(T data, String key1, Object val1, String key2, Object val2, String key3, Object val3, String key4, Object val4) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(key1, val1);
        metadata.put(key2, val2);
        metadata.put(key3, val3);
        metadata.put(key4, val4);
        return new ApiResponse<>(true, "Success", data, metadata);
    }
    
    public static <T> ApiResponse<T> success(T data, String key1, Object val1, String key2, Object val2, String key3, Object val3, String key4, Object val4, String key5, Object val5) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(key1, val1);
        metadata.put(key2, val2);
        metadata.put(key3, val3);
        metadata.put(key4, val4);
        metadata.put(key5, val5);
        return new ApiResponse<>(true, "Success", data, metadata);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null, new HashMap<>());
    }
    
    public ApiResponse<T> withMetadata(String key, Object value) {
        this.metadata.put(key, value);
        return this;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
}
