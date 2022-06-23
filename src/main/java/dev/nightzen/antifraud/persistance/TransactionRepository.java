package dev.nightzen.antifraud.persistance;

import dev.nightzen.antifraud.business.entity.Transaction;
import dev.nightzen.antifraud.constants.WorldRegion;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long> {

    Iterable<Transaction> findByOrderByIdAsc();

    List<Transaction> findByNumberOrderByIdAsc(String number);

    @Query("SELECT COUNT(DISTINCT t.region) FROM Transaction t WHERE t.region <> ?1 AND t.number = ?2 AND t.date BETWEEN ?3 AND ?4")
    Long getTransactionsWithDistinctRegionCount(
            WorldRegion region,
            String number,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("SELECT COUNT(DISTINCT t.ip) FROM Transaction t WHERE t.ip <> ?1 AND t.number = ?2 AND t.date BETWEEN ?3 AND ?4")
    Long getTransactionsWithDistinctIpCount(String ip,
                                            String number,
                                            LocalDateTime start,
                                            LocalDateTime end);
}
