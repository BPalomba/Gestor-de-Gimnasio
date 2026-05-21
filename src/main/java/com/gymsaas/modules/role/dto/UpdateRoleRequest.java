package com.gymsaas.modules.role.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
public class UpdateRoleRequest {

    @Size(max = 100)
    private String name;

    private String description;

    private Set<UUID> permissionIds;

    private Boolean active;
}