package com.gymsaas.modules.payment;

import com.gymsaas.modules.payment.dto.CreatePaymentRequest;
import com.gymsaas.modules.payment.dto.PaymentResponse;
import com.gymsaas.modules.payment.dto.PaymentSummaryResponse;
import com.gymsaas.shared.dto.ApiResponse;
import com.gymsaas.shared.security.GymContextHolder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Gestión de pagos y cobros del gimnasio")
public class PaymentController {

    private final PaymentService paymentService;

    @Operation(
            summary = "Listar pagos por período",
            description = "Retorna pagos paginados entre dos fechas. Por defecto el mes actual."
    )
    @GetMapping
    @PreAuthorize("hasAuthority('PAYMENT_VIEW')")
    public ResponseEntity<ApiResponse<Page<PaymentResponse>>> findAll(
            @Parameter(description = "Fecha desde (yyyy-MM-dd)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,

            @Parameter(description = "Fecha hasta (yyyy-MM-dd)")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,

            Pageable pageable) {

        UUID gymId = GymContextHolder.getRequired();
        LocalDate dateFrom = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate dateTo   = to   != null ? to   : LocalDate.now();

        return ResponseEntity.ok(
                ApiResponse.ok(paymentService.findAll(gymId, dateFrom, dateTo, pageable)));
    }

    @Operation(summary = "Resumen financiero del período")
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('PAYMENT_VIEW')")
    public ResponseEntity<ApiResponse<PaymentSummaryResponse>> getSummary(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        UUID gymId = GymContextHolder.getRequired();
        LocalDate dateFrom = from != null ? from : LocalDate.now().withDayOfMonth(1);
        LocalDate dateTo   = to   != null ? to   : LocalDate.now();

        return ResponseEntity.ok(
                ApiResponse.ok(paymentService.getSummary(gymId, dateFrom, dateTo)));
    }

    @Operation(summary = "Obtener pago por ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PAYMENT_VIEW')")
    public ResponseEntity<ApiResponse<PaymentResponse>> findById(
            @PathVariable UUID id) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(paymentService.findById(gymId, id)));
    }

    @Operation(
            summary = "Historial de pagos de un socio",
            description = "Retorna todos los pagos de un socio ordenados por fecha descendente."
    )
    @GetMapping("/member/{memberId}")
    @PreAuthorize("hasAuthority('PAYMENT_VIEW')")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> findByMember(
            @PathVariable UUID memberId) {
        return ResponseEntity.ok(
                ApiResponse.ok(paymentService.findByMember(memberId)));
    }

    @Operation(
            summary = "Registrar pago manual",
            description = "El staff registra un cobro en efectivo, transferencia u otro método. " +
                    "El revenue share se calcula automáticamente."
    )
    @PostMapping
    @PreAuthorize("hasAuthority('PAYMENT_CREATE')")
    public ResponseEntity<ApiResponse<PaymentResponse>> create(
            @Valid @RequestBody CreatePaymentRequest req) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(paymentService.create(gymId, req)));
    }

    @Operation(
            summary = "Reembolsar pago",
            description = "Marca el pago como reembolsado. " +
                    "No es posible si el revenue share ya fue liquidado."
    )
    @PatchMapping("/{id}/refund")
    @PreAuthorize("hasAuthority('PAYMENT_REFUND')")
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(
            @PathVariable UUID id) {
        UUID gymId = GymContextHolder.getRequired();
        return ResponseEntity.ok(
                ApiResponse.ok(paymentService.refund(gymId, id)));
    }
}