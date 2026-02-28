package com.gymsaas.modules.payment;

import com.gymsaas.modules.gym.Gym;
import com.gymsaas.modules.member.Member;
import com.gymsaas.modules.membership.Membership;
import com.gymsaas.modules.user.User;
import com.gymsaas.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membership_id", nullable = false)
    private Membership membership;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency = "ARS";

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 20)
    private PaymentMethod paymentMethod;

    @Column(name = "payment_date", nullable = false)
    private LocalDate paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status = PaymentStatus.COMPLETED;

    @Column(name = "reference_code", length = 100)
    private String referenceCode;

    @Column(name = "revenue_share_amount", precision = 10, scale = 2)
    private BigDecimal revenueShareAmount;

    @Column(name = "revenue_share_paid", nullable = false)
    private boolean revenueSharePaid = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registered_by")
    private User registeredBy;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public enum PaymentMethod {
        CASH, CARD, TRANSFER, MP, OTHER
    }

    public enum PaymentStatus {
        PENDING, COMPLETED, REFUNDED, CANCELLED
    }
}