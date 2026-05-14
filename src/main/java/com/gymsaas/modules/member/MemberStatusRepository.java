package com.gymsaas.modules.member;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface MemberStatusRepository extends JpaRepository<MemberStatus, UUID> {

    Optional<MemberStatus> findByCode(String code);

    Optional<MemberStatus> findByCodeAndActiveTrue(String code);
}