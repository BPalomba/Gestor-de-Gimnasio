package com.gymsaas.modules.plan.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class UpdatePlanRequest {

    @Size(max = 150)
    private String name;

    private String description;

//    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal price;

    @Min(value = 1, message = "La duración debe ser al menos 1 día")
    private Integer durationDays;

    @Min(value = 1)
    private Integer sessionsPerWeek;

    private Boolean publicPlan;

    private Boolean active;
}