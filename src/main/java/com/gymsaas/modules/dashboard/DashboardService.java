package com.gymsaas.modules.dashboard;

import com.gymsaas.modules.dashboard.dto.DashboardResponse;
import com.gymsaas.modules.dashboard.dto.PlanStatDto;
import com.gymsaas.modules.member.MemberRepository;
import com.gymsaas.modules.membership.Membership;
import com.gymsaas.modules.membership.MembershipRepository;
import com.gymsaas.modules.payment.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final MemberRepository     memberRepository;
    private final MembershipRepository membershipRepository;
    private final PaymentRepository    paymentRepository;

    public DashboardResponse getDashboard(UUID gymId) {

        LocalDate today     = LocalDate.now();
        LocalDate monthStart = today.withDayOfMonth(1);
        LocalDate prevStart  = monthStart.minusMonths(1);
        LocalDate prevEnd    = monthStart.minusDays(1);

        // ── Socios ──────────────────────────────────────────
        long active    = memberRepository.countByGymIdAndStatusCode(gymId, "ACTIVE");
        long suspended = memberRepository.countByGymIdAndStatusCode(gymId, "SUSPENDED");
        long cancelled = memberRepository.countByGymIdAndStatusCode(gymId, "CANCELLED");
        long newThis   = memberRepository.countNewMembers(gymId, monthStart, today);

        // ── Membresías ───────────────────────────────────────
        long msActive   = membershipRepository.countByGymIdAndStatus(gymId, Membership.MembershipStatus.ACTIVE);
        long msFrozen   = membershipRepository.countByGymIdAndStatus(gymId, Membership.MembershipStatus.FROZEN);
        long expIn7     = membershipRepository.countExpiringBetween(gymId, today, today.plusDays(7));
        long expIn30    = membershipRepository.countExpiringBetween(gymId, today, today.plusDays(30));

        // ── Financiero ───────────────────────────────────────
        BigDecimal revThis  = paymentRepository.sumAmountByGymAndPeriod(gymId, monthStart, today);
        BigDecimal revPrev  = paymentRepository.sumAmountByGymAndPeriod(gymId, prevStart, prevEnd);
        BigDecimal rsPending = paymentRepository.sumPendingRevenueShare(gymId);
        long totalPayments  = paymentRepository.countPaymentsByPeriod(gymId, monthStart, today);

        // ── Top planes ───────────────────────────────────────
        List<Object[]> rawPlans = membershipRepository
                .findTopPlansByActiveMemberships(gymId, (Pageable) PageRequest.of(0, 5));

        List<PlanStatDto> topPlans = rawPlans.stream()
                .map(row -> {
                    String name  = (String) row[0];
                    long   count = (Long) row[1];
                    String pct   = msActive > 0
                            ? BigDecimal.valueOf(count * 100.0 / msActive)
                            .setScale(1, RoundingMode.HALF_UP) + "%"
                            : "0%";
                    return new PlanStatDto(name, count, pct);
                })
                .toList();

        return DashboardResponse.builder()
                .totalMembersActive(active)
                .totalMembersSuspended(suspended)
                .totalMembersCancelled(cancelled)
                .newMembersThisMonth(newThis)
                .totalMembershipsActive(msActive)
                .totalMembershipsFrozen(msFrozen)
                .membershipsExpiringIn7Days(expIn7)
                .membershipsExpiringIn30Days(expIn30)
                .revenueThisMonth(revThis)
                .revenuePreviousMonth(revPrev)
                .revenueSharePending(rsPending)
                .totalPaymentsThisMonth(totalPayments)
                .topPlans(topPlans)
                .build();
    }
}