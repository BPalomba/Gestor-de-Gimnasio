package com.gymsaas.modules.auth.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String email,
        String role,
        String gymId
) {}