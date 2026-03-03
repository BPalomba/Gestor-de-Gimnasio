package com.gymsaas.modules.plan.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CreatePlanRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String name;

    private String description;

//    @NotNull(message = "El precio es obligatorio")
//    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @NotBlank(message = "La moneda es obligatoria")
    @Size(min = 3, max = 3, message = "La moneda debe ser un código de 3 letras (ARS, USD)")
    private String currency = "ARS";

    @NotNull(message = "La duración es obligatoria")
    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    private Integer durationDays;

    @Min(value = 1, message = "Las sesiones por semana deben ser al menos 1")
    private Integer sessionsPerWeek;

    private boolean publicPlan = true;
}