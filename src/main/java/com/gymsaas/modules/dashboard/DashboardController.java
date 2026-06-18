package com.gymsaas.modules.dashboard;

import com.gymsaas.modules.dashboard.dto.DashboardResponse;
import com.gymsaas.shared.dto.ApiResponse;
import com.gymsaas.shared.security.GymContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Métricas y estadísticas del gimnasio")
public class DashboardController {

    private final DashboardService dashboardService;

    @Operation(
            summary = "Obtener métricas del dashboard",
            description = "Retorna todos los indicadores financieros y operativos del gimnasio en tiempo real"
    )
    @GetMapping
    @PreAuthorize("hasAuthority('DASHBOARD_VIEW')")
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard() {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(dashboardService.getDashboard(gymId)));
    }
}