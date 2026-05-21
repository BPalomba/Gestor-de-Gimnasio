package com.gymsaas.modules.role;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<Role, UUID> {

    List<Role> findByGymIdAndActiveTrue(UUID gymId);

    Optional<Role> findByIdAndGymId(UUID id, UUID gymId);

    boolean existsByGymIdAndName(UUID gymId, String name);
}