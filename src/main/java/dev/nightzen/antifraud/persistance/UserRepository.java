package dev.nightzen.antifraud.persistance;

import dev.nightzen.antifraud.business.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsernameIgnoreCase(String username);
    Iterable<User> findByOrderByIdAsc();
}
