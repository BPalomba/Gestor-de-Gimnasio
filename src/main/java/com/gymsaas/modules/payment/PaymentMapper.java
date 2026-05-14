package com.gymsaas.modules.payment;

import com.gymsaas.modules.payment.dto.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setMemberId(payment.getMember().getId());
        response.setMemberFullName(
                payment.getMember().getFirstName() + " " +
                        payment.getMember().getLastName());
        response.setMembershipId(payment.getMembership().getId());
        response.setAmount(payment.getAmount());
        response.setCurrency(payment.getCurrency());
        response.setPaymentMethod(payment.getPaymentMethod());
        response.setStatus(payment.getStatus());
        response.setReferenceCode(payment.getReferenceCode());
        response.setRevenueShareAmount(payment.getRevenueShareAmount());
        response.setRevenueSharePaid(payment.isRevenueSharePaid());
        response.setNotes(payment.getNotes());
        response.setCreatedAt(payment.getCreatedAt());

        if (payment.getRegisteredBy() != null) {
            response.setRegisteredByName(
                    payment.getRegisteredBy().getFirstName() + " " +
                            payment.getRegisteredBy().getLastName());
        }

        return response;
    }
}