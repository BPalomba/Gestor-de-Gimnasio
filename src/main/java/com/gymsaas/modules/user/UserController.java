package com.gymsaas.modules.user;

import com.gymsaas.modules.user.dto.*;
import com.gymsaas.shared.dto.ApiResponse;
import com.gymsaas.shared.security.GymContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Usuarios", description = "Gestión de operadores del sistema")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Listar usuarios del gimnasio")
    @GetMapping
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<ApiResponse<List<UserResponse>>> findAll() {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(userService.findAll(gymId)));
    }

    @Operation(summary = "Obtener usuario por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    public ResponseEntity<ApiResponse<UserResponse>> findById(
            @PathVariable UUID id) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(userService.findById(gymId, id)));
    }

    @Operation(summary = "Crear usuario operador")
    @PostMapping
    @PreAuthorize("hasAuthority('USER_CREATE')")
    public ResponseEntity<ApiResponse<UserResponse>> create(
            @Valid @RequestBody CreateUserRequest req) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(userService.create(gymId, req)));
    }

    @Operation(summary = "Actualizar usuario")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest req) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(userService.update(gymId, id, req)));
    }

    @Operation(
            summary = "Cambiar contraseña",
            description = "El propio usuario puede cambiar su contraseña verificando la actual"
    )
    @PatchMapping("/{id}/password")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<Void> changePassword(
            @PathVariable UUID id,
            @Valid @RequestBody ChangePasswordRequest req) {
        UUID gymId = GymContextHolder.getRequired();
        userService.changePassword(gymId, id, req);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desactivar usuario")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<Void> deactivate(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID gymId = GymContextHolder.getRequired();
        UUID authenticatedUserId = UUID.fromString(userDetails.getUsername());
        userService.deactivate(gymId, id, authenticatedUserId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reactivar usuario")
    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasAuthority('USER_EDIT')")
    public ResponseEntity<Void> reactivate(@PathVariable UUID id) {
        UUID gymId = GymContextHolder.getRequired();
        userService.reactivate(gymId, id);
        return ResponseEntity.noContent().build();
    }
}