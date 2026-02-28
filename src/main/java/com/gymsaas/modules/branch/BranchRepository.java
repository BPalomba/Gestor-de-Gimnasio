package com.gymsaas.modules.branch;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BranchRepository extends JpaRepository<Branch, UUID> {

    List<Branch> findByGymIdAndActiveTrue(UUID gymId);

    Optional<Branch> findByIdAndGymId(UUID id, UUID gymId);
}