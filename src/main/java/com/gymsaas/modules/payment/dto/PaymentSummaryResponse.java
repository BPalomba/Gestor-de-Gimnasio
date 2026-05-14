package com.gymsaas.modules.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class PaymentSummaryResponse {

    private BigDecimal totalAmount;
    private BigDecimal totalRevenueShare;
    private long       totalPayments;
    private BigDecimal averagePayment;
}