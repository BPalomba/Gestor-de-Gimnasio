package com.gymsaas.modules.member;

import com.gymsaas.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "member_statuses")
@Getter @Setter @NoArgsConstructor
public class MemberStatus extends BaseEntity {


    // ACTIVE, SUSPENDED, CANCELLED
    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean active = true;
}