package com.gymsaas.modules.plan.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
public class PlanResponse {

    private UUID       id;
    private String     name;
    private String     description;
    private BigDecimal price;
    private String     currency;
    private int        durationDays;
    private Integer    sessionsPerWeek;
    private boolean    publicPlan;
    private boolean    active;
    private OffsetDateTime createdAt;
}