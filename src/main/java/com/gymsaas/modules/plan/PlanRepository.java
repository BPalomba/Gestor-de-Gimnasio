package com.gymsaas.modules.plan;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {

    List<Plan> findByGymIdAndActiveTrue(UUID gymId);

    List<Plan> findByGymIdAndActiveTrueAndPublicPlanTrue(UUID gymId);

    Optional<Plan> findByIdAndGymId(UUID id, UUID gymId);

    boolean existsByGymIdAndName(UUID gymId, String name);
}