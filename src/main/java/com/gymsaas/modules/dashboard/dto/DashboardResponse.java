package com.gymsaas.modules.dashboard.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
public class DashboardResponse {

    // Socios
    private long totalMembersActive;
    private long totalMembersSuspended;
    private long totalMembersCancelled;
    private long newMembersThisMonth;

    // Membresías
    private long totalMembershipsActive;
    private long membershipsExpiringIn7Days;
    private long membershipsExpiringIn30Days;
    private long totalMembershipsFrozen;

    // Financiero
    private BigDecimal revenueThisMonth;
    private BigDecimal revenuePreviousMonth;
    private BigDecimal revenueSharePending;
    private long      totalPaymentsThisMonth;

    // Planes
    private List<PlanStatDto> topPlans;
}