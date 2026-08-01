package com.gymsaas.modules.auth.dto;

import java.util.List;
import java.util.UUID;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String email,
        String role,
        String gymId,
        List<String> permissions  // ← agregar esto
) {}