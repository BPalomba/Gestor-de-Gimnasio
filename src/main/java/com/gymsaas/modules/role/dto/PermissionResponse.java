package com.gymsaas.modules.role.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Getter @Setter
public class PermissionResponse {
    private UUID   id;
    private String code;
    private String description;
    private String module;
}