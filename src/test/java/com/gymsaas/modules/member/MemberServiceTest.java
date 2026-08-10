package com.gymsaas.modules.member;

import com.gymsaas.modules.branch.BranchRepository;
import com.gymsaas.modules.gym.Gym;
import com.gymsaas.modules.gym.GymRepository;
import com.gymsaas.modules.member.dto.CreateMemberRequest;
import com.gymsaas.shared.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MemberService — Tests unitarios")
class MemberServiceTest {

    @Mock private MemberRepository       memberRepository;
    @Mock private MemberStatusRepository memberStatusRepository;
    @Mock private GymRepository          gymRepository;
    @Mock private BranchRepository       branchRepository;
    @Mock private MemberMapper           mapper;

    @InjectMocks private MemberService memberService;

    private UUID gymId;
    private Gym  mockGym;
    private MemberStatus activeStatus;



    @BeforeEach
    void setUp() {

        mockGym = new Gym();
        gymId = mockGym.getId();
        mockGym.setName("Gimnasio Demo");

        activeStatus = new MemberStatus();
        activeStatus.setCode("ACTIVE");
        activeStatus.setActive(true);
    }

    @Test
    @DisplayName("CP04 — Crear socio exitosamente")
    void create_conDatosValidos_retornaSocioCreado() {
        CreateMemberRequest req = new CreateMemberRequest();
        req.setFirstName("María");
        req.setLastName("González");
        req.setDni("28441223");

        Member savedMember = new Member();
        savedMember.setFirstName("María");
        savedMember.setLastName("González");
        savedMember.setStatus(activeStatus);

        when(memberRepository.existsByGymIdAndDni(gymId, "28441223")).thenReturn(false);
        when(gymRepository.findById(gymId)).thenReturn(Optional.of(mockGym));
        when(memberStatusRepository.findByCodeAndActiveTrue("ACTIVE")).thenReturn(Optional.of(activeStatus));
        when(mapper.toEntity(req)).thenReturn(savedMember);
        when(memberRepository.save(any())).thenReturn(savedMember);
        when(mapper.toResponse(savedMember)).thenReturn(null);

        assertThatNoException().isThrownBy(() ->
                memberService.create(gymId, req));

        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("CP05 — Crear socio falla con DNI duplicado")
    void create_conDniDuplicado_lanzaBusinessException() {
        CreateMemberRequest req = new CreateMemberRequest();
        req.setFirstName("Carlos");
        req.setLastName("López");
        req.setDni("28441223");

        when(memberRepository.existsByGymIdAndDni(gymId, "28441223")).thenReturn(true);

        assertThatThrownBy(() -> memberService.create(gymId, req))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("DNI");

        verify(memberRepository, never()).save(any());
    }

    @Test
    @DisplayName("Suspender socio activo cambia estado a SUSPENDED")
    void suspend_conSocioActivo_cambiaEstado() {
        UUID memberId = UUID.randomUUID();
        MemberStatus suspendedStatus = new MemberStatus();
        suspendedStatus.setCode("SUSPENDED");

        Member member = new Member();
        member.setStatus(activeStatus);

        when(memberRepository.findByIdAndGymId(memberId, gymId))
                .thenReturn(Optional.of(member));
        when(memberStatusRepository.findByCodeAndActiveTrue("SUSPENDED"))
                .thenReturn(Optional.of(suspendedStatus));

        memberService.suspend(gymId, memberId);

        assertThat(member.getStatus().getCode()).isEqualTo("SUSPENDED");
    }

    @Test
    @DisplayName("Suspender socio cancelado lanza BusinessException")
    void suspend_conSocioCancelado_lanzaBusinessException() {
        UUID memberId = UUID.randomUUID();
        MemberStatus cancelledStatus = new MemberStatus();
        cancelledStatus.setCode("CANCELLED");

        Member member = new Member();
        member.setStatus(cancelledStatus);

        when(memberRepository.findByIdAndGymId(memberId, gymId))
                .thenReturn(Optional.of(member));

        assertThatThrownBy(() -> memberService.suspend(gymId, memberId))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cancelado");
    }
}