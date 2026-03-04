package com.gymsaas.modules.membership.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class FreezeMembershipRequest {

    @NotNull(message = "La fecha de inicio del congelamiento es obligatoria")
    private LocalDate frozenSince;
}