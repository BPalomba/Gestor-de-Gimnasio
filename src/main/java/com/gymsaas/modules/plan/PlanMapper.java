package com.gymsaas.modules.plan;

import com.gymsaas.modules.plan.dto.CreatePlanRequest;
import com.gymsaas.modules.plan.dto.PlanResponse;
import com.gymsaas.modules.plan.dto.UpdatePlanRequest;
import org.springframework.stereotype.Component;

@Component
public class PlanMapper {

    public Plan toEntity(CreatePlanRequest req) {
        Plan plan = new Plan();
        plan.setName(req.getName());
        plan.setDescription(req.getDescription());
        plan.setPrice(req.getPrice());
        plan.setCurrency(req.getCurrency());
        plan.setDurationDays(req.getDurationDays());
        plan.setSessionsPerWeek(req.getSessionsPerWeek());
        plan.setPublicPlan(req.isPublicPlan());
        return plan;
    }

    public PlanResponse toResponse(Plan plan) {
        PlanResponse response = new PlanResponse();
        response.setId(plan.getId());
        response.setName(plan.getName());
        response.setDescription(plan.getDescription());
        response.setPrice(plan.getPrice());
        response.setCurrency(plan.getCurrency());
        response.setDurationDays(plan.getDurationDays());
        response.setSessionsPerWeek(plan.getSessionsPerWeek());
        response.setPublicPlan(plan.isPublicPlan());
        response.setActive(plan.isActive());
        response.setCreatedAt(plan.getCreatedAt());
        return response;
    }

    public void updateEntity(UpdatePlanRequest req, Plan plan) {
        if (req.getName()            != null) plan.setName(req.getName());
        if (req.getDescription()     != null) plan.setDescription(req.getDescription());
        if (req.getPrice()           != null) plan.setPrice(req.getPrice());
        if (req.getDurationDays()    != null) plan.setDurationDays(req.getDurationDays());
        if (req.getSessionsPerWeek() != null) plan.setSessionsPerWeek(req.getSessionsPerWeek());
        if (req.getPublicPlan()      != null) plan.setPublicPlan(req.getPublicPlan());
        if (req.getActive()          != null) plan.setActive(req.getActive());
    }
}