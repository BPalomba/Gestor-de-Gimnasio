package com.gymsaas.modules.user.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter @Setter
public class UpdateUserRequest {

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Email(message = "El email no tiene un formato válido")
    private String email;

    private UUID roleId;

    private UUID branchId;
}