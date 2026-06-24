package com.gymsaas.modules.membership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    Optional<Membership> findByMemberIdAndStatus(
            UUID memberId,
            Membership.MembershipStatus status
    );

    Optional<Membership> findByIdAndGymId(UUID id, UUID gymId);

    List<Membership> findByMemberIdOrderByStartDateDesc(UUID memberId);

    @Query("SELECT ms FROM Membership ms " +
            "WHERE ms.gym.id = :gymId " +
            "AND ms.status = 'ACTIVE' " +
            "AND ms.endDate BETWEEN :from AND :to " +
            "ORDER BY ms.endDate ASC")
    List<Membership> findExpiringBetween(@Param("gymId") UUID gymId,
                                         @Param("from") LocalDate from,
                                         @Param("to") LocalDate to);

    // buscar membresías vencidas para el cron job
    List<Membership> findByStatusAndEndDateBefore(
            Membership.MembershipStatus status, LocalDate date);

    // Contar por estado
    long countByGymIdAndStatus(UUID gymId, Membership.MembershipStatus status);

    // Contar por vencer en N días
    @Query("SELECT COUNT(ms) FROM Membership ms " +
            "WHERE ms.gym.id = :gymId " +
            "AND ms.status = 'ACTIVE' " +
            "AND ms.endDate BETWEEN :from AND :to")
    long countExpiringBetween(
            @Param("gymId") UUID gymId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    // Top planes por membresías activas
    @Query("SELECT ms.plan.name, COUNT(ms) as total " +
            "FROM Membership ms " +
            "WHERE ms.gym.id = :gymId " +
            "AND ms.status = 'ACTIVE' " +
            "GROUP BY ms.plan.name " +
            "ORDER BY total DESC")
    List<Object[]> findTopPlansByActiveMemberships(
            @Param("gymId") UUID gymId,
            Pageable pageable);
}