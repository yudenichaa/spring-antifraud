package dev.nightzen.antifraud.persistance;

import dev.nightzen.antifraud.business.entity.StolenCard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StolenCardRepository extends CrudRepository<StolenCard, Long> {
    Optional<StolenCard> findByNumber(String number);
    Iterable<StolenCard> findByOrderByIdAsc();
}
