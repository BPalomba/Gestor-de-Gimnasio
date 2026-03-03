package com.gymsaas.shared.dto;

public record ApiResponse<T>(
        T data,
        String message,
        boolean success
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(data, null, true);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(data, "Creado exitosamente", true);
    }
}