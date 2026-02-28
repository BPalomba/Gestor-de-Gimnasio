package com.gymsaas.modules.plan;

import com.gymsaas.modules.gym.Gym;
import com.gymsaas.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
public class Plan extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "gym_id", nullable = false)
    private Gym gym;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    private String currency = "ARS";

    @Column(name = "duration_days", nullable = false)
    private int durationDays;

    @Column(name = "sessions_per_week")
    private Integer sessionsPerWeek;

    @Column(name = "is_public", nullable = false)
    private boolean publicPlan = true;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}