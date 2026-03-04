package com.gymsaas.modules.member;

import com.gymsaas.modules.member.dto.CreateMemberRequest;
import com.gymsaas.modules.member.dto.MemberResponse;
import com.gymsaas.modules.member.dto.UpdateMemberRequest;
import com.gymsaas.shared.dto.ApiResponse;
import com.gymsaas.shared.security.GymContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Socios", description = "Gestión de socios del gimnasio")
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "Listar socios")
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<Page<MemberResponse>>> findAll(
            @RequestParam(defaultValue = "ACTIVE") Member.MemberStatus status,
            Pageable pageable) {

        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(memberService.findAll(gymId, status, pageable)));
    }

    @Operation(summary = "Buscar socios")
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<Page<MemberResponse>>> search(
            @RequestParam String q,
            Pageable pageable) {

        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(memberService.search(gymId, q, pageable)));
    }

    @Operation(summary = "Obtener socio por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN','STAFF')")
    public ResponseEntity<ApiResponse<MemberResponse>> findById(
            @PathVariable UUID id) {

        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(memberService.findById(gymId, id)));
    }

    @Operation(summary = "Crear socio")
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse<MemberResponse>> create(
            @Valid @RequestBody CreateMemberRequest req) {

        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(memberService.create(gymId, req)));
    }

    @Operation(summary = "Actualizar socio")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<ApiResponse<MemberResponse>> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateMemberRequest req) {

        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(memberService.update(gymId, id, req)));
    }

    @Operation(summary = "Suspender socio")
    @PatchMapping("/{id}/suspend")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> suspend(@PathVariable UUID id) {
        memberService.suspend(GymContextHolder.getRequired(), id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reactivar socio")
    @PatchMapping("/{id}/reactivate")
    @PreAuthorize("hasAnyRole('OWNER','ADMIN')")
    public ResponseEntity<Void> reactivate(@PathVariable UUID id) {
        memberService.reactivate(GymContextHolder.getRequired(), id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Cancelar socio")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> cancel(@PathVariable UUID id) {
        memberService.cancel(GymContextHolder.getRequired(), id);
        return ResponseEntity.noContent().build();
    }
}