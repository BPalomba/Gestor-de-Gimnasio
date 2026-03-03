package com.gymsaas.modules.plan;

import com.gymsaas.modules.gym.Gym;
import com.gymsaas.modules.gym.GymRepository;
import com.gymsaas.modules.plan.dto.CreatePlanRequest;
import com.gymsaas.modules.plan.dto.PlanResponse;
import com.gymsaas.modules.plan.dto.UpdatePlanRequest;
import com.gymsaas.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlanService {

    private final PlanRepository planRepository;
    private final GymRepository  gymRepository;
    private final PlanMapper     mapper;

    // Todos los planes del gym (activos e inactivos) para el panel interno
    public List<PlanResponse> findAll(UUID gymId) {
        return planRepository.findByGymIdAndActiveTrue(gymId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    // Solo los planes públicos y activos para la landing
    public List<PlanResponse> findPublic(UUID gymId) {
        return planRepository.findByGymIdAndActiveTrueAndPublicPlanTrue(gymId)
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    public PlanResponse findById(UUID gymId, UUID planId) {
        return planRepository.findByIdAndGymId(planId, gymId)
                .map(mapper::toResponse)
                .orElseThrow(() -> new BusinessException(
                        "Plan no encontrado", HttpStatus.NOT_FOUND));
    }

    @Transactional
    public PlanResponse create(UUID gymId, CreatePlanRequest req) {

        // Validar nombre duplicado dentro del gym
        if (planRepository.existsByGymIdAndName(gymId, req.getName())) {
            throw new BusinessException(
                    "Ya existe un plan con ese nombre en este gimnasio");
        }

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(() -> new BusinessException(
                        "Gimnasio no encontrado", HttpStatus.NOT_FOUND));

        Plan plan = mapper.toEntity(req);
        plan.setGym(gym);

        return mapper.toResponse(planRepository.save(plan));
    }

    @Transactional
    public PlanResponse update(UUID gymId, UUID planId, UpdatePlanRequest req) {
        Plan plan = planRepository.findByIdAndGymId(planId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Plan no encontrado", HttpStatus.NOT_FOUND));

        // Si cambia el nombre, verificar que no exista otro con ese nombre
        if (req.getName() != null
                && !req.getName().equals(plan.getName())
                && planRepository.existsByGymIdAndName(gymId, req.getName())) {
            throw new BusinessException(
                    "Ya existe un plan con ese nombre en este gimnasio");
        }

        mapper.updateEntity(req, plan);
        return mapper.toResponse(plan);
    }

    @Transactional
    public void deactivate(UUID gymId, UUID planId) {
        Plan plan = planRepository.findByIdAndGymId(planId, gymId)
                .orElseThrow(() -> new BusinessException(
                        "Plan no encontrado", HttpStatus.NOT_FOUND));

        if (!plan.isActive()) {
            throw new BusinessException("El plan ya está desactivado");
        }

        // No eliminamos planes, solo los desactivamos
        // porque pueden tener membresías históricas asociadas
        plan.setActive(false);
    }
}