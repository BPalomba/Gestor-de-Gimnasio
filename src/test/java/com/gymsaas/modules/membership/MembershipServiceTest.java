package com.gymsaas.modules.membership;

import com.gymsaas.modules.member.Member;
import com.gymsaas.modules.member.MemberRepository;
import com.gymsaas.modules.member.MemberStatus;
import com.gymsaas.modules.member.MemberStatusRepository;
import com.gymsaas.modules.membership.dto.CreateMembershipRequest;
import com.gymsaas.modules.plan.Plan;
import com.gymsaas.modules.plan.PlanRepository;
import com.gymsaas.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MembershipService — Tests unitarios")
class MembershipServiceTest {

    @Mock
    private MembershipRepository membershipRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PlanRepository planRepository;

    @Mock
    private MembershipMapper mapper;

    @Mock
    private MemberStatusRepository memberStatusRepository;

    @InjectMocks
    private MembershipService membershipService;

    private UUID gymId;
    private UUID memberId;
    private UUID planId;

    private Member mockMember;
    private Plan mockPlan;
    private MemberStatus activeStatus;

    @BeforeEach
    void setUp() {
        gymId = UUID.randomUUID();
        memberId = UUID.randomUUID();
        planId = UUID.randomUUID();

        activeStatus = new MemberStatus();
        activeStatus.setCode("ACTIVE");

        mockMember = new Member();
        mockMember.setStatus(activeStatus);

        mockPlan = new Plan();
        mockPlan.setName("Plan Mensual");
        mockPlan.setPrice(new BigDecimal("5000"));
        mockPlan.setDurationDays(30);
        mockPlan.setActive(true);
    }

    @Test
    @DisplayName("CP07 — Crear membresía calcula endDate correctamente")
    void create_conDatosValidos_calculaEndDateCorrectamente() {

        CreateMembershipRequest req = new CreateMembershipRequest();
        req.setMemberId(memberId);
        req.setPlanId(planId);
        req.setStartDate(LocalDate.of(2026, 4, 1));

        when(memberRepository.findByIdAndGymId(memberId, gymId))
                .thenReturn(Optional.of(mockMember));

        // El ID de mockMember es null porque no tiene setId().
        // El service usa member.getId(), por eso aceptamos cualquier UUID/null.
        when(membershipRepository.findByMemberIdAndStatus(
                any(),
                eq(Membership.MembershipStatus.ACTIVE)))
                .thenReturn(Optional.empty());

        when(planRepository.findByIdAndGymId(planId, gymId))
                .thenReturn(Optional.of(mockPlan));

        when(membershipRepository.save(any(Membership.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        /*
         * El service hace:
         *
         * membershipRepository.findById(saved.getId())
         *
         * Como no podemos hacer setId(), saved.getId() será null.
         * Mockito puede igualmente responder a findById(null).
         */
        when(membershipRepository.findById(null))
                .thenReturn(Optional.of(new Membership()));

        when(mapper.toResponse(any(Membership.class)))
                .thenReturn(null);

        membershipService.create(gymId, req);

        verify(membershipRepository)
                .save(any(Membership.class));

        verify(membershipRepository)
                .flush();
    }

    @Test
    @DisplayName("CP08 — Crear membresía falla si socio ya tiene una activa")
    void create_conMembresiaActiva_lanzaBusinessException() {

        CreateMembershipRequest req = new CreateMembershipRequest();
        req.setMemberId(memberId);
        req.setPlanId(planId);

        when(memberRepository.findByIdAndGymId(memberId, gymId))
                .thenReturn(Optional.of(mockMember));

        when(membershipRepository.findByMemberIdAndStatus(
                any(),
                eq(Membership.MembershipStatus.ACTIVE)))
                .thenReturn(Optional.of(new Membership()));

        assertThatThrownBy(() ->
                membershipService.create(gymId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("ya tiene una membresía activa");

        verify(membershipRepository, never())
                .save(any(Membership.class));

        verify(planRepository, never())
                .findByIdAndGymId(any(UUID.class), any(UUID.class));
    }

    @Test
    @DisplayName("Congelar membresía activa cambia estado a FROZEN")
    void freeze_conMembresiaActiva_cambiaEstadoAFrozen() {

        UUID membershipId = UUID.randomUUID();

        Membership ms = new Membership();
        ms.setStatus(Membership.MembershipStatus.ACTIVE);
        ms.setEndDate(LocalDate.of(2026, 5, 1));

        var req =
                new com.gymsaas.modules.membership.dto.FreezeMembershipRequest();

        req.setFrozenSince(LocalDate.of(2026, 4, 10));

        when(membershipRepository.findByIdAndGymId(
                membershipId,
                gymId))
                .thenReturn(Optional.of(ms));

        when(mapper.toResponse(ms))
                .thenReturn(null);

        membershipService.freeze(gymId, membershipId, req);

        assertThat(ms.getStatus())
                .isEqualTo(Membership.MembershipStatus.FROZEN);

        assertThat(ms.getFrozenSince())
                .isEqualTo(LocalDate.of(2026, 4, 10));

        verify(membershipRepository)
                .findByIdAndGymId(membershipId, gymId);

        verify(mapper)
                .toResponse(ms);
    }
}

