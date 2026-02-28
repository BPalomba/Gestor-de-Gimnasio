package com.gymsaas.shared.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Buscar el header Authorization
        String header = request.getHeader("Authorization");

        // Si no hay token, dejar pasar (Spring Security decidirá después)
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer el token (sacar el "Bearer " del principio)
        String token = header.substring(7);

        // 3. Validar el token
        if (!jwtProvider.isValid(token)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
            return;
        }

        // 4. Extraer los datos del token
        Claims claims = jwtProvider.parseToken(token);
        String userId = claims.getSubject();
        String gymId  = claims.get("gymId", String.class);

        // 5. Cargar el gymId en el ThreadLocal para los Services
        if (gymId != null) {
            GymContextHolder.set(UUID.fromString(gymId));
        }

        // 6. Cargar el usuario y registrarlo en Spring Security
        UserDetails userDetails = userDetailsService.loadUserByUsername(userId);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
        authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 7. Continuar con el request
        filterChain.doFilter(request, response);

        // 8. Limpiar el ThreadLocal al terminar (MUY IMPORTANTE)
        GymContextHolder.clear();
    }
}