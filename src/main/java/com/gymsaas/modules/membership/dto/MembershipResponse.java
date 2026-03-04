package com.gymsaas.modules.membership.dto;

import com.gymsaas.modules.membership.Membership.MembershipStatus;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class MembershipResponse {

    private UUID             id;
    private UUID             memberId;
    private String           memberFullName;
    private UUID             planId;
    private String           planName;
    private LocalDate        startDate;
    private LocalDate        endDate;
    private MembershipStatus status;
    private BigDecimal       pricePaid;
    private int              frozenDays;
    private LocalDate        frozenSince;
    private String           notes;
    private OffsetDateTime   createdAt;

    // Calculado: días que faltan para vencer
    private long daysUntilExpiration;
}