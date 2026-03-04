package com.gymsaas.modules.membership;

import com.gymsaas.modules.membership.dto.MembershipResponse;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class MembershipMapper {

    public MembershipResponse toResponse(Membership ms) {
        MembershipResponse response = new MembershipResponse();
        response.setId(ms.getId());
        response.setMemberId(ms.getMember().getId());
        response.setMemberFullName(
                ms.getMember().getFirstName() + " " + ms.getMember().getLastName());
        response.setPlanId(ms.getPlan().getId());
        response.setPlanName(ms.getPlan().getName());
        response.setStartDate(ms.getStartDate());
        response.setEndDate(ms.getEndDate());
        response.setStatus(ms.getStatus());
        response.setPricePaid(ms.getPricePaid());
        response.setFrozenDays(ms.getFrozenDays());
        response.setFrozenSince(ms.getFrozenSince());
        response.setNotes(ms.getNotes());
        response.setCreatedAt(ms.getCreatedAt());

        // Calcular días hasta vencimiento
        long days = ChronoUnit.DAYS.between(LocalDate.now(), ms.getEndDate());
        response.setDaysUntilExpiration(Math.max(days, 0));

        return response;
    }
}