package dev.nightzen.antifraud.persistance;

import dev.nightzen.antifraud.business.entity.CardLimits;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardLimitsRepository extends CrudRepository<CardLimits, Long> {
    Optional<CardLimits> findByNumber(String number);
}
