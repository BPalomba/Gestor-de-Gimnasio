package com.gymsaas.modules.membership.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateMembershipRequest {

    @NotNull(message = "El socio es obligatorio")
    private UUID memberId;

    @NotNull(message = "El plan es obligatorio")
    private UUID planId;

    // Si no se manda, se usa la fecha de hoy
    private LocalDate startDate;

    private String notes;
}