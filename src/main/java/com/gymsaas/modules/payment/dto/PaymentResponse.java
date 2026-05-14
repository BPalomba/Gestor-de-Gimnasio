package com.gymsaas.modules.payment.dto;

import com.gymsaas.modules.payment.Payment.PaymentMethod;
import com.gymsaas.modules.payment.Payment.PaymentStatus;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class PaymentResponse {

    private UUID          id;
    private UUID          memberId;
    private String        memberFullName;
    private UUID          membershipId;
    private BigDecimal    amount;
    private String        currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String        referenceCode;
    private BigDecimal    revenueShareAmount;
    private boolean       revenueSharePaid;
    private String        registeredByName;
    private String        notes;
    private OffsetDateTime createdAt;
}