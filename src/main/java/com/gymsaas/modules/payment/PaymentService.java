package com.gymsaas.modules.payment;

import com.gymsaas.modules.member.Member;
import com.gymsaas.modules.member.MemberRepository;
import com.gymsaas.modules.member.MemberStatus;
import com.gymsaas.modules.member.MemberStatusRepository;
import com.gymsaas.modules.membership.Membership;
import com.gymsaas.modules.membership.MembershipRepository;
import com.gymsaas.modules.payment.dto.CreatePaymentRequest;
import com.gymsaas.modules.payment.dto.PaymentResponse;
import com.gymsaas.modules.payment.dto.PaymentSummaryResponse;
import com.gymsaas.modules.user.User;
import com.gymsaas.modules.user.UserRepository;
import com.gymsaas.shared.exception.BusinessException;
import com.gymsaas.shared.security.GymContextHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository    paymentRepository;
    private final MemberRepository     memberRepository;
    private final MembershipRepository membershipRepository;
    private final UserRepository       userRepository;
    private final PaymentMapper        mapper;
    private final MemberStatusRepository memberStatusRepository;

    public Page<PaymentResponse> findAll(UUID gymId,
                                         LocalDate from,
                                         LocalDate to,
                                         Pageable pageable) {
        return paymentRepository
                .findByGymIdAndCreatedAtBetween(gymId, from, to, pageable)
                .map(mapper::toResponse);
    }

    public PaymentResponse findById(UUID gymId, UUID paymentId) {
        return paymentRepository.findByIdAndGymId(paymentId, gymId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new BusinessException(
                        "Pago no encontrado", HttpStatus.NOT_FOUND));
    }

    public List<PaymentResponse> findByMember(UUID memberId) {
        return paymentRepository
                .findByMemberIdOrderByCreatedAtDesc(memberId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public PaymentSummaryResponse getSummary(UUID gymId,
                                             LocalDate from,
                                             LocalDate to) {
        BigDecimal total = paymentRepository.sumAmountByGymAndPeriod(gymId, from, to);
        List<Payment> payments = paymentRepository
                .findByGymIdAndCreatedAtBetween(gymId, from, to, Pageable.unpaged())
                .getContent();

        BigDecimal revenueShare = payments.stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.COMPLETED)
                .map(Payment::getRevenueShareAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long count = payments.stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.COMPLETED)
                .count();

        BigDecimal average = count > 0
                ? total.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new PaymentSummaryResponse(total, revenueShare, count, average);
    }

    @Transactional
    public PaymentResponse create(UUID gymId, CreatePaymentRequest req) {

        // 1. Validar socio
        Member member = memberRepository.findByIdAndGymId(req.getMemberId(), gymId)
                .orElseThrow(() -> new BusinessException(
                        "Socio no encontrado", HttpStatus.NOT_FOUND));

        // 2. Validar membresía
        Membership membership = membershipRepository
                .findByIdAndGymId(req.getMembershipId(), gymId)
                .orElseThrow(() -> new BusinessException(
                        "Membresía no encontrada", HttpStatus.NOT_FOUND));

        // 3. Verificar que la membresía pertenece al socio
        if (!membership.getMember().getId().equals(member.getId())) {
            throw new BusinessException(
                    "La membresía no pertenece al socio indicado");
        }

        // 4. Verificar que no haya un pago completado para esta membresía
        // (evita cobros duplicados)
        boolean alreadyPaid = paymentRepository
                .findByMemberIdOrderByCreatedAtDesc(member.getId())
                .stream()
                .anyMatch(p -> p.getMembership().getId().equals(membership.getId())
                        && p.getStatus() == Payment.PaymentStatus.COMPLETED);

        if (alreadyPaid) {
            throw new BusinessException(
                    "Esta membresía ya tiene un pago completado registrado");
        }

        // 5. Calcular revenue share automáticamente
        BigDecimal revenueSharePct = member.getGym().getRevenueSharePct();
        BigDecimal revenueShareAmount = req.getAmount()
                .multiply(revenueSharePct)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        // 6. Obtener el usuario que registra el pago desde el token JWT
        String userId = SecurityContextHolder.getContext()
                .getAuthentication().getName();
        User registeredBy = userRepository.findById(UUID.fromString(userId))
                .orElse(null);

        // 7. Construir el pago
        Payment payment = new Payment();
        payment.setGym(member.getGym());
        payment.setMember(member);
        payment.setMembership(membership);
        payment.setAmount(req.getAmount());
        payment.setCurrency(membership.getPlan().getCurrency());
        payment.setPaymentMethod(req.getPaymentMethod());
        payment.setStatus(Payment.PaymentStatus.COMPLETED);
        payment.setReferenceCode(req.getReferenceCode());
        payment.setRevenueShareAmount(revenueShareAmount);
        payment.setRegisteredBy(registeredBy);
        payment.setNotes(req.getNotes());

        // 8. Activar el socio si estaba suspendido
        if (member.getStatus().getCode().equals("SUSPENDED")) {
            MemberStatus activeStatus = memberStatusRepository
                    .findByCodeAndActiveTrue("ACTIVE")
                    .orElseThrow(() -> new BusinessException("Estado ACTIVE no encontrado"));
            member.setStatus(activeStatus);
        }

        Payment saved = paymentRepository.save(payment);
        paymentRepository.flush();
        return mapper.toResponse(
                paymentRepository.findById(saved.getId()).orElseThrow());
    }

    @Transactional
    public PaymentResponse refund(UUID gymId, UUID paymentId) {
        Payment payment = paymentRepository.findByIdAndGymId(paymentId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Pago no encontrado", HttpStatus.NOT_FOUND));

        if (payment.getStatus() != Payment.PaymentStatus.COMPLETED) {
            throw new BusinessException(
                    "Solo se pueden reembolsar pagos completados");
        }

        if (payment.isRevenueSharePaid()) {
            throw new BusinessException(
                    "No se puede reembolsar un pago cuyo revenue share ya fue liquidado");
        }

        payment.setStatus(Payment.PaymentStatus.REFUNDED);
        return mapper.toResponse(payment);
    }
}