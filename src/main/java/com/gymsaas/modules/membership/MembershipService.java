package com.gymsaas.modules.membership;

import com.gymsaas.modules.member.Member;
import com.gymsaas.modules.member.MemberRepository;
import com.gymsaas.modules.membership.dto.CreateMembershipRequest;
import com.gymsaas.modules.membership.dto.FreezeMembershipRequest;
import com.gymsaas.modules.membership.dto.MembershipResponse;
import com.gymsaas.modules.plan.Plan;
import com.gymsaas.modules.plan.PlanRepository;
import com.gymsaas.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipService {

    private final MembershipRepository membershipRepository;
    private final MemberRepository     memberRepository;
    private final PlanRepository       planRepository;
    private final MembershipMapper     mapper;

    public MembershipResponse findById(UUID gymId, UUID membershipId) {
        return membershipRepository.findByIdAndGymId(membershipId, gymId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new BusinessException(
                        "Membresía no encontrada", HttpStatus.NOT_FOUND));
    }

    public List<MembershipResponse> findByMember(UUID memberId) {
        return membershipRepository
                .findByMemberIdOrderByStartDateDesc(memberId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public MembershipResponse findActiveMembership(UUID memberId) {
        return membershipRepository
                .findByMemberIdAndStatus(memberId, Membership.MembershipStatus.ACTIVE)
                .map(mapper::toResponse)
                .orElseThrow(() -> new BusinessException(
                        "El socio no tiene una membresía activa", HttpStatus.NOT_FOUND));
    }

    public List<MembershipResponse> findExpiring(UUID gymId, int daysAhead) {
        LocalDate from = LocalDate.now();
        LocalDate to   = from.plusDays(daysAhead);
        return membershipRepository.findExpiringBetween(gymId, from, to)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public MembershipResponse create(UUID gymId, CreateMembershipRequest req) {

        // 1. Buscar el socio verificando que pertenece al gym
        Member member = memberRepository.findByIdAndGymId(req.getMemberId(), gymId)
                .orElseThrow(() -> new BusinessException(
                        "Socio no encontrado", HttpStatus.NOT_FOUND));

        // 2. Verificar que el socio no esté cancelado
        if (member.getStatus() == Member.MemberStatus.CANCELLED) {
            throw new BusinessException(
                    "No se puede crear una membresía para un socio cancelado");
        }

        // 3. Verificar que no tenga ya una membresía activa
        boolean hasActive = membershipRepository
                .findByMemberIdAndStatus(member.getId(),
                        Membership.MembershipStatus.ACTIVE)
                .isPresent();
        if (hasActive) {
            throw new BusinessException(
                    "El socio ya tiene una membresía activa. " +
                            "Debe vencer o cancelarse antes de crear una nueva");
        }

        // 4. Buscar el plan verificando que pertenece al gym
        Plan plan = planRepository.findByIdAndGymId(req.getPlanId(), gymId)
                .orElseThrow(() -> new BusinessException(
                        "Plan no encontrado", HttpStatus.NOT_FOUND));

        if (!plan.isActive()) {
            throw new BusinessException(
                    "El plan seleccionado no está disponible");
        }

        // 5. Calcular fechas
        LocalDate startDate = req.getStartDate() != null
                ? req.getStartDate()
                : LocalDate.now();

        // 🔑 Cálculo automático del vencimiento
        LocalDate endDate = startDate.plusDays(plan.getDurationDays());

        // 6. Crear la membresía
        Membership membership = new Membership();
        membership.setGym(member.getGym());
        membership.setMember(member);
        membership.setPlan(plan);
        membership.setStartDate(startDate);
        membership.setEndDate(endDate);
        membership.setPricePaid(plan.getPrice()); // Snapshot del precio actual
        membership.setNotes(req.getNotes());

        // 7. Activar el socio si estaba suspendido
        if (member.getStatus() == Member.MemberStatus.SUSPENDED) {
            member.setStatus(Member.MemberStatus.ACTIVE);
        }

        Membership saved = membershipRepository.save(membership);
        membershipRepository.flush();  // fuerza la escritura inmediata
        return mapper.toResponse(membershipRepository.findById(saved.getId()).orElseThrow());
    }

    @Transactional
    public MembershipResponse freeze(UUID gymId, UUID membershipId,
                                     FreezeMembershipRequest req) {

        Membership ms = membershipRepository.findByIdAndGymId(membershipId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Membresía no encontrada", HttpStatus.NOT_FOUND));

        if (ms.getStatus() != Membership.MembershipStatus.ACTIVE) {
            throw new BusinessException(
                    "Solo se puede congelar una membresía activa");
        }

        if (req.getFrozenSince().isAfter(ms.getEndDate())) {
            throw new BusinessException(
                    "La fecha de congelamiento no puede ser posterior al vencimiento");
        }

        ms.setStatus(Membership.MembershipStatus.FROZEN);
        ms.setFrozenSince(req.getFrozenSince());

        return mapper.toResponse(ms);
    }

    @Transactional
    public MembershipResponse unfreeze(UUID gymId, UUID membershipId) {

        Membership ms = membershipRepository.findByIdAndGymId(membershipId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Membresía no encontrada", HttpStatus.NOT_FOUND));

        if (ms.getStatus() != Membership.MembershipStatus.FROZEN) {
            throw new BusinessException("La membresía no está congelada");
        }

        // Calcular días que estuvo congelada
        long daysFrozen = ChronoUnit.DAYS.between(ms.getFrozenSince(), LocalDate.now());

        // Acumular días congelados y extender el vencimiento
        ms.setFrozenDays(ms.getFrozenDays() + (int) daysFrozen);
        ms.setEndDate(ms.getEndDate().plusDays(daysFrozen));

        // Limpiar el congelamiento
        ms.setFrozenSince(null);
        ms.setStatus(Membership.MembershipStatus.ACTIVE);

        return mapper.toResponse(ms);
    }

    @Transactional
    public void cancel(UUID gymId, UUID membershipId) {

        Membership ms = membershipRepository.findByIdAndGymId(membershipId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Membresía no encontrada", HttpStatus.NOT_FOUND));

        if (ms.getStatus() == Membership.MembershipStatus.EXPIRED) {
            throw new BusinessException("No se puede cancelar una membresía ya vencida");
        }
        if (ms.getStatus() == Membership.MembershipStatus.CANCELLED) {
            throw new BusinessException("La membresía ya está cancelada");
        }

        ms.setStatus(Membership.MembershipStatus.CANCELLED);
        ms.getMember().setStatus(Member.MemberStatus.SUSPENDED);
    }
}