package com.gymsaas.modules.payment.dto;

import com.gymsaas.modules.payment.Payment.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreatePaymentRequest {

    @NotNull(message = "El socio es obligatorio")
    private UUID memberId;

    @NotNull(message = "La membresía es obligatoria")
    private UUID membershipId;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    private BigDecimal amount;

    @NotNull(message = "El método de pago es obligatorio")
    private PaymentMethod paymentMethod;

    @Size(max = 100, message = "La referencia no puede superar los 100 caracteres")
    private String referenceCode;

    private String notes;
}