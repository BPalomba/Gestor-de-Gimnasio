package com.gymsaas.modules.membership;

import com.gymsaas.modules.membership.dto.CreateMembershipRequest;
import com.gymsaas.modules.membership.dto.FreezeMembershipRequest;
import com.gymsaas.modules.membership.dto.MembershipResponse;
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
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
@Tag(name = "Membresías", description = "Gestión de membresías de socios")
public class MembershipController {

    private final MembershipService membershipService;

    @Operation(summary = "Obtener membresía por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<MembershipResponse>> findById(
            @PathVariable UUID id) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(membershipService.findById(gymId, id)));
    }

    @Operation(summary = "Historial de membresías de un socio")
    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<List<MembershipResponse>>> findByMember(
            @PathVariable UUID memberId) {
        return ResponseEntity.ok(
                ApiResponse.ok(membershipService.findByMember(memberId)));
    }

    @Operation(summary = "Membresía activa de un socio")
    @GetMapping("/member/{memberId}/active")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<MembershipResponse>> findActive(
            @PathVariable UUID memberId) {
        return ResponseEntity.ok(
                ApiResponse.ok(membershipService.findActiveMembership(memberId)));
    }

    @Operation(summary = "Membresías próximas a vencer",
            description = "Retorna membresías que vencen en los próximos N días")
    @GetMapping("/expiring")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse<List<MembershipResponse>>> findExpiring(
            @RequestParam(defaultValue = "7") int days) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(membershipService.findExpiring(gymId, days)));
    }

    @Operation(summary = "Crear membresía para un socio")
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse<MembershipResponse>> create(
            @Valid @RequestBody CreateMembershipRequest req) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(membershipService.create(gymId, req)));
    }

    @Operation(summary = "Congelar membresía")
    @PatchMapping("/{id}/freeze")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse<MembershipResponse>> freeze(
            @PathVariable UUID id,
            @Valid @RequestBody FreezeMembershipRequest req) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(membershipService.freeze(gymId, id, req)));
    }

    @Operation(summary = "Descongelar membresía",
            description = "Extiende el vencimiento por los días que estuvo congelada")
    @PatchMapping("/{id}/unfreeze")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse<MembershipResponse>> unfreeze(
            @PathVariable UUID id) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(membershipService.unfreeze(gymId, id)));
    }

    @Operation(summary = "Cancelar membresía")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        membershipService.cancel(GymContextHolder.getRequired(), id);
        return ResponseEntity.noContent().build();
    }
}