package com.gymsaas.modules.branch;

import com.gymsaas.modules.branch.dto.CreateBranchRequest;
import com.gymsaas.modules.branch.dto.BranchResponse;
import com.gymsaas.modules.branch.dto.UpdateBranchRequest;
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
@RequestMapping("/api/branches")
@RequiredArgsConstructor
@Tag(name = "Sucursales", description = "Gestión de sucursales del gimnasio")
public class BranchController {

    private final BranchService branchService;

    @Operation(summary = "Listar sucursales activas")
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<BranchResponse>>> findAll() {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(branchService.findAll(gymId)));
    }

    @Operation(summary = "Obtener sucursal por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<BranchResponse>> findById(
            @PathVariable UUID id) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(branchService.findById(gymId, id)));
    }

    @Operation(summary = "Crear sucursal")
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> create(
            @Valid @RequestBody CreateBranchRequest req) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(branchService.create(gymId, req)));
    }

    @Operation(summary = "Actualizar sucursal")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse<BranchResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateBranchRequest req) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(branchService.update(gymId, id, req)));
    }

    @Operation(summary = "Desactivar sucursal")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        branchService.deactivate(GymContextHolder.getRequired(), id);
        return ResponseEntity.noContent().build();
    }
}
