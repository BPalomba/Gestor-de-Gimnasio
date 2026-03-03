package com.gymsaas.modules.plan;

import com.gymsaas.modules.plan.dto.CreatePlanRequest;
import com.gymsaas.modules.plan.dto.PlanResponse;
import com.gymsaas.modules.plan.dto.UpdatePlanRequest;
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
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Tag(name = "Planes", description = "Gestión de planes de membresía")
public class PlanController {

    private final PlanService planService;

    @Operation(summary = "Listar planes activos del gimnasio")
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<PlanResponse>>> findAll() {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(planService.findAll(gymId)));
    }

    @Operation(summary = "Obtener plan por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<PlanResponse>> findById(
            @PathVariable UUID id) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(planService.findById(gymId, id)));
    }

    @Operation(summary = "Crear plan de membresía")
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse<PlanResponse>> create(
            @Valid @RequestBody CreatePlanRequest req) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(planService.create(gymId, req)));
    }

    @Operation(summary = "Actualizar plan")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse<PlanResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePlanRequest req) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(planService.update(gymId, id, req)));
    }

    @Operation(summary = "Desactivar plan",
            description = "No elimina el plan, lo desactiva para preservar el historial")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deactivate(@PathVariable UUID id) {
        planService.deactivate(GymContextHolder.getRequired(), id);
        return ResponseEntity.noContent().build();
    }
}