package com.gymsaas.modules.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("""
    SELECT DISTINCT u
    FROM User u
    LEFT JOIN FETCH u.role r
    LEFT JOIN FETCH r.permissions
    WHERE u.id = :id
""")
    Optional<User> findByIdWithPermissions(UUID id);

    @Query("""
    SELECT u
    FROM User u
    JOIN FETCH u.role r
    WHERE u.id = :id
""")
    Optional<User> findByIdWithRole(UUID id);

    @Query("""
    SELECT u
    FROM User u
    JOIN FETCH u.role r
    JOIN FETCH u.gym g
    WHERE u.id = :id
""")
    Optional<User> findByIdForSecurity(UUID id);

    @Query("""
    SELECT DISTINCT u
    FROM User u
    LEFT JOIN FETCH u.role r
    LEFT JOIN FETCH r.permissions
    LEFT JOIN FETCH u.gym g
    WHERE u.email = :email
""")
    Optional<User> findByEmailForLogin(String email);

    Optional<User> findByEmail(String email);

    Optional<User> findByIdAndGymId(UUID id, UUID gymId);

    List<User> findByGymIdOrderByLastNameAsc(UUID gymId);

    boolean existsByEmail(String email);
}