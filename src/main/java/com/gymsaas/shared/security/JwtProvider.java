package com.gymsaas.shared.security;

import com.gymsaas.config.JwtConfig;
import com.gymsaas.modules.role.Permission;
import com.gymsaas.modules.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtProvider {

    private final JwtConfig jwtConfig;

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Genera el access token con los datos del usuario
    public String generateAccessToken(User user) {
        List<String> permissions = user.getRole().getPermissions()
                .stream()
                .map(Permission::getCode)
                .toList();

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("gymId",      user.getGym().getId().toString())
                .claim("role",       user.getRole().getName())
                .claim("email",      user.getEmail())
                .claim("permissions", permissions)  // ← nuevo
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getExpirationMs()))
                .signWith(getKey())
                .compact();
    }

    // Genera el refresh token (solo lleva el userId)
    public String generateRefreshToken(UUID userId) {
        return Jwts.builder()
                .subject(userId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()
                        + jwtConfig.getRefreshExpirationMs()))
                .signWith(getKey())
                .compact();
    }

    // Extrae todos los datos del token
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Verifica si el token es válido (no expiró, no fue alterado)
    public boolean isValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Token inválido: {}", e.getMessage());
            return false;
        }
    }
}