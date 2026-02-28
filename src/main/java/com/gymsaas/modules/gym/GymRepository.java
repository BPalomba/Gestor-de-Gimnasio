package com.gymsaas.modules.gym;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GymRepository extends JpaRepository<Gym, UUID> {

    Optional<Gym> findBySlug(String slug);

    List<Gym> findAllByActiveTrue();

    boolean existsBySlug(String slug);
}