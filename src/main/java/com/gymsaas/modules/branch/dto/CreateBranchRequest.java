package com.gymsaas.modules.branch.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CreateBranchRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String name;

    private String address;
    private String city;
    private String province;

    @DecimalMin(value = "-90.0",  message = "Latitud inválida")
    @DecimalMax(value = "90.0",   message = "Latitud inválida")
    private BigDecimal lat;

    @DecimalMin(value = "-180.0", message = "Longitud inválida")
    @DecimalMax(value = "180.0",  message = "Longitud inválida")
    private BigDecimal lng;

    @Size(max = 30)
    private String phone;
}