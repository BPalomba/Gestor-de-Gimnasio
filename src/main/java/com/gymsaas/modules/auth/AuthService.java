package com.gymsaas.modules.auth;

import com.gymsaas.modules.auth.dto.AuthResponse;
import com.gymsaas.modules.auth.dto.LoginRequest;
import com.gymsaas.modules.user.User;
import com.gymsaas.modules.user.UserRepository;
import com.gymsaas.shared.exception.BusinessException;
import com.gymsaas.shared.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {


        // 1. Buscar el usuario por email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException(
                        "Credenciales incorrectas", HttpStatus.UNAUTHORIZED));

        // 2. Verificar que la cuenta está activa
        if (!user.isActive()) {
            throw new BusinessException(
                    "Cuenta deshabilitada", HttpStatus.UNAUTHORIZED);
        }

        // 3. Verificar la contraseña
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(
                    "Credenciales incorrectas", HttpStatus.UNAUTHORIZED);
        }

        // 4. Generar los tokens
        String accessToken  = jwtProvider.generateAccessToken(user);
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        return new AuthResponse(
                accessToken,
                refreshToken,
                user.getEmail(),
                user.getRole().name(),
                user.getGym() != null ? user.getGym().getId().toString() : null
        );
    }
}