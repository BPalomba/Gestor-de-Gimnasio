package com.gymsaas.modules.membership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
}