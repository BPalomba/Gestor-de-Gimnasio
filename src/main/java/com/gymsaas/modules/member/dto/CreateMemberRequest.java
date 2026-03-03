package com.gymsaas.modules.member.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class CreateMemberRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    private String lastName;

    @Size(max = 20, message = "El DNI no puede superar los 20 caracteres")
    private String dni;

    @Email(message = "El email no tiene un formato válido")
    private String email;

    @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres")
    private String phone;

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate birthDate;

    private UUID branchId;

    private String notes;
}