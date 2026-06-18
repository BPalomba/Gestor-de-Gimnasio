package com.gymsaas.modules.user.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter
public class UserResponse {

    private UUID   id;
    private String firstName;
    private String lastName;
    private String email;
    private String roleName;
    private UUID   roleId;
    private String branchName;
    private UUID   branchId;
    private OffsetDateTime lastLoginAt;
    private OffsetDateTime createdAt;
}