package com.gymsaas.modules.member.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class MemberResponse {

    private UUID   id;
    private String firstName;
    private String lastName;
    private String dni;
    private String email;
    private String phone;
    private LocalDate      birthDate;
    private String statusCode;
    private String statusDescription;
    private String         branchName;
    private OffsetDateTime createdAt;
}