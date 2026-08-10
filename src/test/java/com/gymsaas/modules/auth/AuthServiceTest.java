package com.gymsaas.modules.auth;

import com.gymsaas.modules.auth.dto.AuthResponse;
import com.gymsaas.modules.auth.dto.LoginRequest;
import com.gymsaas.modules.role.Role;
import com.gymsaas.modules.user.User;
import com.gymsaas.modules.user.UserRepository;
import com.gymsaas.shared.exception.BusinessException;
import com.gymsaas.shared.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — Tests unitarios")
class AuthServiceTest {

    @Mock private UserRepository  userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtProvider     jwtProvider;

    @InjectMocks private AuthService authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setName("Owner");
        role.setPermissions(new HashSet<>());

        mockUser = new User();
        mockUser.setEmail("admin@gimnasio.com");
        mockUser.setPasswordHash("$2a$12$hashedPassword");
        mockUser.setFirstName("Admin");
        mockUser.setLastName("Demo");
        mockUser.setActive(true);
        mockUser.setRole(role);
    }

    @Test
    @DisplayName("CP01 — Login exitoso con credenciales válidas")
    void login_conCredencialesValidas_retornaTokens() {
        when(userRepository.findByEmailForLogin("admin@gimnasio.com"))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("password123", mockUser.getPasswordHash()))
                .thenReturn(true);
        when(jwtProvider.generateAccessToken(mockUser))
                .thenReturn("access-token-mock");
        when(jwtProvider.generateRefreshToken(mockUser.getId()))
                .thenReturn("refresh-token-mock");

        AuthResponse response = authService.login(
                new LoginRequest("admin@gimnasio.com", "password123"));

        assertThat(response.accessToken()).isEqualTo("access-token-mock");
        assertThat(response.refreshToken()).isEqualTo("refresh-token-mock");
        assertThat(response.email()).isEqualTo("admin@gimnasio.com");
    }

    @Test
    @DisplayName("CP02 — Login falla con contraseña incorrecta")
    void login_conPasswordIncorrecto_lanzaBusinessException() {
        when(userRepository.findByEmailForLogin("admin@gimnasio.com"))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrong", mockUser.getPasswordHash()))
                .thenReturn(false);

        assertThatThrownBy(() ->
                authService.login(new LoginRequest("admin@gimnasio.com", "wrong")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Credenciales incorrectas");
    }

    @Test
    @DisplayName("Login falla con email inexistente")
    void login_conEmailInexistente_lanzaBusinessException() {
        when(userRepository.findByEmailForLogin("noexiste@mail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                authService.login(new LoginRequest("noexiste@mail.com", "password")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Credenciales incorrectas");
    }

    @Test
    @DisplayName("Login falla con usuario inactivo")
    void login_conUsuarioInactivo_lanzaBusinessException() {
        mockUser.setActive(false);
        when(userRepository.findByEmailForLogin("admin@gimnasio.com"))
                .thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() ->
                authService.login(new LoginRequest("admin@gimnasio.com", "password123")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Cuenta deshabilitada");
    }
}