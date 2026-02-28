package com.gymsaas.modules.gym;

import com.gymsaas.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "gyms")
@Getter
@Setter
@NoArgsConstructor
public class Gym extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(unique = true, nullable = false, length = 100)
    private String slug;

    @Column(nullable = false, length = 255)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "revenue_share_pct", precision = 5, scale = 2)
    private BigDecimal revenueSharePct = BigDecimal.TEN;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}