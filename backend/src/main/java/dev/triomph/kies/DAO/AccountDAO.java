package dev.triomph.kies.DAO;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.triomph.kies.pojo.Account;


public interface AccountDAO extends JpaRepository<Account, Long> {
    Optional<Account> findByPlayerPlayerId(Long playerId);
    Optional<Account> findByEmail(String email);
}