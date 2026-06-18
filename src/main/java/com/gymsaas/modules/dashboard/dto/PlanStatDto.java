package com.gymsaas.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlanStatDto {
    private String planName;
    private long   activeMemberships;
    private String percentage;
}