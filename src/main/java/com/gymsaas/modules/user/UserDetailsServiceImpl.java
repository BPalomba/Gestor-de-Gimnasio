package com.gymsaas.modules.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    // Spring Security llama a este método con el "username"
    // En nuestro caso el username es el UUID del usuario
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + userId));

        // Convertir permisos del rol en authorities de Spring Security
        List<GrantedAuthority> authorities = user.getRole().getPermissions()
                .stream()
                .map(p -> new SimpleGrantedAuthority(p.getCode().toString()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getId().toString(),
                user.getPasswordHash(),
                authorities
        );
    }
}