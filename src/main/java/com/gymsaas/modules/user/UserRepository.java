package com.gymsaas.modules.user;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndGymId(UUID id, UUID gymId);

    boolean existsByEmail(String email);
}