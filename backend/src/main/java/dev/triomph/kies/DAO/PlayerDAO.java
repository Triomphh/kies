package dev.triomph.kies.DAO;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.triomph.kies.pojo.Player;


public interface PlayerDAO extends JpaRepository<Player, Long> {
    Optional<Player> findByNickname(String nickname);
}