package com.gymsaas.modules.member;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<Member, UUID> {

    Page<Member> findByGymIdAndStatus(
            UUID gymId,
            MemberStatus status,
            Pageable pageable
    );

    Optional<Member> findByIdAndGymId(UUID id, UUID gymId);

    boolean existsByGymIdAndDni(UUID gymId, String dni);

    long countByGymIdAndStatus(UUID gymId, MemberStatus status);

    @Query("SELECT m FROM Member m WHERE m.gym.id = :gymId " +
            "AND (LOWER(m.firstName) LIKE LOWER(CONCAT('%',:q,'%')) " +
            "OR LOWER(m.lastName) LIKE LOWER(CONCAT('%',:q,'%')) " +
            "OR m.dni LIKE CONCAT('%',:q,'%'))")
    Page<Member> search(@Param("gymId") UUID gymId,
                        @Param("q") String q,
                        Pageable pageable);

    // Contar por estado
    long countByGymIdAndStatusCode(UUID gymId, String statusCode);

    // Socios nuevos del mes
    @Query("SELECT COUNT(m) FROM Member m " +
            "WHERE m.gym.id = :gymId " +
            "AND CAST(m.createdAt AS date) BETWEEN :from AND :to")
    long countNewMembers(
            @Param("gymId") UUID gymId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}