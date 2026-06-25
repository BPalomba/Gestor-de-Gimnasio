package com.gymsaas.modules.membership;

import com.gymsaas.modules.member.MemberStatus;
import com.gymsaas.modules.member.MemberStatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MembershipExpirationJob {

    private final MembershipRepository membershipRepository;
    private final MemberStatusRepository memberStatusRepository;

    // En el properties se pone el intervalo
    @Scheduled(cron = "${app.cron.expiration}")
    @Transactional
    public void expireMemberships() {
        LocalDate today = LocalDate.now();

        log.info("[MembershipExpirationJob] Iniciando proceso de vencimientos para {}", today);

        // Buscar membresías ACTIVE cuyo endDate ya pasó
        List<Membership> expired = membershipRepository
                .findByStatusAndEndDateBefore(
                        Membership.MembershipStatus.ACTIVE,
                        today);

        if (expired.isEmpty()) {
            log.info("[MembershipExpirationJob] No hay membresías vencidas hoy");
            return;
        }

        // Buscar el estado SUSPENDED para los socios
        MemberStatus suspended = memberStatusRepository
                .findByCodeAndActiveTrue("SUSPENDED")
                .orElseThrow(() -> new RuntimeException(
                        "Estado SUSPENDED no encontrado en la base de datos"));

        // Marcar cada membresía como EXPIRED y suspender al socio
        expired.forEach(ms -> {
            ms.setStatus(Membership.MembershipStatus.EXPIRED);
            ms.getMember().setStatus(suspended);
        });

        log.info("[MembershipExpirationJob] {} membresías marcadas como EXPIRED", expired.size());
    }

    // Job de prueba — se ejecuta cada 1 minuto
    // Comentar o eliminar antes de producción
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void expireMembershipsTest() {
        expireMemberships();
    }
}