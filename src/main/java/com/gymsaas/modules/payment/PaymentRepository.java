package com.gymsaas.modules.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query("SELECT p FROM Payment p " +
            "WHERE p.gym.id = :gymId " +
            "AND CAST(p.createdAt AS date) BETWEEN :from AND :to")
    Page<Payment> findByGymIdAndCreatedAtBetween(
            @Param("gymId") UUID gymId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            Pageable pageable);

    Optional<Payment> findByIdAndGymId(UUID id, UUID gymId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
            "WHERE p.gym.id = :gymId " +
            "AND p.status = 'COMPLETED' " +
            "AND CAST(p.createdAt AS date) BETWEEN :from AND :to")
    BigDecimal sumAmountByGymAndPeriod(
            @Param("gymId") UUID gymId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    @Query("SELECT p FROM Payment p " +
            "WHERE p.gym.id = :gymId " +
            "AND p.revenueSharePaid = false " +
            "AND p.status = 'COMPLETED' " +
            "ORDER BY p.createdAt ASC")
    List<Payment> findPendingRevenueShare(@Param("gymId") UUID gymId);

    List<Payment> findByMemberIdOrderByCreatedAtDesc(UUID memberId);

    // Revenue share pendiente
    @Query("SELECT COALESCE(SUM(p.revenueShareAmount), 0) FROM Payment p " +
            "WHERE p.gym.id = :gymId " +
            "AND p.status = 'COMPLETED' " +
            "AND p.revenueSharePaid = false")
    BigDecimal sumPendingRevenueShare(@Param("gymId") UUID gymId);

    // Cantidad de pagos en período
    @Query("SELECT COUNT(p) FROM Payment p " +
            "WHERE p.gym.id = :gymId " +
            "AND p.status = 'COMPLETED' " +
            "AND CAST(p.createdAt AS date) BETWEEN :from AND :to")
    long countPaymentsByPeriod(
            @Param("gymId") UUID gymId,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);
}