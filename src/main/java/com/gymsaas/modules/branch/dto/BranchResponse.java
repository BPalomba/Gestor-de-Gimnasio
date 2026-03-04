package com.gymsaas.modules.branch.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class BranchResponse {

    private UUID       id;
    private String     name;
    private String     address;
    private String     city;
    private String     province;
    private BigDecimal lat;
    private BigDecimal lng;
    private String     phone;
    private boolean    active;
    private OffsetDateTime createdAt;
}