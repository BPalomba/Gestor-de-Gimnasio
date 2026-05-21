package com.gymsaas.modules.role.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

@Getter @Setter
public class RoleResponse {
    private UUID                    id;
    private String                  name;
    private String                  description;
    private boolean                 active;
    private Set<PermissionResponse> permissions;
    private OffsetDateTime          createdAt;
}