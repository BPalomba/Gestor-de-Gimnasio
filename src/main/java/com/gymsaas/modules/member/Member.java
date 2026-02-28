package com.gymsaas.modules.member;

import com.gymsaas.modules.gym.Gym;
import com.gymsaas.modules.branch.Branch;
import com.gymsaas.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"gym_id", "dni"}))
@Getter
@Setter
@NoArgsConstructor
public class Member extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(length = 20)
    private String dni;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(length = 255)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private MemberStatus status = MemberStatus.ACTIVE;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public enum MemberStatus {
        ACTIVE, SUSPENDED, CANCELLED
    }
}