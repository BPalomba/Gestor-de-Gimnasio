package com.gymsaas.modules.member;

import com.gymsaas.modules.branch.Branch;
import com.gymsaas.modules.branch.BranchRepository;
import com.gymsaas.modules.gym.Gym;
import com.gymsaas.modules.gym.GymRepository;
import com.gymsaas.modules.member.dto.CreateMemberRequest;
import com.gymsaas.modules.member.dto.MemberResponse;
import com.gymsaas.modules.member.dto.UpdateMemberRequest;
import com.gymsaas.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository  memberRepository;
    private final GymRepository     gymRepository;
    private final BranchRepository  branchRepository;
    private final MemberMapper      mapper;

    public Page<MemberResponse> findAll(UUID gymId,
                                        Member.MemberStatus status,
                                        Pageable pageable) {
        return memberRepository
                .findByGymIdAndStatus(gymId, status, pageable)
                .map(mapper::toResponse);
    }

    public Page<MemberResponse> search(UUID gymId, String q, Pageable pageable) {
        return memberRepository
                .search(gymId, q, pageable)
                .map(mapper::toResponse);
    }

    public MemberResponse findById(UUID gymId, UUID memberId) {
        return memberRepository
                .findByIdAndGymId(memberId, gymId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new BusinessException(
                        "Socio no encontrado", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public MemberResponse create(UUID gymId, CreateMemberRequest req) {

        // Validar DNI duplicado dentro del gym
        if (req.getDni() != null &&
                memberRepository.existsByGymIdAndDni(gymId, req.getDni())) {
            throw new BusinessException(
                    "Ya existe un socio con ese DNI en este gimnasio");
        }

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new BusinessException(
                        "Gimnasio no encontrado", HttpStatus.NOT_FOUND));

        Member member = mapper.toEntity(req);
        member.setGym(gym);

        // Asignar sucursal si se proporcionó
        if (req.getBranchId() != null) {
            Branch branch = branchRepository
                    .findByIdAndGymId(req.getBranchId(), gymId)
                    .orElseThrow(() -> new BusinessException(
                            "Sucursal no encontrada", HttpStatus.NOT_FOUND));
            member.setBranch(branch);
        }

        return mapper.toResponse(memberRepository.save(member));
    }

    @Transactional
    public MemberResponse update(UUID gymId, UUID memberId,
                                 UpdateMemberRequest req) {
        Member member = memberRepository
                .findByIdAndGymId(memberId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Socio no encontrado", HttpStatus.NOT_FOUND));

        // Cambio de sucursal
        if (req.getBranchId() != null) {
            Branch branch = branchRepository
                    .findByIdAndGymId(req.getBranchId(), gymId)
                    .orElseThrow(() -> new BusinessException(
                            "Sucursal no encontrada", HttpStatus.NOT_FOUND));
            member.setBranch(branch);
        }

        mapper.updateEntity(req, member);
        return mapper.toResponse(member);
    }

    @Transactional
    public void suspend(UUID gymId, UUID memberId) {
        Member member = memberRepository
                .findByIdAndGymId(memberId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Socio no encontrado", HttpStatus.NOT_FOUND));

        if (member.getStatus() == Member.MemberStatus.CANCELLED) {
            throw new BusinessException("No se puede suspender un socio cancelado");
        }
        if (member.getStatus() == Member.MemberStatus.SUSPENDED) {
            throw new BusinessException("El socio ya está suspendido");
        }

        member.setStatus(Member.MemberStatus.SUSPENDED);
    }

    @Transactional
    public void reactivate(UUID gymId, UUID memberId) {
        Member member = memberRepository
                .findByIdAndGymId(memberId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Socio no encontrado", HttpStatus.NOT_FOUND));

        if (member.getStatus() == Member.MemberStatus.CANCELLED) {
            throw new BusinessException(
                    "No se puede reactivar un socio cancelado, " +
                            "debe registrarse nuevamente");
        }

        member.setStatus(Member.MemberStatus.ACTIVE);
    }

    @Transactional
    public void cancel(UUID gymId, UUID memberId) {
        Member member = memberRepository
                .findByIdAndGymId(memberId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Socio no encontrado", HttpStatus.NOT_FOUND));

        if (member.getStatus() == Member.MemberStatus.CANCELLED) {
            throw new BusinessException("El socio ya está cancelado");
        }

        member.setStatus(Member.MemberStatus.CANCELLED);
    }
}