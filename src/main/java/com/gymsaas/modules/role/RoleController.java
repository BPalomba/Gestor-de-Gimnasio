package com.gymsaas.modules.role;

import com.gymsaas.modules.role.dto.CreateRoleRequest;
import com.gymsaas.modules.role.dto.PermissionResponse;
import com.gymsaas.modules.role.dto.RoleResponse;
import com.gymsaas.modules.role.dto.UpdateRoleRequest;
import com.gymsaas.shared.dto.ApiResponse;
import com.gymsaas.shared.security.GymContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles y Permisos", description = "Gestión de roles y permisos del gimnasio")
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "Listar roles del gimnasio")
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ApiResponse<List<RoleResponse>>> findAll() {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(roleService.findAll(gymId)));
    }

    @Operation(summary = "Listar todos los permisos disponibles",
            description = "Retorna el catálogo completo de permisos agrupados por módulo")
    @GetMapping("/permissions")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ApiResponse<List<PermissionResponse>>> findAllPermissions() {
        return ResponseEntity.ok(
                ApiResponse.ok(roleService.findAllPermissions()));
    }

    @Operation(summary = "Obtener rol por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    public ResponseEntity<ApiResponse<RoleResponse>> findById(
            @PathVariable UUID id) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(roleService.findById(gymId, id)));
    }

    @Operation(summary = "Crear rol con permisos")
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    public ResponseEntity<ApiResponse<RoleResponse>> create(
            @Valid @RequestBody CreateRoleRequest req) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(roleService.create(gymId, req)));
    }

    @Operation(summary = "Actualizar rol y sus permisos")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_EDIT')")
    public ResponseEntity<ApiResponse<RoleResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateRoleRequest req) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(roleService.update(gymId, id, req)));
    }
}