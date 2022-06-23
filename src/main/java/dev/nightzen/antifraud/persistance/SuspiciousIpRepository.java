package dev.nightzen.antifraud.persistance;

import dev.nightzen.antifraud.business.entity.SuspiciousIp;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuspiciousIpRepository extends CrudRepository<SuspiciousIp, Long> {
    Optional<SuspiciousIp> findByIp(String ip);
    Iterable<SuspiciousIp> findByOrderByIdAsc();
}
